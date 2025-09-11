package Liatrio.ParkingManagement.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class ReceiptResponse {
    private String licensePlate;
    private String spotId;
    private Instant checkInAt;
    private Instant checkOutAt;
    private long billableMinutes;
    private BigDecimal amount;

    public ReceiptResponse() {}

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    public String getSpotId() { return spotId; }
    public void setSpotId(String spotId) { this.spotId = spotId; }
    public Instant getCheckInAt() { return checkInAt; }
    public void setCheckInAt(Instant checkInAt) { this.checkInAt = checkInAt; }
    public Instant getCheckOutAt() { return checkOutAt; }
    public void setCheckOutAt(Instant checkOutAt) { this.checkOutAt = checkOutAt; }
    public long getBillableMinutes() { return billableMinutes; }
    public void setBillableMinutes(long billableMinutes) { this.billableMinutes = billableMinutes; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}
