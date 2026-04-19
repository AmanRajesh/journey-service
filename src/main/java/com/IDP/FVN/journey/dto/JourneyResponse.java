package com.IDP.FVN.journey.dto;


public record JourneyResponse(
        String sessionId,
        String token,
        String status,
        String message
) {}