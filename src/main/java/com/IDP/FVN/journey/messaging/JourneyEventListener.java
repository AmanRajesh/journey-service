package com.IDP.FVN.journey.messaging;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class JourneyEventListener {

    // This annotation tells Spring to constantly listen to this exact topic
    @KafkaListener(topics = "journey-events", groupId = "fvn-group")
    public void consumeJourneyEvent(String message) {

        // In a fully finished app, this is where you would write code to
        // trigger a push notification, calculate billing, or update a dashboard!

        System.out.println("\n🎧 [KAFKA LISTENER TRIGGERED] \nIncoming Event: " + message + "\n");
    }
}