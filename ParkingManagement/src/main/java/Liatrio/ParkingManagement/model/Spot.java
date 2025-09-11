package Liatrio.ParkingManagement.model;

import java.time.Instant;
import java.util.UUID;

public record Spot(
        UUID id,
        int floor,
        String bay,          // e.g., "A", "B"
        String spotNumber,   // e.g., "A-12"
        SpotSize size,       // COMPACT, STANDARD, OVERSIZED
        boolean evCapable,   // true if has EV charging
        Status status,       // AVAILABLE, OCCUPIED
        Instant lastUpdated
) {}
