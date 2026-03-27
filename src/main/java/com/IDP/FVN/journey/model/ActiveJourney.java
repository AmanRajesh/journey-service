package com.IDP.FVN.journey.model;

import java.time.Instant;

// We use a Record here because it is immutable and perfect for temporary session data
public record ActiveJourney(
        String sessionId,
        String vehicleType,
        String destination,
        Instant startTime
) {}
