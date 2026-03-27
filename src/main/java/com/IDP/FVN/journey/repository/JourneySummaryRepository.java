package com.IDP.FVN.journey.repository;

import com.IDP.FVN.journey.model.JourneyEntity; // Your Postgres record/entity
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Repository
public interface JourneySummaryRepository extends ReactiveCrudRepository<JourneyEntity, String> {
    @Modifying
    @Query("INSERT INTO journeys (session_id, vehicle_type, destination, start_time, end_time) VALUES (:sessionId, :vehicleType, :destination, :startTime, :endTime)")
    Mono<Integer> insertJourney(String sessionId, String vehicleType, String destination, Instant startTime, Instant endTime);
}