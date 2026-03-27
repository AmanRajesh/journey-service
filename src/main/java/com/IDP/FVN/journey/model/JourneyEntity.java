package com.IDP.FVN.journey.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import java.time.Instant;

@Table("journeys")
public record JourneyEntity(
        @Id String sessionId,
        String vehicleType,
        String destination,
        Instant startTime,
        Instant endTime
) {}