package com.IDP.FVN.journey.service;

import com.IDP.FVN.journey.dto.JourneyResponse;
import com.IDP.FVN.journey.dto.JourneyStartRequest;
import com.IDP.FVN.journey.messaging.JourneyEventProducer;
import com.IDP.FVN.journey.model.ActiveJourney;
import com.IDP.FVN.journey.model.JourneyEntity; // Make sure you created this record!
import com.IDP.FVN.journey.repository.JourneySummaryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import javax.crypto.SecretKey;
import java.util.Date;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
public class JourneyServiceImpl implements JourneyService {

    // 1. All variables declared exactly once
    private final JourneyEventProducer eventProducer;
    private final ReactiveRedisTemplate<String, ActiveJourney> redisTemplate;
    private final JourneySummaryRepository summaryRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    // 2. ONE unified constructor for Spring to inject everything
    public JourneyServiceImpl(JourneyEventProducer eventProducer,
                              ReactiveRedisTemplate<String, ActiveJourney> redisTemplate,
                              JourneySummaryRepository summaryRepository,
                              KafkaTemplate<String, String> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.eventProducer = eventProducer;
        this.redisTemplate = redisTemplate;
        this.summaryRepository = summaryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }
    @Value("${jwt.secret}")
    private String jwtSecret;

    // 2. Helper method to generate the token
    private String generateToken(String sessionId, String vehicleType) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
        return Jwts.builder()
                .subject(sessionId)                      // The core identity
                .claim("vehicleType", vehicleType)       // Extra metadata
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 hours
                .signWith(key)
                .compact();
    }

    @Override
    public Mono<JourneyResponse> startJourney(JourneyStartRequest request) {
        String sessionId = UUID.randomUUID().toString();
        String token = generateToken(sessionId, request.vehicleType());
        ActiveJourney activeJourney = new ActiveJourney(
                sessionId,
                request.vehicleType(),
                request.destination(),
                Instant.now()
        );

        // Save to Redis with a 24-hour expiration
        return redisTemplate.opsForValue()
                .set("journey:" + sessionId, activeJourney, Duration.ofHours(24))
                .doOnSuccess(saved -> {
                    // Publish the Kafka event ONLY if Redis save was successful
                    eventProducer.publishJourneyStarted(sessionId, request.vehicleType());
                })
                .thenReturn(new JourneyResponse(sessionId, token,"STARTED", "Journey successfully initiated."));
    }

    @Override
    public Mono<JourneyResponse> endJourney(String sessionId) {
        String redisKey = "journey:" + sessionId;

        // STEP 1: Fetch active details from Redis
        return redisTemplate.opsForValue().get(redisKey)
                .switchIfEmpty(Mono.error(new RuntimeException("Error: Journey " + sessionId + " not found.")))
                .flatMap(activeJourney -> {

                    // STEP 2: Prepare the Postgres record
                    JourneyEntity permanentRecord = new JourneyEntity(
                            activeJourney.sessionId(),
                            activeJourney.vehicleType(),
                            activeJourney.destination(),
                            activeJourney.startTime(),
                            Instant.now() // The exact moment it ended
                    );

                    // STEP 3: Force an INSERT into Postgres (Bypasses the .save() update bug)
                    return summaryRepository.insertJourney(
                                    permanentRecord.sessionId(),
                                    permanentRecord.vehicleType(),
                                    permanentRecord.destination(),
                                    permanentRecord.startTime(),
                                    permanentRecord.endTime()
                            )

                            // STEP 4: Delete from Redis ONLY IF Postgres insert was successful
                            .flatMap(rowsAffected -> redisTemplate.opsForValue().delete(redisKey))

                            // STEP 5: Broadcast to Kafka using your custom producer
                            .doOnSuccess(deleted -> {
                                eventProducer.publishJourneyEnded(sessionId, activeJourney.vehicleType());
                            })

                            // STEP 6: Return your clean JourneyResponse
                            .thenReturn(new JourneyResponse(sessionId,null, "ENDED", "Journey archived to Postgres and terminated."));
                });
    }
}