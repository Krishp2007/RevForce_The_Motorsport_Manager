package com.revForce.race;
import java.util.*;
import java.sql.*;

public class Races {
    // Constants for race state
    private static final int NOT_STARTED = 0;
    private static final int IN_PROGRESS = 1;
    private static final int FINISHED = 2;

    private int raceStatus = NOT_STARTED;
    private final List<Car> cars;
    private final Track track;
    private int currentLap = 0;
    private final Map<Integer, Double> lapTimes; // Car ID → Last lap time

    public Races(Track track) {
        this.track = track;
        this.cars = Collections.synchronizedList(new ArrayList<>());
        this.lapTimes = new HashMap<>();
        }

    // Add car to race (thread-safe)
    public synchronized void registerCar(Car car) throws InvalidCarException {
        if (car == null) throw new InvalidCarException("Car cannot be null");
        cars.add(car);
    }

    // Start race with multithreading
    public synchronized void startRace() throws RaceException {
        if (raceStatus != NOT_STARTED) {
            throw new RaceException("Races already started/finished");
        }
        raceStatus = IN_PROGRESS;

        // Thread for lap timing
        new Thread(() -> {
            while (raceStatus == IN_PROGRESS && currentLap < track.getTotalLaps()) {
                updateLapTimes();
                currentLap++;
                try {
                    Thread.sleep(5000); // 5 sec per lap (simulated)
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            raceStatus = FINISHED;
        }).start();

        // Thread for random events
        new Thread(this::simulateRaceEvents).start();
    }

    // Update lap times (synchronized)
    private synchronized void updateLapTimes() {
        for (Car car : cars) {
            double lapTime = calculateLapTime(car);
            lapTimes.put(car.getId(), lapTime);
            System.out.printf("Car %d completed lap %d in %.2f sec\n",
                    car.getId(), currentLap, lapTime);
        }
    }

    private double calculateLapTime(Car car) {
        // Simplified lap time calculation (track length / speed + randomness)
        return (track.getLength() / car.getSpeed()) * (0.8 + Math.random() * 0.4);
    }

    // Simulate random race events
    private void simulateRaceEvents() {
        Random random = new Random();
        while (raceStatus == IN_PROGRESS) {
            try {
                Thread.sleep(3000); // Event every 3 sec
                synchronized (this) {
                    Car randomCar = cars.get(random.nextInt(cars.size()));
                    if (random.nextDouble() < 0.3) { // 30% chance of event
                        System.out.println("Car " + randomCar.getId() +
                                " pits for fresh tires!");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // Get current leaderboard
    public synchronized void printLeaderboard() {
        cars.sort(Comparator.comparingDouble(c -> lapTimes.getOrDefault(c.getId(), Double.MAX_VALUE)));
        System.out.println("\n--- Leaderboard ---");
        for (int i = 0; i < cars.size(); i++) {
            Car car = cars.get(i);
            System.out.printf("%d. Car %d (Avg: %.2f sec/lap)\n",
                    i + 1, car.getId(), lapTimes.get(car.getId()));
        }
    }

    // Supporting classes
    public static class Car {
        private final int id;
        private final double speed; // m/s
        public Car(int id, double speed) { this.id = id; this.speed = speed; }
        public int getId() { return id; }
        public double getSpeed() { return speed; }
    }

    public static class Track {
        private final String name;
        private final double length; // meters
        private final int totalLaps;
        public Track(String name, double length, int laps) {
            this.name = name;
            this.length = length;
            this.totalLaps = laps;
        }
        public double getLength() { return length; }
        public int getTotalLaps() { return totalLaps; }
    }

    public static class InvalidCarException extends Exception {
        public InvalidCarException(String message) { super(message); }
    }

    public static class RaceException extends Exception {
        public RaceException(String message) { super(message); }
    }

    // Example usage
    public static void main(String[] args) {
        Track monza = new Track("Monza Circuit", 5793, 5);
        Races race = new Races(monza);

        try {
            race.registerCar(new Car(1, 85)); // Ferrari (85 m/s)
            race.registerCar(new Car(2, 82)); // Mercedes (82 m/s)

            System.out.println("Starting race at " + monza.name + "!");
            race.startRace();

            // Let race run for 25 seconds
            Thread.sleep(25000);

            race.printLeaderboard();
        } catch (Exception e) {
            System.err.println("Races error: " + e.getMessage());
        }
    }
}
class RaceDBManager {
    private static final String DB_URL = "jdbc:mysql://localhost81:3306/racing_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    // JDBC DRIVER INITIALIZATION
    static {
        try {
            Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // 1. CREATE RACE TABLE (Run once during setup)
    public void createRaceTable() throws SQLException {
        String sql = """
            CREATE TABLE IF NOT EXISTS races (
                race_id INT PRIMARY KEY AUTO_INCREMENT,
                track_name VARCHAR(100) NOT NULL,
                total_laps INT NOT NULL,
                status VARCHAR(20) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        }
    }

    // 2. INSERT NEW RACE (Using PreparedStatement)
    public int createRace(String trackName, int totalLaps) throws SQLException {
        String sql = "INSERT INTO races (track_name, total_laps, status) VALUES (?, ?, 'PENDING')";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, trackName);
            stmt.setInt(2, totalLaps);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : -1; // Return generated race_id
            }
        }
    }

    // 3. UPDATE RACE STATUS (Using PreparedStatement)
    public boolean updateRaceStatus(int raceId, String status) throws SQLException {
        String sql = "UPDATE races SET status = ? WHERE race_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            stmt.setInt(2, raceId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 4. DELETE RACE (Using PreparedStatement)
    public boolean deleteRace(int raceId) throws SQLException {
        String sql = "DELETE FROM races WHERE race_id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, raceId);
            return stmt.executeUpdate() > 0;
        }
    }

    // 5. GET ALL RACES (Using PreparedStatement for filtering)
    public List<Race> getAllRaces(String statusFilter) throws SQLException {
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM races";

        if (statusFilter != null) {
            sql += " WHERE status = ?";
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (statusFilter != null) {
                stmt.setString(1, statusFilter);
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    races.add(new Race(
                            rs.getInt("race_id"),
                            rs.getString("track_name"),
                            rs.getInt("total_laps"),
                            rs.getString("status")
                    ));
                }
            }
        }
        return races;
    }

    // Race class (inner class or separate file)
    public static class Race {
        private final int raceId;
        private final String trackName;
        private final int totalLaps;
        private final String status;

        public Race(int raceId, String trackName, int totalLaps, String status) {
            this.raceId = raceId;
            this.trackName = trackName;
            this.totalLaps = totalLaps;
            this.status = status;
        }
    }
}