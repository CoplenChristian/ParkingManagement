package Liatrio.ParkingManagement.controller;

import Liatrio.ParkingManagement.dto.CarAssignmentResponse;
import Liatrio.ParkingManagement.dto.CheckInRequest;
import Liatrio.ParkingManagement.dto.ReceiptResponse;
import Liatrio.ParkingManagement.model.Car;
import Liatrio.ParkingManagement.service.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cars")
public class CarsController {

    private final CarService carService;

    public CarsController(CarService carService) {
        this.carService = carService;
    }

    @PostMapping("/checkin")
    public ResponseEntity<CarAssignmentResponse> checkIn(@RequestBody CheckInRequest req) {
        Car assigned = carService.checkIn(req);
        return ResponseEntity.ok(CarAssignmentResponse.from(assigned));
    }

    @PostMapping("/{plate}/checkout")
    public ResponseEntity<ReceiptResponse> checkOut(@PathVariable String plate) {
        return ResponseEntity.ok(carService.checkOut(plate));
    }

    // Stretch: look up by plate (active only)
    @GetMapping("/{plate}")
    public ResponseEntity<CarAssignmentResponse> byPlate(@PathVariable String plate) {
        return carService.lookupActiveByPlate(plate)
                .map(c -> ResponseEntity.ok(CarAssignmentResponse.from(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
