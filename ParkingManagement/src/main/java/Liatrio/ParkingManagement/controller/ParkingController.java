package Liatrio.ParkingManagement.controller;

import Liatrio.ParkingManagement.dto.CreateSpotRequest;
import Liatrio.ParkingManagement.dto.SpotResponse;
import Liatrio.ParkingManagement.dto.UpdateSpotRequest;
import Liatrio.ParkingManagement.dto.UsageStatsResponse;
import Liatrio.ParkingManagement.exception.BadRequestException;
import Liatrio.ParkingManagement.model.Spot;
import Liatrio.ParkingManagement.model.SpotSize;
import Liatrio.ParkingManagement.model.Status;
import Liatrio.ParkingManagement.service.SpotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/spots")
public class ParkingController {
	
	private final SpotService spotService;

    public ParkingController(SpotService spotService) {
        this.spotService = spotService;
    }

    @PostMapping
    public ResponseEntity<SpotResponse> create(@RequestBody CreateSpotRequest req) {
    	
    	if (req.getFloor() == null || req.getFloor() < 0) {
            throw new BadRequestException("floor is required and must be >= 0");
        }
        if (req.getSpotNumber() == null || req.getSpotNumber().isBlank()) {
            throw new BadRequestException("spotNumber is required");
        }
        if (req.getSize() == null) {
            throw new BadRequestException("size is required");
        }
        if (req.getEvCapable() == null) {
            throw new BadRequestException("evCapable is required");
        }
        Spot created = spotService.createSpot(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(SpotResponse.from(created));
    }

    // GET /spots with optional filters: status, floor, bay, size, evCapable
    @GetMapping
    public ResponseEntity<List<SpotResponse>> list(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String bay,
            @RequestParam(required = false) SpotSize size,
            @RequestParam(required = false) Boolean evCapable
    ) {
        List<SpotResponse> out = spotService
                .getAll(status, floor, bay, size, evCapable)
                .stream().map(SpotResponse::from)
                .toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/available")
    public ResponseEntity<List<SpotResponse>> listAvailable(
            @RequestParam(required = false) Integer floor,
            @RequestParam(required = false) String bay,
            @RequestParam(required = false) SpotSize size,
            @RequestParam(required = false) Boolean evCapable
    ) {
        List<SpotResponse> out = spotService
                .getAll(Status.AVAILABLE, floor, bay, size, evCapable)
                .stream().map(SpotResponse::from)
                .toList();
        return ResponseEntity.ok(out);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SpotResponse> get(@PathVariable String id) {
        Spot s = spotService.getById(id);
        return ResponseEntity.ok(SpotResponse.from(s));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SpotResponse> update(
            @PathVariable String id,
            @RequestBody UpdateSpotRequest req
    ) {
        if (req == null || req.isAllFieldsNull()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        Spot updated = spotService.updateInfo(id, req);
        return ResponseEntity.ok(SpotResponse.from(updated));
    }

    @PostMapping("/{id}/available")
    public ResponseEntity<SpotResponse> markAvailable(@PathVariable String id) {
        Spot s = spotService.updateStatus(id, Status.AVAILABLE);
        return ResponseEntity.ok(SpotResponse.from(s));
    }

    @PostMapping("/{id}/occupied")
    public ResponseEntity<SpotResponse> markOccupied(@PathVariable String id) {
        Spot s = spotService.updateStatus(id, Status.OCCUPIED);
        return ResponseEntity.ok(SpotResponse.from(s));
    }

    @GetMapping("/usage")
    public ResponseEntity<UsageStatsResponse> usage() {
        return ResponseEntity.ok(spotService.getUsageStats());
    }
}
