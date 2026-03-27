package com.IDP.FVN.journey.service;

import com.IDP.FVN.journey.dto.JourneyStartRequest;
import com.IDP.FVN.journey.dto.JourneyResponse;
import reactor.core.publisher.Mono;

public interface JourneyService {
    Mono<JourneyResponse> startJourney(JourneyStartRequest request);
    Mono<JourneyResponse> endJourney(String sessionId);
}
