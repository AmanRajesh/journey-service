package com.IDP.FVN.journey.controller;

import com.IDP.FVN.journey.dto.JourneyStartRequest; // Using your exact DTO name
import com.IDP.FVN.journey.dto.JourneyResponse;
import com.IDP.FVN.journey.service.JourneyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/journeys")

public class JourneyController {

    private final JourneyService journeyService;

    public JourneyController(JourneyService journeyService) {
        this.journeyService = journeyService;
    }

    @PostMapping("/start")
    public Mono<JourneyResponse> startJourney(@Valid @RequestBody JourneyStartRequest request) {
        // EXACT MATCH: Passing the whole object straight to your service method
        return journeyService.startJourney(request);
    }

    @PostMapping("/{sessionId}/end")
    public Mono<JourneyResponse> endJourney(@PathVariable String sessionId) {
        return journeyService.endJourney(sessionId);
    }

    // (Keep your @GetMapping methods here if you have them for the dashboard!)
}