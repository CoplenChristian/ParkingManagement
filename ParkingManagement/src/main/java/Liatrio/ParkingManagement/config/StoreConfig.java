package Liatrio.ParkingManagement.config;

import Liatrio.ParkingManagement.model.Car;
import Liatrio.ParkingManagement.model.Spot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class StoreConfig {

    private final SpotStoreProperties props;

    public StoreConfig(SpotStoreProperties props) {
        this.props = props;
    }

    @Bean
    public ConcurrentHashMap<UUID, Spot> spotStore() {
        if (props.getType() != SpotStoreProperties.Type.INMEMORY) {
            // Only INMEMORY is implemented right now; fail fast if misconfigured.
            throw new IllegalStateException("Only INMEMORY store type is supported at the moment.");
        }
        // Note: ConcurrentHashMap ignores loadFactor for most practical purposes.
        return new ConcurrentHashMap<>(props.getInitialCapacity());
    }

    @Bean
    public ConcurrentHashMap<String, Car> carStore() {
        // Car assignments keyed by normalized license plate
        return new ConcurrentHashMap<>(props.getInitialCapacity());
    }
}
