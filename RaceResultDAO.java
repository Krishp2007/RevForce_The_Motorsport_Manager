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

        public RaceResult(int raceId, String trackName, Date raceDate, Time raceTime, int laps,
                          int position, long points, String teamName) {
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

    public static List<RaceResult> getAllRaceResults() {
        List<RaceResult> results = new ArrayList<>();
        String query = "SELECT r.race_id, t.name AS track_name, r.race_date, r.race_time, r.laps, " +
                "rp.position, rp.points, tm.team_name " +
                "FROM races r " +
                "JOIN tracks t ON r.track_id = t.track_id " +
                "JOIN race_participations rp ON r.race_id = rp.race_id " +
                "JOIN teams tm ON rp.team_id = tm.team_id " +
                "WHERE rp.position IS NOT NULL " +
                "ORDER BY r.race_date DESC, r.race_time DESC, rp.position ASC";
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
                        rs.getInt("position"),
                        rs.getLong("points"),
                        rs.getString("team_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }


    public static List<ParticipantStats> getParticipantsWithStats(int raceId) {
        String sql = "SELECT rp.participation_id, rp.race_id, rp.team_id, rp.position, rp.points, " +
                "d.skill_level, c.engine_power, t.budget " +
                "FROM race_participations rp " +
                "JOIN teams t ON rp.team_id = t.team_id " +
                "JOIN drivers d ON d.team_id = t.team_id " +          // Assuming one driver per team for simplicity
                "JOIN cars c ON c.team_id = t.team_id " +
                "WHERE rp.race_id = ?";

        List<ParticipantStats> list = new ArrayList<>();
        try (Connection conn = DBManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, raceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                RaceParticipation rp = new RaceParticipation(
                        rs.getInt("participation_id"),
                        rs.getInt("race_id"),
                        rs.getInt("team_id"),
                        rs.getObject("position") != null ? rs.getInt("position") : null,
                        rs.getInt("points")
                );
                int skill = rs.getInt("skill_level");
                int power = rs.getInt("engine_power");
                long budget = rs.getLong("budget");

                list.add(new ParticipantStats(rp, skill, power, budget));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public static void simulateRaceResults(int raceId) {
        List<ParticipantStats> participants = getParticipantsWithStats(raceId);
        Random rand = new Random();

        for (ParticipantStats p : participants) {
            double baseScore = p.driverSkill * 0.5 + p.carPower * 0.3 + p.teamBudget * 0.2;
            double randomFactor = 0.8 + rand.nextDouble() * 0.4;  // 0.8 to 1.2 multiplier
            p.setScore(baseScore * randomFactor);
        }

        Collections.sort(participants, Comparator.comparingDouble(ParticipantStats::getScore).reversed());

        for (int i = 0; i < participants.size(); i++) {
            ParticipantStats p = participants.get(i);
            int position = i + 1;
            int points = calculatePoints(position);

            updatePositionAndPoints(p.participation.participationId, position, points);
        }

        System.out.println("Race " + raceId + " results simulated and saved.");
    }

    public static int calculatePoints(int position) {
        switch (position) {
            case 1: return 25;
            case 2: return 18;
            case 3: return 15;
            case 4: return 12;
            case 5: return 10;
            case 6: return 8;
            case 7: return 6;
            case 8: return 4;
            case 9: return 2;
            case 10: return 1;
            default: return 0;
        }
    }

    public static boolean updatePositionAndPoints(int participationId, int position, int points) {
        try (Connection conn = DBManager.getConnection()) {
            PreparedStatement ps = conn.prepareStatement("UPDATE race_participations SET position = ?, points = ? WHERE participation_id = ?");
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
