package Liatrio.ParkingManagement.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app.spot-store")
public class SpotStoreProperties {
    public enum Type { INMEMORY, REDIS }

    private Type type = Type.INMEMORY;
    private int initialCapacity = 1024;
    private double loadFactor = 0.75;

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public int getInitialCapacity() { return initialCapacity; }
    public void setInitialCapacity(int initialCapacity) { this.initialCapacity = initialCapacity; }

    public double getLoadFactor() { return loadFactor; }
    public void setLoadFactor(double loadFactor) { this.loadFactor = loadFactor; }
}
