package com.IDP.FVN.journey.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class JourneyEventProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private static final String TOPIC = "journey-events"; // The topic we created earlier

    public JourneyEventProducer(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishJourneyStarted(String sessionId, String vehicleType) {
        try {
            // Create a clean JSON structure for the event
            Map<String, Object> eventPayload = Map.of(
                    "eventType", "JOURNEY_STARTED",
                    "sessionId", sessionId,
                    "vehicleType", vehicleType,
                    "timestamp", Instant.now().toString()
            );

            // Convert to JSON string
            String message = objectMapper.writeValueAsString(eventPayload);

            // Broadcast it! (Topic, Routing Key, Message)
            kafkaTemplate.send(TOPIC, sessionId, message);
            System.out.println("✅ Broadcasted: " + message);

        } catch (Exception e) {
            System.err.println("❌ Failed to broadcast start event: " + e.getMessage());
        }
    }

    public void publishJourneyEnded(String sessionId) {
        try {
            Map<String, Object> eventPayload = Map.of(
                    "eventType", "JOURNEY_ENDED",
                    "sessionId", sessionId,
                    "timestamp", Instant.now().toString()
            );

            String message = objectMapper.writeValueAsString(eventPayload);
            kafkaTemplate.send(TOPIC, sessionId, message);
            System.out.println("✅ Broadcasted: " + message);

        } catch (Exception e) {
            System.err.println("❌ Failed to broadcast end event: " + e.getMessage());
        }
    }
}