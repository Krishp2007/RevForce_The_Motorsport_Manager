package dao;

import model.ParticipantStats;
import model.RaceParticipation;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class RaceResultDAO {

    public static class RaceResult {
        public int raceId;
        public String trackName;
        public Date raceDate;
        public Time raceTime;
        public int laps;
        public int position;
        public long points;
        public String teamName;

        public RaceResult(int raceId, String trackName, Date raceDate, Time raceTime,
                          int laps, int position, long points, String teamName) {
            this.raceId = raceId;
            this.trackName = trackName;
            this.raceDate = raceDate;
            this.raceTime = raceTime;
            this.laps = laps;
            this.position = position;
            this.points = points;
            this.teamName = teamName;
        }
    }

    public static class DetailedRaceResult {
        public int position;
        public String teamName;
        public String driverName;
        public String carName;
        public int points;
        public int driverSkill;
        public int carPower;
        public long teamBudget;

        public DetailedRaceResult(int position, String teamName, String driverName,
                                  String carName, int points, int driverSkill,
                                  int carPower, long teamBudget) {
            this.position = position;
            this.teamName = teamName;
            this.driverName = driverName;
            this.carName = carName;
            this.points = points;
            this.driverSkill = driverSkill;
            this.carPower = carPower;
            this.teamBudget = teamBudget;
        }
    }

    public static List<DetailedRaceResult> getDetailedRaceResults(int raceId) {
        List<DetailedRaceResult> results = new ArrayList<>();
        String query =
                "SELECT rr.position, rr.points, t.team_name, d.driver_name, c.car_name, " +
                        "d.skill_level, c.engine_power, t.budget " +
                        "FROM race_results rr " +
                        "JOIN race_participations rp ON rr.participation_id = rp.participation_id " +
                        "JOIN teams t ON rp.team_id = t.team_id " +
                        "JOIN drivers d ON rp.driver_id = d.driver_id " +
                        "JOIN cars c ON rp.car_id = c.car_id " +
                        "WHERE rr.race_id = ? " +
                        "ORDER BY rr.position ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, raceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(new DetailedRaceResult(
                            rs.getInt("position"),
                            rs.getString("team_name"),
                            rs.getString("driver_name"),
                            rs.getString("car_name"),
                            rs.getInt("points"),
                            rs.getInt("skill_level"),
                            rs.getInt("engine_power"),
                            rs.getLong("budget")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static void autoSimulatePastRaces() {
        try (Connection conn = DBManager.getConnection()) {
            // Find past races that have participations, to register all teams
            String findRacesQuery =
                    "SELECT r.race_id " +
                            "FROM races r " +
                            "JOIN race_participations rp ON r.race_id = rp.race_id " +
                            "WHERE CONCAT(r.race_date, ' ', r.race_time) < NOW() " +
                            "GROUP BY r.race_id";

            try (PreparedStatement ps1 = conn.prepareStatement(findRacesQuery);
                 ResultSet rs1 = ps1.executeQuery()) {

                while (rs1.next()) {
                    int raceId = rs1.getInt("race_id");

                    // Get all teams to register them for this race if not registered
                    String getTeamsQuery = "SELECT team_id FROM teams";
                    try (PreparedStatement ps2 = conn.prepareStatement(getTeamsQuery);
                         ResultSet rs2 = ps2.executeQuery()) {

                        while (rs2.next()) {
                            int teamId = rs2.getInt("team_id");
                            // Register team for race if not already registered, ignoring duplicates
                            String registerQuery =
                                    "INSERT IGNORE INTO race_participations (race_id, team_id, car_id, driver_id) " +
                                            "VALUES (?, ?, NULL, NULL)";
                            try (PreparedStatement ps3 = conn.prepareStatement(registerQuery)) {
                                ps3.setInt(1, raceId);
                                ps3.setInt(2, teamId);
                                ps3.executeUpdate();
                            }
                        }
                    }

                    // Simulate the race after registration
                    simulateRaceResults(raceId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<RaceResult> getAllRaceResults() {
        List<RaceResult> results = new ArrayList<>();
        String query =
                "SELECT r.race_id, t.name AS track_name, r.race_date, r.race_time, r.laps, " +
                        "rr.position, rr.points, tm.team_name " +
                        "FROM races r " +
                        "JOIN tracks t ON r.track_id = t.track_id " +
                        "LEFT JOIN race_participations rp ON r.race_id = rp.race_id " +
                        "LEFT JOIN race_results rr ON rp.participation_id = rr.participation_id " +
                        "LEFT JOIN teams tm ON rp.team_id = tm.team_id " +
                        "ORDER BY r.race_date DESC, r.race_time DESC, rr.position ASC";

        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                results.add(new RaceResult(
                        rs.getInt("race_id"),
                        rs.getString("track_name"),
                        rs.getDate("race_date"),
                        rs.getTime("race_time"),
                        rs.getInt("laps"),
                        rs.getObject("position") != null ? rs.getInt("position") : -1,
                        rs.getObject("points") != null ? rs.getLong("points") : 0L,
                        rs.getString("team_name") != null ? rs.getString("team_name") : "No participants"
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static List<ParticipantStats> getParticipantsWithStats(int raceId) {
        String sql =
                "SELECT rp.participation_id, rp.race_id, rp.team_id, rr.position, rr.points, " +
                        "d.skill_level, c.engine_power, t.budget " +
                        "FROM race_participations rp " +
                        "JOIN race_results rr ON rp.participation_id = rr.participation_id " +
                        "JOIN teams t ON rp.team_id = t.team_id " +
                        "JOIN drivers d ON d.team_id = t.team_id AND d.driver_id = (" +
                        "SELECT MIN(driver_id) FROM drivers WHERE team_id = t.team_id) " +
                        "JOIN cars c ON c.team_id = t.team_id AND c.car_id = (" +
                        "SELECT MIN(car_id) FROM cars WHERE team_id = t.team_id) " +
                        "WHERE rp.race_id = ?";

        List<ParticipantStats> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, raceId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RaceParticipation rp = new RaceParticipation(
                            rs.getInt("participation_id"),
                            rs.getInt("race_id"),
                            rs.getInt("team_id")
                    );
                    int skill = rs.getInt("skill_level");
                    int power = rs.getInt("engine_power");
                    long budget = rs.getLong("budget");

                    list.add(new ParticipantStats(rp, skill, power, budget));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void simulateRaceResults(int raceId) {
        List<ParticipantStats> participants = getParticipantsWithStats(raceId);
        if (participants.isEmpty()) {
            System.out.println("No participants found for race " + raceId);
            return;
        }

        Random rand = new Random();
        double maxBudget = participants.stream().mapToLong(p -> p.teamBudget).max().orElse(1);

        for (ParticipantStats p : participants) {
            double normalizedBudget = (p.teamBudget / maxBudget) * 100;
            double baseScore = p.driverSkill * 0.4 + p.carPower * 0.4 + normalizedBudget * 0.2;
            double randomFactor = 0.7 + rand.nextDouble() * 0.6; // 0.7 to 1.3
            p.setScore(baseScore * randomFactor);
        }

        participants.sort(Comparator.comparingDouble(ParticipantStats::getScore).reversed());

        clearRaceResults(raceId);

        for (int i = 0; i < participants.size(); i++) {
            ParticipantStats p = participants.get(i);
            int position = i + 1;
            int points = calculatePoints(position);
            updatePositionAndPoints(p.participation.participationId, position, points);
            storeDetailedRaceResult(raceId, p.participation.teamId, position, points);
        }

    }

    private static void clearRaceResults(int raceId) {
        String query = "DELETE FROM race_results WHERE race_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, raceId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void storeDetailedRaceResult(int raceId, int teamId, int position, int points) {
        try (Connection conn = DBManager.getConnection()) {
            String getParticipationQuery =
                    "SELECT participation_id FROM race_participations WHERE race_id = ? AND team_id = ? LIMIT 1";

            try (PreparedStatement ps1 = conn.prepareStatement(getParticipationQuery)) {
                ps1.setInt(1, raceId);
                ps1.setInt(2, teamId);
                try (ResultSet rs = ps1.executeQuery()) {
                    if (rs.next()) {
                        int participationId = rs.getInt("participation_id");
                        String insertResultQuery =
                                "INSERT INTO race_results (race_id, participation_id, position, points) VALUES (?, ?, ?, ?)";
                        try (PreparedStatement ps2 = conn.prepareStatement(insertResultQuery)) {
                            ps2.setInt(1, raceId);
                            ps2.setInt(2, participationId);
                            ps2.setInt(3, position);
                            ps2.setInt(4, points);
                            ps2.executeUpdate();
                        }
                    } else {
                        System.out.println("No participation found for race_id " + raceId + " and team_id " + teamId);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static int calculatePoints(int position) {
        switch (position) {
            case 1:  return 25;
            case 2:  return 18;
            case 3:  return 15;
            case 4:  return 12;
            case 5:  return 10;
            case 6:  return 8;
            case 7:  return 6;
            case 8:  return 4;
            case 9:  return 2;
            case 10: return 1;
            default: return 0;
        }
    }

    public static boolean updatePositionAndPoints(int participationId, int position, int points) {
        String query = "UPDATE race_results SET position = ?, points = ? WHERE participation_id = ?";
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, position);
            ps.setInt(2, points);
            ps.setInt(3, participationId);

            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
