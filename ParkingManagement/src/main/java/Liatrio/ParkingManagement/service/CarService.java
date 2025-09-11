package Liatrio.ParkingManagement.service;

import Liatrio.ParkingManagement.dto.CheckInRequest;
import Liatrio.ParkingManagement.dto.ReceiptResponse;
import Liatrio.ParkingManagement.exception.BadRequestException;
import Liatrio.ParkingManagement.exception.ConflictException;
import Liatrio.ParkingManagement.exception.NotFoundException;
import Liatrio.ParkingManagement.model.Car;
import Liatrio.ParkingManagement.model.Spot;
import Liatrio.ParkingManagement.model.SpotSize;
import Liatrio.ParkingManagement.model.Status;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CarService {

    private final ConcurrentHashMap<UUID, Spot> spotStore;
    private final ConcurrentHashMap<String, Car> carStore;
    private final RatePlan ratePlan;

    public CarService(ConcurrentHashMap<UUID, Spot> spotStore,
                      ConcurrentHashMap<String, Car> carStore) {
        this.spotStore = spotStore;
        this.carStore = carStore;
        this.ratePlan = new RatePlan(new BigDecimal("3.00"), true); // $3/hr, round up
    }

    public Optional<Car> lookupActiveByPlate(String plate) {
        Car c = carStore.get(normalize(plate));
        return Optional.ofNullable(c);
    }

    public Car checkIn(CheckInRequest req) {
        if (req == null || req.getLicensePlate() == null || req.getLicensePlate().isBlank()) {
            throw new BadRequestException("licensePlate is required");
        }
        String plate = normalize(req.getLicensePlate());
        if (carStore.containsKey(plate)) {
            throw new ConflictException("Car is already checked in: " + plate);
        }
        SpotSize carSize = req.getCarSize() != null ? req.getCarSize() : SpotSize.STANDARD;

        // If a specific spot is requested, validate it; else find first compatible available spot.
        UUID chosenSpotId;
        if (req.getRequestedSpotId() != null && !req.getRequestedSpotId().isBlank()) {
            chosenSpotId = UUID.fromString(req.getRequestedSpotId());
            Spot s = spotStore.get(chosenSpotId);
            if (s == null) throw new NotFoundException("Requested spot not found: " + req.getRequestedSpotId());
            if (s.status() != Status.AVAILABLE) throw new ConflictException("Requested spot is not available");
            if (!isCompatible(carSize, s.size())) throw new ConflictException("Requested spot not compatible with car size");
            occupySpotAtomically(chosenSpotId);
        } else {
            chosenSpotId = findAndOccupyFirstCompatibleSpot(carSize)
                    .orElseThrow(() -> new ConflictException("No compatible available spots"));
        }

        Car assigned = new Car(plate, carSize, chosenSpotId, Instant.now());
        carStore.put(plate, assigned);
        return assigned;
    }

    public ReceiptResponse checkOut(String plateRaw) {
        String plate = normalize(plateRaw);
        Car active = carStore.remove(plate);
        if (active == null) throw new NotFoundException("No active assignment for plate: " + plate);

        // Free the spot atomically
        freeSpotAtomically(active.assignedSpotId());

        Instant out = Instant.now();
        long minutes = Math.max(1, Duration.between(active.checkInAt(), out).toMinutes());
        BigDecimal amount = ratePlan.priceForMinutes(minutes);

        ReceiptResponse r = new ReceiptResponse();
        r.setLicensePlate(plate);
        r.setSpotId(active.assignedSpotId().toString());
        r.setCheckInAt(active.checkInAt());
        r.setCheckOutAt(out);
        r.setBillableMinutes(minutes);
        r.setAmount(amount);
        return r;
    }

    // ----- internals -----

    private Optional<UUID> findAndOccupyFirstCompatibleSpot(SpotSize carSize) {
        // two-phase: pick candidate, then CAS via compute
        return spotStore.values().stream()
                .filter(s -> s.status() == Status.AVAILABLE && isCompatible(carSize, s.size()))
                .findFirst()
                .map(Spot::id)
                .flatMap(this::tryOccupy);
    }

    private Optional<UUID> tryOccupy(UUID spotId) {
        Spot after = occupySpotAtomically(spotId);
        return after != null ? Optional.of(spotId) : Optional.empty();
    }

    private Spot occupySpotAtomically(UUID spotId) {
        return spotStore.compute(spotId, (id, cur) -> {
            if (cur == null) return null;
            if (cur.status() != Status.AVAILABLE) return cur; // no change
            return new Spot(cur.id(), cur.floor(), cur.bay(), cur.spotNumber(), cur.size(), cur.evCapable(),
                    Status.OCCUPIED, Instant.now());
        });
    }

    private void freeSpotAtomically(UUID spotId) {
        spotStore.compute(spotId, (id, cur) -> {
            if (cur == null) return null;
            if (cur.status() == Status.AVAILABLE) return cur;
            return new Spot(cur.id(), cur.floor(), cur.bay(), cur.spotNumber(), cur.size(), cur.evCapable(),
                    Status.AVAILABLE, Instant.now());
        });
    }

    private static boolean isCompatible(SpotSize car, SpotSize spot) {
        // COMPACT fits only COMPACT spots? Typically: smaller car can fit larger spot.
        // We'll enforce: carSize <= spotSize (COMPACT <= STANDARD <= OVERSIZED)
        return ordinal(car) <= ordinal(spot);
    }

    private static int ordinal(SpotSize s) {
        return switch (s) {
            case COMPACT -> 0;
            case STANDARD -> 1;
            case OVERSIZED -> 2;
        };
    }

    private static String normalize(String plate) {
        return plate.trim().toUpperCase();
    }

    // simple hourly rate plan
    public static final class RatePlan {
        private final BigDecimal hourly;
        private final boolean roundUpHours;

        public RatePlan(BigDecimal hourly, boolean roundUpHours) {
            this.hourly = hourly;
            this.roundUpHours = roundUpHours;
        }

        public BigDecimal priceForMinutes(long minutes) {
            if (minutes <= 0) return BigDecimal.ZERO;
            if (roundUpHours) {
                long hours = (minutes + 59) / 60; // ceil
                return hourly.multiply(BigDecimal.valueOf(hours)).setScale(2, RoundingMode.HALF_UP);
            } else {
                BigDecimal hrs = BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 4, RoundingMode.HALF_UP);
                return hourly.multiply(hrs).setScale(2, RoundingMode.HALF_UP);
            }
        }
    }
}
