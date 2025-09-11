package Liatrio.ParkingManagement.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import Liatrio.ParkingManagement.model.Car;
import Liatrio.ParkingManagement.model.Spot;
import Liatrio.ParkingManagement.model.SpotSize;
import Liatrio.ParkingManagement.model.Status;

import java.time.Instant;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DataSeeder {
    // DEMO-ONLY CLASS
    // Configurable in application.yml to toggle and size of seed data

    private static final String[] BAYS = {"A", "B", "C", "D", "E"};
    private static final SpotSize[] SIZES = SpotSize.values();
    private static final Random RANDOM = new Random();

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.seed.count:10}")
    private int seedCount;

    @Value("${app.seed.occupancy:0.5}")
    private double occupancyRatio; // 0.0..1.0 probability that a spot starts OCCUPIED

    @Bean
    CommandLineRunner seedTestData(
            ConcurrentHashMap<UUID, Spot> spotStore,
            ConcurrentHashMap<String, Car> carStore
    ) {
        return args -> {
            if (!seedEnabled) {
                System.out.println("⚠️ Spot seeding is disabled.");
                return;
            }

            int occupied = 0;
            for (int i = 0; i < seedCount; i++) {
                UUID id = UUID.randomUUID();

                int floor = RANDOM.nextInt(5); // floors 0..4
                String bay = BAYS[RANDOM.nextInt(BAYS.length)];
                String spotNumber = bay + "-" + (RANDOM.nextInt(20) + 1);

                SpotSize size = SIZES[RANDOM.nextInt(SIZES.length)];
                boolean evCapable = RANDOM.nextDouble() < 0.3; // ~30% EV-capable
                boolean startOccupied = RANDOM.nextDouble() < occupancyRatio;

                Status status = startOccupied ? Status.OCCUPIED : Status.AVAILABLE;

                Spot spot = new Spot(
                        id,
                        floor,
                        bay,
                        spotNumber,
                        size,
                        evCapable,
                        status,
                        Instant.now()
                );

                spotStore.put(id, spot);

                // If seeding as occupied, create a compatible active car assignment
                if (startOccupied) {
                    SpotSize carSize = randomCarSizeCompatibleWith(size);
                    String plate = randomPlate();
                    Instant checkIn = Instant.now().minusSeconds(RANDOM.nextInt(60 * 6) + 60); // 1–361 mins ago

                    Car car = new Car(
                            plate,
                            carSize,
                            id,
                            checkIn
                    );

                    carStore.put(plate, car);
                    occupied++;
                }
            }

            System.out.printf("✅ Seeded %d spots (%d occupied, %d available).%n",
                    seedCount, occupied, seedCount - occupied);
        };
    }

    private static SpotSize randomCarSizeCompatibleWith(SpotSize spotSize) {
        // Car must be <= spot capacity
        // COMPACT spot → only COMPACT cars
        // STANDARD spot → COMPACT or STANDARD cars
        // OVERSIZED spot → any
        return switch (spotSize) {
            case COMPACT -> SpotSize.COMPACT;
            case STANDARD -> RANDOM.nextBoolean() ? SpotSize.COMPACT : SpotSize.STANDARD;
            case OVERSIZED -> {
                int pick = RANDOM.nextInt(3);
                yield (pick == 0) ? SpotSize.COMPACT : (pick == 1 ? SpotSize.STANDARD : SpotSize.OVERSIZED);
            }
        };
    }

    private static String randomPlate() {
        // Simple IL-style-ish plate: 3 letters + 3 digits (e.g., ABC-123)
        char a = (char) ('A' + RANDOM.nextInt(26));
        char b = (char) ('A' + RANDOM.nextInt(26));
        char c = (char) ('A' + RANDOM.nextInt(26));
        int n = RANDOM.nextInt(900) + 100;
        return "" + a + b + c + "-" + n;
        // If you prefer no dash: return "" + a + b + c + n;
    }
}
