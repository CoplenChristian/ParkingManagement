package Liatrio.ParkingManagement.dto;

import Liatrio.ParkingManagement.model.SpotSize;

public class UpdateSpotRequest {

    private Integer floor;
    private String bay;
    private String spotNumber;
    private SpotSize size;
    private Boolean evCapable;

    public UpdateSpotRequest() {}

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

    public boolean isAllFieldsNull() {
        return floor == null &&
               bay == null &&
               spotNumber == null &&
               size == null &&
               evCapable == null;
    }
}
