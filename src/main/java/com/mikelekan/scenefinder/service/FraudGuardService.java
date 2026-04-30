package com.mikelekan.scenefinder.service;

import tools.jackson.databind.ObjectMapper;
import com.mikelekan.scenefinder.dto.LocationPingDTO;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FraudGuardService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = "scenefinder-pings", groupId = "fraud-monitor-group")
    public void monitorLocation(String message) {
        try {
            LocationPingDTO ping = objectMapper.readValue(message, LocationPingDTO.class);

            System.out.println("🔍 Analyzing ping from: " + ping.userId() +
                    " at: "    + ping.location() +
                    " ("       + ping.distanceMiles() + " miles away)");

            if (!ping.isLocal() && ping.distanceMiles() > 500) {
                System.err.println("🚨 FRAUD ALERT: Impossible travel detected!" +
                        " User " + ping.userId() +
                        " pinged from " + ping.location() +
                        " which is " + ping.distanceMiles() + " miles away!");
            } else if (!ping.isLocal()) {
                System.out.println("⚠️  WARNING: User outside local area - " +
                        ping.distanceMiles() + " miles from home base");
            } else {
                System.out.println("✅ Normal activity - user is local");
            }

        } catch (Exception e) {
            System.err.println("❌ Error processing message: " + e.getMessage());
        }
    }
}