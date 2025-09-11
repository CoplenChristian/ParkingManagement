package Liatrio.ParkingManagement.dto;

import Liatrio.ParkingManagement.model.SpotSize;

public class CheckInRequest {

    private String licensePlate;
    private SpotSize carSize;        // Optional; defaults to STANDARD if null
    private String requestedSpotId;  // Optional; service validates if provided

    public CheckInRequest() {}

    public String getLicensePlate() {
        return licensePlate;
    }
    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public SpotSize getCarSize() {
        return carSize;
    }
    public void setCarSize(SpotSize carSize) {
        this.carSize = carSize;
    }

    public String getRequestedSpotId() {
        return requestedSpotId;
    }
    public void setRequestedSpotId(String requestedSpotId) {
        this.requestedSpotId = requestedSpotId;
    }
}
