package Liatrio.ParkingManagement.dto;

import Liatrio.ParkingManagement.model.SpotSize;

public class CreateSpotRequest {

    private Integer floor;
    private String bay;           // e.g., "A"
    private String spotNumber;    // e.g., "A-12"
    private SpotSize size;        // Optional; service defaults to STANDARD if null
    private Boolean evCapable;    // Optional; defaults to false if null

    public CreateSpotRequest() {}

    public Integer getFloor() {
        return floor;
    }
    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    public String getBay() {
        return bay;
    }
    public void setBay(String bay) {
        this.bay = bay;
    }

    public String getSpotNumber() {
        return spotNumber;
    }
    public void setSpotNumber(String spotNumber) {
        this.spotNumber = spotNumber;
    }

    public SpotSize getSize() {
        return size;
    }
    public void setSize(SpotSize size) {
        this.size = size;
    }

    public Boolean getEvCapable() {
        return evCapable;
    }
    public void setEvCapable(Boolean evCapable) {
        this.evCapable = evCapable;
    }
}
