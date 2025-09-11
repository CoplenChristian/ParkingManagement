package Liatrio.ParkingManagement.dto;

import java.time.Instant;
import java.util.UUID;

import Liatrio.ParkingManagement.model.Spot;
import Liatrio.ParkingManagement.model.SpotSize;
import Liatrio.ParkingManagement.model.Status;

public class SpotResponse {
    private String id;
    private int floor;
    private String bay;
    private String spotNumber;
    private SpotSize size;
    private boolean evCapable;
    private Status status;
    private Instant lastUpdated;

    public SpotResponse() {}

    public static SpotResponse from(Spot s) {
        SpotResponse r = new SpotResponse();
        r.id = s.id().toString();
        r.floor = s.floor();
        r.bay = s.bay();
        r.spotNumber = s.spotNumber();
        r.size = s.size();
        r.evCapable = s.evCapable();
        r.status = s.status();
        r.lastUpdated = s.lastUpdated();
        return r;
    }

    public UUID getUuid() {
        return UUID.fromString(id);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public int getFloor() { return floor; }
    public void setFloor(int floor) { this.floor = floor; }
    public String getBay() { return bay; }
    public void setBay(String bay) { this.bay = bay; }
    public String getSpotNumber() { return spotNumber; }
    public void setSpotNumber(String spotNumber) { this.spotNumber = spotNumber; }
    public SpotSize getSize() { return size; }
    public void setSize(SpotSize size) { this.size = size; }
    public boolean isEvCapable() { return evCapable; }
    public void setEvCapable(boolean evCapable) { this.evCapable = evCapable; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Instant getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(Instant lastUpdated) { this.lastUpdated = lastUpdated; }
}
