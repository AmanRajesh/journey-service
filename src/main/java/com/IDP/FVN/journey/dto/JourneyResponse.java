package com.IDP.FVN.journey.dto;


public record JourneyResponse(
        String sessionId,
        String status,
        String message
) {}