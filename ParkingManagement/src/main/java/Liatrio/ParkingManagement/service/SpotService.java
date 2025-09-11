package Liatrio.ParkingManagement.service;

import Liatrio.ParkingManagement.dto.CreateSpotRequest;
import Liatrio.ParkingManagement.dto.UpdateSpotRequest;
import Liatrio.ParkingManagement.dto.UsageStatsResponse;
import Liatrio.ParkingManagement.exception.NotFoundException;
import Liatrio.ParkingManagement.model.Spot;
import Liatrio.ParkingManagement.model.SpotSize;
import Liatrio.ParkingManagement.model.Status;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

@Service
public class SpotService {

    private final ConcurrentHashMap<UUID, Spot> spotStore;

    public SpotService(ConcurrentHashMap<UUID, Spot> spotStore) {
        this.spotStore = spotStore;
    }

    public Spot createSpot(CreateSpotRequest req) {
        UUID id = UUID.randomUUID();
        Spot spot = new Spot(
                id,
                req.getFloor(),
                req.getBay(),
                req.getSpotNumber(),
                req.getSize() != null ? req.getSize() : SpotSize.STANDARD,
                req.getEvCapable() != null && req.getEvCapable(),
                Status.AVAILABLE,
                Instant.now()
        );
        spotStore.put(id, spot);
        return spot;
    }

    public List<Spot> getAll(Status status, Integer floor, String bay, SpotSize size, Boolean evCapable) {
        Stream<Spot> s = spotStore.values().stream();
        if (status != null) s = s.filter(x -> x.status() == status);
        if (floor != null) s = s.filter(x -> x.floor() == floor);
        if (bay != null && !bay.isBlank()) s = s.filter(x -> bay.equalsIgnoreCase(x.bay()));
        if (size != null) s = s.filter(x -> x.size() == size);
        if (evCapable != null) s = s.filter(x -> x.evCapable() == evCapable);
        return s.toList();
    }

    public Spot getById(String id) {
        Spot found = spotStore.get(UUID.fromString(id));
        if (found == null) throw new NotFoundException("Spot not found: " + id);
        return found;
    }

    public Spot updateInfo(String id, UpdateSpotRequest req) {
        UUID spotId = UUID.fromString(id);
        return spotStore.compute(spotId, (key, cur) -> {
            if (cur == null) throw new NotFoundException("Spot not found: " + id);
            int floor = (req.getFloor() != null) ? req.getFloor() : cur.floor();
            String bay = (req.getBay() != null) ? req.getBay() : cur.bay();
            String spotNumber = (req.getSpotNumber() != null) ? req.getSpotNumber() : cur.spotNumber();
            SpotSize size = (req.getSize() != null) ? req.getSize() : cur.size();
            boolean evCapable = (req.getEvCapable() != null) ? req.getEvCapable() : cur.evCapable();

            return new Spot(cur.id(), floor, bay, spotNumber, size, evCapable, cur.status(), Instant.now());
        });
    }

    public Spot updateStatus(String id, Status status) {
        UUID spotId = UUID.fromString(id);
        return spotStore.compute(spotId, (key, cur) -> {
            if (cur == null) throw new NotFoundException("Spot not found: " + id);
            if (cur.status() == status) return cur;
            return new Spot(cur.id(), cur.floor(), cur.bay(), cur.spotNumber(), cur.size(), cur.evCapable(), status, Instant.now());
        });
    }

    public UsageStatsResponse getUsageStats() {
        return getUsageStats(null, null);
    }

    /** New filtered usage stats: restrict by floor and/or bay if provided. */
    public UsageStatsResponse getUsageStats(Integer floor, String bay) {
        int total = 0;
        int occupied = 0;

        for (Spot s : spotStore.values()) {
            if (floor != null && s.floor() != floor) continue;
            if (bay != null && !bay.isBlank() && !bay.equalsIgnoreCase(s.bay())) continue;

            total++;
            if (s.status() == Status.OCCUPIED) {
                occupied++;
            }
        }
        int available = total - occupied;
        double percent = (total == 0) ? 0.0 : Math.round((occupied * 10000.0 / total)) / 100.0;

        return new UsageStatsResponse(floor, (bay != null && !bay.isBlank()) ? bay : null,
                total, available, occupied, percent);
    }
}
