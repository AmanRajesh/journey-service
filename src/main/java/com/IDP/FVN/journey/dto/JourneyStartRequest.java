package com.IDP.FVN.journey.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record JourneyStartRequest(
        @NotBlank(message = "Vehicle type cannot be empty or null.")
        String vehicleType,

        @NotBlank(message = "Destination is required to start a journey.")
        @Size(min = 2, message = "Destination must be at least 2 characters long.")
        String destination
) {}
