package Liatrio.ParkingManagement.dto;

public class UsageStatsResponse {
    private Integer floor;       // null = all floors
    private String bay;          // null = all bays
    private int total;
    private int available;
    private int occupied;
    private double percentOccupied; // 0.00 - 100.00 (2 decimals)

    public UsageStatsResponse() {}

    public UsageStatsResponse(Integer floor, String bay, int total, int available, int occupied, double percentOccupied) {
        this.floor = floor;
        this.bay = bay;
        this.total = total;
        this.available = available;
        this.occupied = occupied;
        this.percentOccupied = percentOccupied;
    }

    public Integer getFloor() { return floor; }
    public void setFloor(Integer floor) { this.floor = floor; }
    public String getBay() { return bay; }
    public void setBay(String bay) { this.bay = bay; }
    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public int getAvailable() { return available; }
    public void setAvailable(int available) { this.available = available; }
    public int getOccupied() { return occupied; }
    public void setOccupied(int occupied) { this.occupied = occupied; }
    public double getPercentOccupied() { return percentOccupied; }
    public void setPercentOccupied(double percentOccupied) { this.percentOccupied = percentOccupied; }
}
