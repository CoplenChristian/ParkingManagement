package Liatrio.ParkingManagement.dto;

import java.time.Instant;
import java.util.UUID;

import Liatrio.ParkingManagement.model.Car;
import Liatrio.ParkingManagement.model.SpotSize;

public class CarAssignmentResponse {
    private String licensePlate;
    private String spotId;
    private SpotSize carSize;
    private Instant checkInAt;

    public CarAssignmentResponse() {}

    public static CarAssignmentResponse from(Car a) {
        CarAssignmentResponse r = new CarAssignmentResponse();
        r.licensePlate = a.licensePlate();
        r.spotId = a.assignedSpotId() != null ? a.assignedSpotId().toString() : null;
        r.carSize = a.carSize();
        r.checkInAt = a.checkInAt();
        return r;
    }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    public SpotSize getCarSize() { return carSize; }
    public void setCarSize(SpotSize carSize) { this.carSize = carSize; }
    public Instant getCheckInAt() { return checkInAt; }
    public void setCheckInAt(Instant checkInAt) { this.checkInAt = checkInAt; }

    public UUID getSpotUuid() { return spotId == null ? null : UUID.fromString(spotId); }
}
