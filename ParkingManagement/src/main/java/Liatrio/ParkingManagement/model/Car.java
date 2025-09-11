package Liatrio.ParkingManagement.model;

import java.time.Instant;
import java.util.UUID;

public record Car(
        String licensePlate,
        SpotSize carSize,     // match against spot size
        UUID assignedSpotId,
        Instant checkInAt
) {}
