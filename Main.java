import dao.*;
import ds.BinarySearchTree;
import ds.UserTeamMap;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.sql.Date;

public class Main {
    static UserTeamMap map = new UserTeamMap();

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n" + "🏁".repeat(36));
            System.out.println("                     🏁      REVFORCE MOTORSPORTS      🏁");
            System.out.println("🏁".repeat(36));
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                              🚀 MAIN MENU 🚀                                 ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║  🔐 1. Login as Admin                                                        ║");
            System.out.println("║  🏎️ 2. Login as Team                                                         ║");
            System.out.println("║  👀 3. Continue as Viewer                                                    ║");
            System.out.println("║  ✨ 4. Create New Account                                                    ║");
            System.out.println("║  🚪 5. Exit                                                                  ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
            System.out.print("🎯 Choose option: ");
            String option = sc.nextLine();

            switch (option) {
                case "1" :
                    if(adminLogin(sc)) adminMenu(sc);
                    else System.out.println("❌ Incorrect admin credentials.");
                    break;

                case "2":
                    User user = userLogin(sc);
                    if (user != null && user.teamId != 0) userMenu(sc, user);
                    else System.out.println("❌ Invalid username or password, or no team assigned.");
                    break;

                case "3":
                    viewerMenu(sc);
                    break;

                case "4":
                    createAccount(sc);
                    break;

                case "5":
                    running = false;
                    System.out.println("👋 Goodbye! Thanks for racing with RevForce Motorsports!");
                    break;

                default:
                    System.out.println("⚠ Invalid option. Try again.");

            }
            System.out.println();
        }
        sc.close();
    }


    private static void viewerMenu(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                              👀 VIEWER DASHBOARD 👀");
            System.out.println("=".repeat(80));
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                           👁️ VIEWER DASHBOARD 👁️                             ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║  🏁 1. View Upcoming Races                                                   ║");
            System.out.println("║  🏆 2. View Race Results                                                     ║");
            System.out.println("║  3. 🔍 Search Team By Username                                               ║");
            System.out.println("║  🚪 3. Back to Main Menu                                                     ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
            System.out.print("🎯 Option: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️ Option cannot be empty. Please choose 1, 2, or 3.");
                continue;
            }

            switch (input) {
                case "1":
                    System.out.println("⏩ Viewing Upcoming Races...");
                    viewUpcomingRaces();
                    break;
                case "2":
                    System.out.println("🏁 Viewing Past Races Results...");
                    viewRaceResults();
                    break;
                case "3":
                    String userName = getValidNameInput(sc, "🔍 Enter Username: ");
                    User user = UserDAO.getUserByUsername(userName);
                    if (user != null) {
                        System.out.println("📊 Displaying statistics for team: " + user.teamId);
                        viewMyTeamStatistics(user.teamId);
                    } else {
                        System.out.println("❌ No Team Found for username '" + userName + "'!");
                    }
                    break;
                case "4":
                    running = false;
                    System.out.println("👋 Thank you for using the Race Viewer. Goodbye!");
                    break;
                default:
                    System.out.println("⚠️  Invalid choice, please select a valid option (1-4).");
            }
        }
    }


    private static boolean adminLogin(Scanner sc) {
        System.out.print("Enter admin username: ");
        String adminUser = sc.nextLine().trim();
        if (adminUser.isEmpty()) {
            System.out.println("Admin username cannot be empty.");
            return false;
        }

        System.out.print("Enter admin password: ");
        String adminPass = sc.nextLine().trim();
        if (adminPass.isEmpty()) {
            System.out.println("Admin password cannot be empty.");
            return false;
        }

        boolean isFound = UserDAO.authenticateAdmin(adminUser, adminPass);
        if (isFound) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Login failed.");
        }

        map.loadUserTeamMap();
        return false;
    }

    private static void adminMenu(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n" + "🔐".repeat(20));
            System.out.println("          🔐 ADMIN CONTROL PANEL 🔐");
            System.out.println("🔐".repeat(20));
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                           🎛️ ADMIN DASHBOARD 🎛️                              ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║  🏟️ 1. Add New Track                                                         ║");
            System.out.println("║  🚗 2. Add New Car                                                           ║");
            System.out.println("║  👨‍💼 3. Add New Driver                                                        ║");
            System.out.println("║  💰 4. Add Sponsor                                                           ║");
            System.out.println("║  🏁 5. Schedule Race                                                         ║");
            System.out.println("║  ❌ 6. Cancel Race                                                           ║");
            System.out.println("║  📊 7. View Team Rankings                                                    ║");
            System.out.println("║  💼 8. Generate Sponsorship Offers                                           ║");
            System.out.println("║  🚪 9. Logout                                                                ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");
            System.out.print("🎯 Choose option: ");
            String input = sc.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("⚠️ Option cannot be empty. Please choose 1-9.");
                continue;
            }
            switch (input) {
                case "1": addNewTrack(sc); break;
                case "2": addCar(sc); break;
                case "3": addDriver(sc); break;
                case "4": addSponsor(sc); break;
                case "5" : scheduleRace(sc); break;
                case "6" : cancelRace(sc); break;
                case "7": viewTeamRankings(sc); break;
                case "8": generateSponsorshipOffers(); break;
                case "9": running = false; System.out.println("🔐 Admin logged out."); break;
                default: System.out.println("⚠ Invalid choice.");
            }
        }
    }

    private static void addNewTrack(Scanner sc) {
        String name = getValidNameInput(sc, "Enter Track Name: ");
        String location = getValidNameInput(sc , "Enter Location : ");

        float lengthKm = 0f;
        while (true) {
            System.out.print("Enter Length (km): ");
            String s = sc.nextLine();
            try { lengthKm = Float.parseFloat(s);
                if (lengthKm <= 0) {
                    System.out.println("Length must be positive.");
                    continue;
                }
                if (lengthKm > 100) {
                    System.out.println("Length cannot exceed 100 km.");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        String difficulty = "";
        while (true) {
            try {
                difficulty = getValidNameInput(sc, "Enter Difficulty Level (Easy/Medium/Hard): ");
                if (!difficulty.equalsIgnoreCase("easy") && !difficulty.equalsIgnoreCase("medium") && !difficulty.equalsIgnoreCase("hard")) {
                    throw new RuntimeException("Enter Valid Input");
                }
                break;  // valid input, exit loop
            } catch (Exception e) {
                System.out.println("Invalid Input. Please enter Easy, Medium, or Hard.");
            }
        }

        String type = "";
        while (true) {
            try {
                type = getValidNameInput(sc, "Enter Track Type (Street/Circuit/Off-road): ");
                if (!type.equalsIgnoreCase("street") && !type.equalsIgnoreCase("circuit") && !type.equalsIgnoreCase("off-road")) {
                    throw new RuntimeException("Enter Valid Input");
                }
                break;  // valid input, exit loop
            } catch (Exception e) {
                System.out.println("Invalid Input. Please enter Street, Circuit, or Off-road.");
            }
        }


        System.out.print("Enter Track Image URL: ");
        String url = sc.nextLine().trim();
        if (url.isEmpty()) {
            url = "N/A";
        }

        if (TrackDAO.addTrack(name, location, lengthKm, url, difficulty, type)) System.out.println("Track added successfully.");
        else System.out.println("Failed to add track.");
    }

    private static void scheduleRace(Scanner sc) {
        // List available tracks
        List<Track> tracks = TrackDAO.getAllTracks();
        if (tracks.isEmpty()) {
            System.out.println("No tracks available. Add a track first.");
            return;
        }

        System.out.printf("%-5s %-30s %-20s %-8s %-10s %-10s%n", "ID", "Name", "Location", "Length", "Type", "Difficulty");
        System.out.println("----------------- --------------------------------------------------------------------");
        for (Track t : tracks) {
            System.out.printf("%-5d %-30s %-20s %-8.2f %-10s %-10s%n", t.trackId, t.name, t.location, t.lengthKm, t.trackType, t.difficultyLevel);
        }

        // Ask for track ID
        int trackId = -1;
        Track selectedTrack = null;
        while (true) {
            System.out.print("Enter Track ID to schedule race (or 0 to cancel): ");
            String input = sc.nextLine();
            try {
                trackId = Integer.parseInt(input);
                if (trackId == 0) return;
                for (Track track : tracks) {
                    if (track.trackId == trackId) {
                        selectedTrack = track;
                        break;
                    }
                }
                if (selectedTrack != null) break;
                else System.out.println("Track ID not found. Try again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Enter a valid numeric track ID.");
            }
        }

        LocalDate raceDate;
        while (true) {
            System.out.print("Enter Race Date (YYYY-MM-DD, must be today or later): ");
            String dateInput = sc.nextLine().trim();
            if (dateInput.isEmpty()) {
                System.out.println("Date cannot be empty. Please enter a valid date.");
                continue;
            }
            try {
                raceDate = LocalDate.parse(dateInput);
                if (raceDate.isBefore(LocalDate.now())) {
                    System.out.println("Race date cannot be in the past.");
                } else break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }

        LocalTime raceTime;
        while (true) {
            System.out.print("Enter Race Time (HH:MM, 24-hour, must not be in the past if today): ");
            String timeInput = sc.nextLine().trim();
            if (timeInput.isEmpty()) {
                System.out.println("Time cannot be empty. Please enter a valid time.");
                continue;
            }
            try {
                raceTime = LocalTime.parse(timeInput);
                LocalDateTime raceDateTime = LocalDateTime.of(raceDate, raceTime);
                if (raceDateTime.isBefore(LocalDateTime.now())) {
                    System.out.println("Race time must be in the future.");
                } else break;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid time format. Please use HH:MM (24-hour).");
            }
        }

        // Automatically generate laps based on track type
        int laps = 40; // Default
        if (selectedTrack.trackType.equalsIgnoreCase("Circuit")) laps = 50;
        else if (selectedTrack.trackType.equalsIgnoreCase("Street")) laps = 30;
        else if (selectedTrack.trackType.equalsIgnoreCase("Off-road")) laps = 20;

        String formattedTime = raceTime.toString() + ":00";
        boolean success = RaceDAO.scheduleRace(trackId, raceDate.toString(), formattedTime, laps);
        if (success) {
            System.out.println("Race scheduled successfully! Laps: " + laps);
        } else {
            System.out.println("Failed to schedule race.");
        }
    }

    private static void cancelRace(Scanner sc) {
        List<Race> races = RaceDAO.getAllFutureRaces();
        if (races.isEmpty()) {
            System.out.println("No scheduled races available to cancel.");
            return;
        }
        System.out.println("Scheduled Races:");
        for (Race r : races) {
            System.out.printf("ID: %d | TrackID: %s | Date: %s | Time: %s%n", r.getRaceId(), r.getTrackId(), r.getRaceDate(), r.getRaceTime());
        }

        try {
            int raceId = getValidIntegerInput(sc, "Enter Race ID to cancel: ");

            boolean success = RaceDAO.cancelRace(raceId);
            if (success) {
                System.out.println("Race cancelled Successfully");
            } else {
                System.out.println("Failed to cancel race.");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private static void addCar(Scanner sc) {
        String name = "";
        while (true) {
            System.out.print("Enter Car Name : ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Car name cannot be empty.");
                return;
            }
            if (name.length() < 2) {
                System.out.println("Car name must be at least 2 characters long.");
                return;
            }

            // Regex explanation:
            // ^                 : start of string
            // (?=.*[a-zA-Z])    : must contain at least one alphabet anywhere
            // [a-zA-Z0-9 ]+     : consists of alphabets, digits, and spaces only
            // $                 : end of string
            if (name.matches("^(?=.*[a-zA-Z])[a-zA-Z0-9 ]+$")) {
                break;
            } else {
                System.out.println("Invalid input. Car name must contain at least one letter and may contain digits.");
            }
        }

        long price = 0;
        while (true) {
            System.out.print("Enter Price: ");
            String s = sc.nextLine();
            try { price = Long.parseLong(s);
                if (price <= 0) {
                    System.out.println("Price must be positive.");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int enginePower = 0;
        while (true) {
            System.out.print("Enter Engine Power: ");
            String s = sc.nextLine();
            try {
                enginePower = Integer.parseInt(s);
                if (enginePower <= 0) { System.out.println("Engine power must be positive."); continue; }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int maxSpeed = 0;
        while (true) {
            System.out.print("Enter Max Speed (km/h): ");
            String s = sc.nextLine();
            try {
                maxSpeed = Integer.parseInt(s);
                if (maxSpeed <= 0) {
                    System.out.println("Max speed must be positive.");
                    continue;
                }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        if (CarDAO.addCar(name, price, enginePower, maxSpeed)) System.out.println("Car added successfully.");
        else System.out.println("Failed to add car.");
    }

    private static void addDriver(Scanner sc) {
        String name = getValidNameInput(sc, "Enter Driver Name: ");
        if (name.isEmpty()) {
            System.out.println("Driver name cannot be empty.");
            return;
        }
        if (name.length() < 2) {
            System.out.println("Driver name must be at least 2 characters long.");
            return;
        }

        int skillLevel = 0;
        while (true) {
            System.out.print("Enter Skill Level (1-100): ");
            String s = sc.nextLine();
            try {
                skillLevel = Integer.parseInt(s);
                if (skillLevel < 1 || skillLevel > 100) {
                    System.out.println("Skill level must be between 1 and 100.");
                } else break;
            } catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        String nationality = getValidNameInput(sc, "Enter Driver Nationality: ");
        if (nationality.isEmpty()) {
            System.out.println("Driver nationality cannot be empty.");
            return;
        }
        if (nationality.length() < 2) {
            System.out.println("Driver nationality must be at least 2 characters long.");
            return;
        }

        long rentalPrice = 0;
        while (true) {
            System.out.print("Enter Rental Price per Race: ");
            String s = sc.nextLine();
            try { rentalPrice = Long.parseLong(s);
                if (rentalPrice <= 0) { System.out.println("Rental price must be positive."); continue; }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        if (DriverDAO.addDriver(name, skillLevel, rentalPrice, nationality, Date.valueOf(java.time.LocalDate.now())))
            System.out.println("Driver added successfully.");
        else
            System.out.println("Failed to add driver.");
    }

    private static void addSponsor(Scanner sc) {
        String name = getValidNameInput(sc, "Enter Sponsor Name: ");
        if (name.length() < 2) {
            System.out.println("Sponsor name must be at least 2 characters long.");
            return;
        }

        String industry = getValidNameInput(sc, "Enter Industry: ");
        if (industry.length() < 2) {
            System.out.println("Sponsor name must be at least 2 characters long.");
            return;
        }

        long contractValue = 0;
        while (true) {
            System.out.print("Enter Contract Value: ");
            String s = sc.nextLine();
            try { contractValue = Long.parseLong(s);
                if (contractValue <= 0) { System.out.println("Contract value must be positive."); continue; }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int duration = 12;
        while (true) {
            System.out.print("Enter Contract duration (months): ");
            String s = sc.nextLine();
            try { duration = Integer.parseInt(s);
                if (duration <= 0) { System.out.println("Duration must be positive."); continue; }
                break;
            }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        if (SponsorDAO.addSponsor(name, industry, contractValue, duration))
            System.out.println("Sponsor added successfully.");
        else
            System.out.println("Failed to add sponsor.");
    }

    private static void viewTeamRankings(Scanner sc) {
        System.out.println("\n" + "=".repeat(80));
        System.out.println("                        🏁 TEAM RANKINGS & STATISTICS 🏁");
        System.out.println("=".repeat(80));

        System.out.println("📊 Sort by:");
        System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
        System.out.println("║                              📊 SORTING OPTIONS 📊                          ║");
        System.out.println("╠═════════════════════════════════════════════════════════════════════════════=╣");
        System.out.println("║  1. 🏆 Points (highest first) - Championship ranking                        ║");
        System.out.println("║  2. 🥇 Wins (most wins first) - Victory-based ranking                       ║");
        System.out.println("╚═════════════════════════════════════════════════════════════════════════════=╝");

        int sortChoice = 0;
        while (true) {
            System.out.print("🎯 Choose option (1 or 2): ");
            String sortInput = sc.nextLine().trim();
            if (sortInput.isEmpty()) {
                System.out.println("⚠️ Option cannot be empty. Please choose 1 or 2.");
                continue;
            }
            try {
                sortChoice = Integer.parseInt(sortInput);
                if (sortChoice == 1 || sortChoice == 2) {
                    break;
                } else {
                    System.out.println("⚠️ Invalid option. Please choose 1 or 2.");
                }
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid input. Please enter 1 or 2.");
            }
        }

        String sortBy = (sortChoice == 1) ? "points" : "wins";

        List<Standing> standings = StandingDAO.getSeasonStandings(sortBy);

        if (standings.isEmpty()) {
            System.out.println("❌ No rankings found.");
            return;
        }

        // Enhanced formatted rankings table
        System.out.println("\n┌" + "─".repeat(80) + "┐");
        System.out.printf("│ %-6s │ %-25s │ %-12s │ %-8s │ %-14s │%n",
                "RANK", "TEAM NAME", "POINTS", "WINS", "PERFORMANCE");
        System.out.println("├" + "─".repeat(80) + "┤");

        int rank = 1;
        for (Standing s : standings) {
            String performance = getPerformanceIndicator(s.totalPoints, s.wins);
            String rankDisplay = getRankDisplay(rank);

            System.out.printf("│ %-6s │ %-25s │ %-12d │ %-8d │ %-14s │%n",
                    rankDisplay,
                    truncate(s.teamName, 25),
                    s.totalPoints,
                    s.wins,
                    performance);
            rank++;
        }
        System.out.println("└" + "─".repeat(80) + "┘");

        // Ranking summary
        System.out.println("\n📊 RANKING SUMMARY:");
        System.out.printf("   🥇 1st Place: %s (%d points, %d wins)%n",
                standings.get(0).teamName, standings.get(0).totalPoints, standings.get(0).wins);

        if (standings.size() >= 2) {
            System.out.printf("   🥈 2nd Place: %s (%d points, %d wins)%n",
                    standings.get(1).teamName, standings.get(1).totalPoints, standings.get(1).wins);
        }

        if (standings.size() >= 3) {
            System.out.printf("   🥉 3rd Place: %s (%d points, %d wins)%n",
                    standings.get(2).teamName, standings.get(2).totalPoints, standings.get(2).wins);
        }

        // Season stats
        long totalPoints = standings.stream().mapToLong(s -> s.totalPoints).sum();
        int totalWins = standings.stream().mapToInt(s -> s.wins).sum();
        double avgPoints = standings.size() > 0 ? (double) totalPoints / standings.size() : 0;
        double avgWins = standings.size() > 0 ? (double) totalWins / standings.size() : 0;

        System.out.printf("\n📈 SEASON STATISTICS:%n");
        System.out.printf("   Total Teams: %d%n", standings.size());
        System.out.printf("   Total Points Awarded: %d%n", totalPoints);
        System.out.printf("   Total Wins: %d%n", totalWins);
        System.out.printf("   Average Points per Team: %.1f%n", avgPoints);
        System.out.printf("   Average Wins per Team: %.1f%n", avgWins);

        System.out.println("=".repeat(80));
    }

    private static String getPerformanceIndicator(long points, int wins) {
        if (points >= 100 && wins >= 5) return "🏆 DOMINANT";
        if (points >= 75 && wins >= 3) return "💪 EXCELLENT";
        if (points >= 50 && wins >= 2) return "📈 STRONG";
        if (points >= 25 && wins >= 1) return "👍 GOOD";
        if (points > 0) return "🔄 IMPROVING";
        return "🆕 ROOKIE";
    }

    private static String getRankDisplay(int rank) {
        if (rank == 1) return "🥇 1st";
        if (rank == 2) return "🥈 2nd";
        if (rank == 3) return "🥉 3rd";
        if (rank <= 10) return String.format("%dth", rank);
        return String.valueOf(rank);
    }

    private static void generateSponsorshipOffers() {
        List<Standing> standings = StandingDAO.getSeasonStandings();
        if (standings.isEmpty()) {
            System.out.println("No teams found, cannot generate offers.");
            return;
        }
        List<model.Sponsor> sponsors = SponsorDAO.getAllSponsors();
        if (sponsors.isEmpty()) {
            System.out.println("No sponsors available to generate offers.");
            return;
        }

        // Calculate average sponsor amount
        long totalSponsorAmount = 0;
        for (Sponsor s : sponsors) {
            totalSponsorAmount += s.contractValue;
        }
        double avgSponsorAmount = sponsors.size() > 0 ? (double) totalSponsorAmount / sponsors.size() : 0;

        // Calculate average team points
        long totalTeamPoints = 0;
        for (Standing s : standings) {
            totalTeamPoints += s.totalPoints;
        }
        double avgTeamPoints = standings.size() > 0 ? (double) totalTeamPoints / standings.size() : 0;


        // Partition sponsors
        List<model.Sponsor> highSponsors = new ArrayList<>();
        List<model.Sponsor> lowSponsors = new ArrayList<>();
        for (model.Sponsor s : sponsors) {
            if (s.contractValue > avgSponsorAmount) highSponsors.add(s); else lowSponsors.add(s);
        }
        // Sort sponsors within groups
        highSponsors.sort(Comparator.comparingLong((model.Sponsor s) -> s.contractValue).reversed());
        lowSponsors.sort(Comparator.comparingLong((model.Sponsor s) -> s.contractValue));


        // Partition teams by points, excluding those with 0 points
        List<Standing> highTeams = new ArrayList<>();
        List<Standing> lowTeams = new ArrayList<>();
        for (Standing t : standings) {
            if (t.totalPoints == 0) continue;  // Skip teams with 0 points
            if (t.totalPoints > avgTeamPoints) highTeams.add(t);
            else lowTeams.add(t);
        }


        Comparator<Standing> byPointsDescWinsDesc = new Comparator<Standing>() {

            public int compare(Standing a, Standing b) {
                int cmp = Long.compare(b.totalPoints, a.totalPoints);
                if (cmp != 0) return cmp;
                return Integer.compare(b.wins, a.wins);
            }
        };

        Comparator<Standing> byPointsAscWinsAsc = new Comparator<Standing>() {

            public int compare(Standing a, Standing b) {
                int cmp = Long.compare(a.totalPoints, b.totalPoints);
                if (cmp != 0) return cmp;
                return Integer.compare(a.wins, b.wins);
            }
        };

        highTeams.sort(byPointsDescWinsDesc);
        lowTeams.sort(byPointsAscWinsAsc);
        boolean flag1 = false;
        boolean flag2 = false;
        if (!highTeams.isEmpty()) {
            for (int i = 0; i < highSponsors.size(); i++) {
                Standing team = highTeams.get(i % highTeams.size());
                model.Sponsor sp = highSponsors.get(i);
                if (!SponsorshipOfferDAO.existsActiveOrPendingOffer(team.teamId, sp.name)) {
                    flag1 = flag1 || SponsorshipOfferDAO.generateSponsorship(team.teamId, sp.name, sp.industry, sp.contractValue, sp.contractDurationMonths);
                }
            }
        }
        if (!lowTeams.isEmpty()) {
            for (int i = 0; i < lowSponsors.size(); i++) {
                Standing team = lowTeams.get(i % lowTeams.size());
                model.Sponsor sp = lowSponsors.get(i);
                if (!SponsorshipOfferDAO.existsActiveOrPendingOffer(team.teamId, sp.name)) {
                    flag2 = flag2 || SponsorshipOfferDAO.generateSponsorship(team.teamId, sp.name, sp.industry, sp.contractValue, sp.contractDurationMonths);
                }
            }
        }

        if(flag1 || flag2) {
            System.out.println("Sponsorship offers Generated.");
        } else {
            System.out.println("No Sponsorship Generated");
        }
    }


    private static User userLogin(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return null;
        }

        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return null;
        }

        User u = UserDAO.authenticateUser(username, password);
        if (u != null) System.out.println("Login successful.");
        else System.out.println("Login failed.");

        map.loadUserTeamMap();
        return u;
    }

    private static void createAccount(Scanner sc) {
        System.out.println("---- Create New Account ----");

        // Full Name Input
        String fullName;
        while (true) {
            try {
                System.out.print("Enter Full Name (First and Surname) or 'back' to cancel: ");
                fullName = sc.nextLine().trim();
                if (fullName.equalsIgnoreCase("back")) return;
                if (fullName.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                // Require at least two words (first + surname), letters only
                if (!fullName.matches("[A-Za-z]+(\\s+[A-Za-z]+)+"))
                    throw new IllegalArgumentException("Please enter first name and surname (letters only).");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        // Username Input
        String userName;
        while (true) {
            try {
                System.out.print("Enter Username (letters only, or 'back' to cancel): ");
                userName = sc.nextLine().trim();
                if (userName.equalsIgnoreCase("back")) return;
                if (userName.isEmpty())
                    throw new IllegalArgumentException("Username cannot be empty.");
                if (userName.matches(".*\\d.*"))
                    throw new IllegalArgumentException("Username cannot contain numbers.");
                if (UserDAO.getUserByUsername(userName) != null)
                    throw new IllegalArgumentException("Username already taken.");

                // Protect reserved admin username in DB
                String dbRole = UserDAO.getRoleFromDatabase(userName);
                if (dbRole != null && dbRole.equalsIgnoreCase("admin")) {
                    throw new IllegalArgumentException("This username is reserved for admin and cannot be used.");
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        // Email Input
        String emailID;
        while (true) {
            try {
                System.out.print("Enter Email ID (or 'back' to cancel): ");
                emailID = sc.nextLine().trim();
                if (emailID.equalsIgnoreCase("back")) return;
                if (!emailID.matches("^[A-Za-z0-9+_.-]+@(.+)$") || !emailID.toLowerCase().endsWith("@gmail.com")) {
                    System.out.println("⚠ Invalid email format.");
                    continue;
                }
                if (UserDAO.emailExists(emailID)) {
                    System.out.println("⚠ EmailID already taken.");
                    continue;
                }
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        // Password Input
        String password;
        while (true) {
            try {
                System.out.print("Enter Password (min 8 chars, number, symbol, or 'back' to cancel): ");
                password = sc.nextLine();
                if (password.equalsIgnoreCase("back")) return;

                boolean hasNumber = password.matches(".*\\d.*");
                boolean hasSymbol = password.matches(".*[!@#$%^&*()].*");

                if (!(password.length() >= 8 && hasNumber && hasSymbol))
                    throw new IllegalArgumentException("Password must be at least 8 characters, contain a number and a symbol.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        // Role Input
        String role = "";
        while (true) {
            System.out.println("Choose Role:");
            System.out.println("1. Admin");
            System.out.println("2. User");
            System.out.print("Enter choice (1 or 2): ");
            String choice = sc.nextLine().trim();
            if (choice.equals("1")) {
                role = "admin";
                break;
            } else if (choice.equals("2")) {
                role = "user";
                break;
            } else {
                System.out.println("⚠ Choose a valid role (1 or 2).");
            }
        }

        // Referral Number Input if admin
        boolean isAdminSuccess = false;
        String accessKey = null;
        if (role.equalsIgnoreCase("admin")) {
            while (true) {
                System.out.print("Enter Referral Number (or type 'back' to cancel): ");
                accessKey = sc.nextLine().trim();
                if (accessKey.equalsIgnoreCase("back")) return;
                if (accessKey.isEmpty()) {
                    System.out.println("⚠ Referral number is required to create admin account.");
                    continue;
                }
                if (!UserDAO.isValidAdminAccessKey(accessKey)) {
                    System.out.println("⚠ Invalid referral number. Cannot create admin account.");
                    continue;
                }
                isAdminSuccess = true;
                break;
            }
        }

        if(isAdminSuccess) {
            String newAccessKey;
            while (true) {
                try {
                    System.out.print("Enter New Access Key for this admin (or 'back' to cancel): ");
                    newAccessKey = sc.nextLine().trim();
                    if (newAccessKey.equalsIgnoreCase("back")) return;
                    if (newAccessKey.isEmpty()) {
                        throw new IllegalArgumentException("Access key cannot be empty.");
                    }
                    if (newAccessKey.length() < 6) {
                        throw new IllegalArgumentException("Access key must be at least 6 characters long.");
                    }
                    if (UserDAO.isValidAdminAccessKey(newAccessKey)) {
                        throw new IllegalArgumentException("Access key already exists. Choose a unique one.");
                    }
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠ " + e.getMessage());
                }
            }

            User newAdmin = new User(fullName, userName, emailID, password, role);
            boolean isCreatedAdmin = UserDAO.registerAdmin(newAdmin, newAccessKey);
            if (isCreatedAdmin) {
                System.out.println("Account created successfully.");
            } else {
                System.out.println("Failed to create account. Username or email may be in use, or referral invalid.");
            }
            return;
        }

        // Team Information Input
        System.out.println("\n--- Team Information ---");

        String teamName;
        while (true) {
            try {
                System.out.print("Enter Team Name (or 'back' to cancel): ");
                teamName = sc.nextLine().trim();
                if (teamName.equalsIgnoreCase("back")) return;
                if (teamName.isEmpty()) throw new IllegalArgumentException("Team name cannot be empty.");
                if (teamName.length() < 2) throw new IllegalArgumentException("Team name must be at least 2 characters long.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        String teamOrigin;
        while (true) {
            try {
                System.out.print("Enter Team Origin (or 'back' to cancel): ");
                teamOrigin = sc.nextLine().trim();
                if (teamOrigin.equalsIgnoreCase("back")) return;
                if (teamOrigin.isEmpty()) throw new IllegalArgumentException("Team origin cannot be empty.");
                if (teamOrigin.length() < 2) throw new IllegalArgumentException("Team origin must be at least 2 characters long.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        // Create User Object
        User newUser = new User(fullName, userName, emailID, password);
        boolean isCreatedUser = UserDAO.registerUser(newUser, teamName, teamOrigin);

        if (isCreatedUser) {
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Failed to create account. Username or email may be in use, or referral invalid.");
        }

        map.loadUserTeamMap();
    }


    private static void userMenu(Scanner sc, User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("                              👤 USER DASHBOARD 👤");
            System.out.println("=".repeat(80));
            System.out.println("╔══════════════════════════════════════════════════════════════════════════════╗");
            System.out.println("║                           🏎️ TEAM DASHBOARD 🏎️                               ║");
            System.out.println("╠══════════════════════════════════════════════════════════════════════════════╣");
            System.out.println("║  📊 01. View Team Statistics                                                 ║");
            System.out.println("║  🚗 02. Buy Car                                                              ║");
            System.out.println("║  👨‍💼 03. Purchase Driver                                                      ║");
            System.out.println("║  🏁 04. View Upcoming Races                                                  ║");
            System.out.println("║  📝 05. Register Team for Upcoming Race                                      ║");
            System.out.println("║  🏆 06. View Race Results                                                    ║");
            System.out.println("║  💼 07. View Sponsorship Requests                                            ║");
            System.out.println("║  💰 08. View My Sponsors                                                     ║");
            System.out.println("║  🔍 09. Search Team by Username                                              ║");
            System.out.println("║  👨‍💼 10. View Driver Statistics                                               ║");
            System.out.println("║  🚗 11. View Car Statistics                                                  ║");
            System.out.println("║  📊 12. View Team Standings                                                  ║");
            System.out.println("║  🚪 13. Logout                                                               ║");
            System.out.println("╚══════════════════════════════════════════════════════════════════════════════╝");

            System.out.print("🎯 Option: ");
            String input = sc.nextLine().trim();

            int choice;
            try {
                choice = Integer.parseInt(input.replaceFirst("^0+(?!$)", ""));
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Invalid option.");
                continue;
            }
            switch (choice) {
                case 1:
                    viewMyTeamStatistics(user.teamId);
                    break;
                case 2:
                    buyCar(sc, user);
                    break;
                case 3:
                    rentDriver(sc, user);
                    break;
                case 4:
                    viewUpcomingRaces();
                    break;
                case 5:
                    registerTeamForRace(sc, user);
                    break;
                case 6:
                    viewRaceResults();
                    break;
                case 7:
                    handleSponsorshipRequests(sc, user);
                    break;
                case 8:
                    viewSponsors(sc, user);
                    break;
                case 9:
                    System.out.print("🔍 Enter username to search: ");
                    String username = sc.nextLine().trim();
                    if (username.isEmpty()) {
                        System.out.println("Username cannot be empty.");
                        break;
                    }
                    User searchedUser = UserDAO.getUserByUsername(username);
                    if (searchedUser == null || searchedUser.teamId == 0) {
                        System.out.println("❌ User or team not found.");
                    } else {
                        displayTeamStatistics(searchedUser.teamId);
                    }
                    break;
                case 10:
                    viewDriverStatistics(sc, user.teamId);
                    break;
                case 11:
                    viewCarStatistics(sc, user.teamId);
                    break;
                case 12 :
                    viewTeamRankings(sc);
                    break;
                case 13:
                    loggedIn = false;
                    System.out.println("👋 Logged out.");
                    break;
                default:
                    System.out.println("⚠ Invalid option.");
            }
        }
    }

    private static void buyCar(Scanner sc, User user) {
        List<Car> cars = CarDAO.getAllAvailableCars();
        if (cars.isEmpty()) {
            System.out.println("No cars available for purchase.");
            return;
        }

        System.out.println("Available Cars for Purchase:");
        System.out.println("┌───────┬───────────────────────────┬─────────────┬───────────┬────────────┐");
        System.out.printf("│ %-5s │ %-25s │ %-11s│ %-9s │ %-10s │%n", "ID", "Car Name", "Engine Power", "Max Speed", "Price");
        System.out.println("├───────┼───────────────────────────┼─────────────┼───────────┼────────────┤");

        for (Car car : cars) {
            System.out.printf("│ %-5d │ %-25s │ %-11d │ %-9d │ %,10d │%n",
                    car.carId, car.carName, car.enginePower, car.maxSpeed, car.price);
        }

        System.out.println("└───────┴───────────────────────────┴─────────────┴───────────┴────────────┘");

        System.out.print("Enter Car ID to purchase (or 0 to cancel): ");
        String input = sc.nextLine();
        int carId;
        try {
            carId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid input. Please enter a valid Car ID or 0 to cancel.");
            return;
        }
        if (carId == 0) {
            System.out.println("Purchase cancelled.");
            return;
        }

        Car car = CarDAO.getCarById(carId);
        if (car == null) {
            System.out.println("⚠ Car not found with ID: " + carId);
            return;
        }

        Team team = TeamDAO.getTeamById(user.teamId);
        if (team == null) {
            System.out.println("Team not found. Cannot proceed with purchase.");
            return;
        }
        if (team.budget < car.price) {
            System.out.println("⚠ Not enough budget to buy this car. Your budget: " + team.budget + ", Car price: " + car.price);
            return;
        }

        if (CarDAO.assignCarToTeam(carId, team.teamId, car.price)) {
            TeamDAO.updateBudget(team.teamId, team.budget - car.price);
            System.out.printf("✅ Car '%s' purchased successfully. Remaining budget: %,d%n", car.carName, team.budget - car.price);
        } else {
            System.out.println("⚠ Failed to purchase car. Please try again later.");
        }
    }

    private static void rentDriver(Scanner sc, User user) {
        List<Driver> drivers = DriverDAO.getAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available for rent.");
            return;
        }

        System.out.println("Available Drivers for Rent:");
        System.out.println("┌───────┬───────────────────────────┬─────────────┬───────────────┐");
        System.out.printf("│ %-5s │ %-25s │ %-11s │ %-13s │%n", "ID", "Driver Name", "Skill Level", "Rental Price");
        System.out.println("├───────┼───────────────────────────┼─────────────┼───────────────┤");

        for (Driver driver : drivers) {
            System.out.printf("│ %-5d │ %-25s │ %-11d │ %,13d │%n",
                    driver.driverId, driver.driverName, driver.skillLevel, driver.rentalPrice);
        }

        System.out.println("└───────┴───────────────────────────┴─────────────┴───────────────┘");

        System.out.print("Enter Driver ID to rent (or 0 to cancel): ");
        String input = sc.nextLine();
        int driverId;
        try {
            driverId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("⚠ Invalid input. Please enter a valid Driver ID or 0 to cancel.");
            return;
        }
        if (driverId == 0) {
            System.out.println("Rental cancelled.");
            return;
        }

        Driver driver = DriverDAO.getDriverById(driverId);
        if (driver == null) {
            System.out.println("⚠ Driver not found with ID: " + driverId);
            return;
        }

        Team team = TeamDAO.getTeamById(user.teamId);
        if (team == null) {
            System.out.println("Team not found. Cannot proceed with rent.");
            return;
        }
        if (team.budget < driver.rentalPrice) {
            System.out.println("⚠ Not enough budget to rent this driver. Your budget: " + team.budget + ", Rental price: " + driver.rentalPrice);
            return;
        }

        if (DriverDAO.assignDriverToTeam(driverId, team.teamId, driver.rentalPrice)) {
            System.out.printf("✅ Driver '%s' rented successfully. Remaining budget: %,d%n", driver.driverName, team.budget - driver.rentalPrice);
        } else {
            System.out.println("⚠ Failed to rent driver. Please try again later.");
        }
    }

    private static boolean viewUpcomingRaces() {
        var races = RaceDAO.getAllFutureRaces();  // ✅ Now gets only future races
        if (races.isEmpty()) {
            System.out.println("No upcoming races.");
            return false;
        }

        // Demonstrate manual data structure (BST) by ordering races by date-time
        BinarySearchTree<LocalDateTime, Race> bst = new BinarySearchTree<LocalDateTime, Race>();
        for (var race : races) {
            LocalDateTime raceDateTime = LocalDateTime.of(
                    race.getRaceDate().toLocalDate(),
                    race.getRaceTime().toLocalTime()
            );
            bst.insert(raceDateTime, race);
        }

        System.out.println("Upcoming Races:");
        System.out.printf("%-8s %-10s %-12s %-10s %-6s%n", "Race ID", "Track ID", "Date", "Time", "Laps");
        System.out.println("--------------------------------------------------------");
        for (var race : bst.inOrderValues()) {
            System.out.printf("%-8d %-10d %-12s %-10s %-6d%n", race.getRaceId(), race.getTrackId(), race.getRaceDate(), race.getRaceTime(), race.getLaps());
        }
        return true;
    }

    private static void registerTeamForRace(Scanner sc, User user) {
        if (!viewUpcomingRaces()) {
            System.out.println("No Upcoming Race!");
            return;  // No upcoming races
        }

        // 1. Select race to register
        int raceId = 0;
        while (true) {
            System.out.print("Enter Race ID to register (0 to cancel): ");
            String input = sc.nextLine();
            try {
                raceId = Integer.parseInt(input);
                if (raceId == 0) return;
                var races = RaceDAO.getAllRaces();
                int finalRaceId = raceId;
                boolean validRace = races.stream().anyMatch(r -> r.getRaceId() == finalRaceId);
                if (validRace) break;
                else System.out.println("Invalid Race ID.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }

        // 1. Check if team has cars
        List<Car> teamCars = CarDAO.getCarsByTeam(user.teamId);
        if (teamCars.isEmpty()) {
            System.out.println("Your team does not have any cars registered. Cannot participate.");
            return;
        }

        // 2. Check if team has drivers
        List<Driver> teamDrivers = DriverDAO.getDriversByTeam(user.teamId);
        if (teamDrivers.isEmpty()) {
            System.out.println("Your team does not have any drivers registered. Cannot participate.");
            return;
        }

        // 3. Ask user to choose a car
        System.out.println("Select a car from your team:");
        for (Car car : teamCars) {
            System.out.printf("%d: %s%n", car.carId, car.carName);
        }
        int carId = 0;
        while (true) {
            System.out.print("Enter Car ID: ");
            try {
                carId = Integer.parseInt(sc.nextLine());
                int finalCarId = carId;
                boolean validCar = teamCars.stream().anyMatch(c -> c.carId == finalCarId);
                if (validCar) break;
                else System.out.println("Invalid Car ID, try again.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }

        // 4. Ask user to choose a driver
        System.out.println("Select a driver from your team:");
        for (Driver driver : teamDrivers) {
            System.out.printf("%d: %s%n", driver.driverId, driver.driverName);
        }
        int driverId = 0;
        while (true) {
            System.out.print("Enter Driver ID: ");
            try {
                driverId = Integer.parseInt(sc.nextLine());
                int finalDriverId = driverId;
                boolean validDriver = teamDrivers.stream().anyMatch(d -> d.driverId == finalDriverId);
                if (validDriver) break;
                else System.out.println("Invalid Driver ID, try again.");
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid number.");
            }
        }

        // 6. Register participation with car and driver info
        boolean success = RaceParticipationDAO.registerTeamForRace(raceId, user.teamId, carId, driverId);
        if (success) {
            System.out.println("Registered for race successfully.");
        } else {
            System.out.println("Registration failed or already registered.");
        }
    }

    private static void viewSponsors(Scanner sc, User user) {
        List<SponsorshipOffer> sponsors = SponsorshipOfferDAO.getActiveSponsorshipOffersForTeam(user.teamId);
        if (sponsors.isEmpty()) {
            System.out.println("You currently have no sponsors.");
        } else {
            for (SponsorshipOffer s : sponsors) {
                System.out.println(s);
            }
        }
    }

    private static String truncate(String str, int length) {
        if (str == null) return "N/A";
        return str.length() > length ? str.substring(0, length-3) + "..." : str;
    }

    private static void viewRaceResults() {
        // Simulate past races once (choose autoSimulatePastRaces or simulatePastRaces based on your logic)
        RaceResultDAO.autoSimulatePastRaces();

        List<dao.RaceResultDAO.RaceResult> allRaces = RaceResultDAO.getAllRaceResults();

        if (allRaces.isEmpty()) {
            System.out.println("No races have been scheduled yet.");
            return;
        }

        System.out.println("\n" + "🏁".repeat(13));
        System.out.println("🏁 RACE RESULTS & SCHEDULE 🏁");
        System.out.println("🏁".repeat(13));

        int currentRaceId = -1;

        for (dao.RaceResultDAO.RaceResult row : allRaces) {
            if (row.raceId != currentRaceId) {
                currentRaceId = row.raceId;
                LocalDateTime raceDateTime = LocalDateTime.of(
                        row.raceDate.toLocalDate(),
                        row.raceTime.toLocalTime()
                );
                String dateTimeStr = raceDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                System.out.println("\n" + "─".repeat(100));
                System.out.printf("🏁 RACE #%d | 🏟 Track: %s | ⏰ When: %s | 🔄 Laps: %d\n",
                        row.raceId, row.trackName, dateTimeStr, row.laps);
                System.out.println("─".repeat(100));

                LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
                if (raceDateTime.isAfter(now)) {
                    // Future race
                    System.out.println("📅 Status: Scheduled for future");

                    List<RaceParticipation> participants = RaceParticipationDAO.getParticipationsByRaceId(row.raceId);
                    if (!participants.isEmpty()) {
                        System.out.printf("👥 Registered Teams: %d\n", participants.size());
                        System.out.println("📋 Participants:");
                        for (RaceParticipation p : participants) {
                            System.out.printf("   • Team ID: %d\n", p.teamId);
                        }
                    } else {
                        System.out.println("⚠ No teams registered yet");
                    }
                    continue;
                }

                // Past or current race: Fetch detailed results
                List<RaceResultDAO.DetailedRaceResult> results =
                        RaceResultDAO.getDetailedRaceResults(row.raceId);

                if (results.isEmpty()) {
                    List<RaceParticipation> participants = RaceParticipationDAO.getParticipationsByRaceId(row.raceId);
                    if (!participants.isEmpty()) {
                        System.out.println("⚠ Status: Race completed but results not yet processed");
                        System.out.printf("👥 Participants: %d teams\n", participants.size());
                    } else {
                        System.out.println("⚠ Status: No participants registered for this race");
                    }
                } else {
                    System.out.println("🏆 FINAL RESULTS:");
                    System.out.println("┌" + "─".repeat(123) + "┐");
                    System.out.printf("│ %-4s │ %-20s │ %-20s │ %-20s │ %8s │ %8s │ %8s │ %12s │%n",
                            "Pos", "Team", "Driver", "Car", "Points", "Skill", "Power", "Budget");
                    System.out.println("├" + "─".repeat(123) + "┤");
                    for (RaceResultDAO.DetailedRaceResult r : results) {
                        String positionSymbol = getPositionSymbol(r.position);
                        System.out.printf("│ %-4s │ %-20s │ %-20s │ %-20s │ %8d │ %8d │ %8d │ %,12d │%n",
                                positionSymbol,
                                truncate(r.teamName, 20),
                                truncate(r.driverName, 20),
                                truncate(r.carName, 20),
                                r.points,
                                r.driverSkill,
                                r.carPower,
                                r.teamBudget);
                    }
                    System.out.println("└" + "─".repeat(123) + "┘");
                }
            }
        }
        System.out.println();
    }

    private static String getPositionSymbol(int position) {
        if (position == 1) return "🥇";
        if (position == 2) return "🥈";
        if (position == 3) return "🥉";
        if (position <= 10) return String.valueOf(position);
        return String.valueOf(position);
    }

    private static void viewDriverStatistics(Scanner sc, int teamId) {
        List<Driver> drivers = DriverDAO.getDriversByTeam(teamId);
        if (drivers.isEmpty()) {
            System.out.println("❌ No Driver found in your team.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("👨‍💼 DRIVER STATISTICS & PERFORMANCE 👨‍💼");
        System.out.println("=".repeat(80));

        listDriverIdsAndNames(drivers);

        System.out.print("\n📊 Enter Driver ID to view statistics: ");

        String input = sc.nextLine();
        int driverId;
        try {
            driverId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input.");
            return;
        }
        DriverStatsDAO.DriverStats stats = DriverStatsDAO.getDriverStats(driverId, teamId);
        if (stats == null) {
            System.out.println("❌ Driver not found or no stats available.");
            return;
        }

        System.out.println("\n🏆 DRIVER PERFORMANCE REPORT:");
        System.out.println("┌" + "─".repeat(60) + "┐");
        System.out.printf("│ %-20s │ %-35s │\n", "STATISTIC", "VALUE");
        System.out.println("├" + "─".repeat(60) + "┤");
        System.out.printf("│ %-20s │ %-35s │\n", "Driver Name", stats.driverName);
        System.out.printf("│ %-20s │ %-35d │\n", "Total Races", stats.totalRaces);
        System.out.printf("│ %-20s │ %-35d │\n", "Wins", stats.wins);
        System.out.printf("│ %-20s │ %-35d │\n", "Total Points", stats.totalPoints);

        // Calculate additional stats
        double winRate = stats.totalRaces > 0 ? (double) stats.wins / stats.totalRaces * 100 : 0;
        double avgPoints = stats.totalRaces > 0 ? (double) stats.totalPoints / stats.totalRaces : 0;

        System.out.printf("│ %-20s │ %-30.1f%%│\n", "Win Rate", winRate);
        System.out.printf("│ %-20s │ %-35.1f │\n", "Average Points/Race", avgPoints);
        System.out.println("└" + "─".repeat(60) + "┘");

        // Performance analysis
        System.out.println("\n📈 PERFORMANCE ANALYSIS:");
        if (winRate >= 50) {
            System.out.println("   🏆 EXCEPTIONAL: This driver is a race winner!");
        } else if (winRate >= 25) {
            System.out.println("   💪 STRONG: Consistent podium contender");
        } else if (winRate >= 10) {
            System.out.println("   📈 GOOD: Regular points scorer");
        } else if (winRate > 0) {
            System.out.println("   🔄 DEVELOPING: Shows potential");
        } else {
            System.out.println("   🆕 ROOKIE: Still building experience");
        }

        System.out.println("=".repeat(80));
    }

    public static void listDriverIdsAndNames(List<Driver> drivers) {
        System.out.println("📋 AVAILABLE DRIVERS:");
        System.out.println("┌" + "─".repeat(40) + "┐");
        System.out.printf("│ %-5s │ %-30s │\n", "ID", "Driver Name");
        System.out.println("├" + "─".repeat(40) + "┤");
        for (Driver driver : drivers) {
            System.out.printf("│ %-5d │ %-30s │\n", driver.driverId, driver.driverName);
        }
        System.out.println("└" + "─".repeat(40) + "┘");
    }

    private static void viewCarStatistics(Scanner sc, int teamId) {
        List<Car> cars = CarDAO.getCarsByTeam(teamId);
        if (cars.isEmpty()) {
            System.out.println("❌ No cars found in your team.");
            return;
        }

        System.out.println("\n" + "=".repeat(80));
        System.out.println("🚗 CAR STATISTICS & PERFORMANCE 🚗");
        System.out.println("=".repeat(80));

        listCarIdsAndNames(cars);

        System.out.print("\n📊 Enter Car ID to view statistics: ");
        String input = sc.nextLine();
        int carId;
        try {
            carId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input.");
            return;
        }

        CarStatsDAO.CarStats stats = CarStatsDAO.getCarStats(carId, teamId);
        if (stats == null) {
            System.out.println("❌ Car not found or no stats available.");
            return;
        }


        System.out.println("\n🏆 CAR PERFORMANCE REPORT:");
        System.out.println("┌" + "─".repeat(60) + "┐");
        System.out.printf("│ %-20s │ %-35s │\n", "STATISTIC", "VALUE");
        System.out.println("├" + "─".repeat(60) + "┤");
        System.out.printf("│ %-20s │ %-35s │\n", "Car Name", stats.carName);
        System.out.printf("│ %-20s │ %-35d │\n", "Total Races", stats.totalRaces);
        System.out.printf("│ %-20s │ %-35d │\n", "Wins", stats.wins);
        System.out.printf("│ %-20s │ %-35d │\n", "Total Points", stats.totalPoints);

        // Calculate additional stats
        double winRate = stats.totalRaces > 0 ? (double) stats.wins / stats.totalRaces * 100 : 0;
        double avgPoints = stats.totalRaces > 0 ? (double) stats.totalPoints / stats.totalRaces : 0;

        System.out.printf("│ %-20s │ %-35.1f%% │\n", "Win Rate", winRate);
        System.out.printf("│ %-20s │ %-35.1f │\n", "Average Points/Race", avgPoints);
        System.out.println("└" + "─".repeat(60) + "┘");

        // Performance analysis
        System.out.println("\n📈 PERFORMANCE ANALYSIS:");
        if (winRate >= 50) {
            System.out.println("   🏆 EXCEPTIONAL: This car is a race winner!");
        } else if (winRate >= 25) {
            System.out.println("   💪 STRONG: Consistent podium contender");
        } else if (winRate >= 10) {
            System.out.println("   📈 GOOD: Regular points scorer");
        } else if (winRate > 0) {
            System.out.println("   🔄 DEVELOPING: Shows potential");
        } else {
            System.out.println("   🆕 ROOKIE: Still building experience");
        }

        System.out.println("=".repeat(85));

    }

    public static void listCarIdsAndNames(List<Car> cars) {
        System.out.println("📋 AVAILABLE CARS:");
        System.out.println("┌" + "─".repeat(40) + "┐");
        System.out.printf("│ %-5s │ %-30s │\n", "ID", "Car Name");
        System.out.println("├" + "─".repeat(40) + "┤");
        for (Car car : cars) {
            System.out.printf("│ %-5d │ %-30s │\n", car.carId, car.carName);
        }
        System.out.println("└" + "─".repeat(40) + "┘");

    }

    private static void handleSponsorshipRequests(Scanner sc, User user) {
        List<SponsorshipOffer> offers = SponsorshipOfferDAO.getPendingOffersByTeamId(user.teamId);
        if (offers.isEmpty()) {
            System.out.println("No pending sponsorship offers.");
            return;
        }

        System.out.println("Pending Sponsorship Offers:");
        for (SponsorshipOffer offer : offers) {
            System.out.println(offer);
        }

        while (true) {
            System.out.print("Enter Offer ID to respond (or 0 to exit): ");
            String input = sc.nextLine();
            int offerId;
            try {
                offerId = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input.");
                continue;
            }
            if (offerId == 0) {
                System.out.println("Exiting sponsorship requests.");
                break;
            }

            SponsorshipOffer selectedOffer = null;
            for (SponsorshipOffer o : offers) {
                if (o.offerId == offerId) {
                    selectedOffer = o;
                    break;
                }
            }

            if (selectedOffer == null) {
                System.out.println("Offer ID not found.");
                continue;
            }

            System.out.print("Accept or Decline (a/d): ");
            String response = sc.nextLine().trim().toLowerCase();
            if ("a".equals(response)) {
                boolean budgetUpdated = SponsorshipOfferDAO.addSponsorshipAmountToTeam(user.teamId, selectedOffer.amount);
                boolean statusUpdated = SponsorshipOfferDAO.updateOfferStatus(selectedOffer.offerId, "accepted");
                if (budgetUpdated && statusUpdated) {
                    System.out.println("Offer accepted. Budget updated by " + selectedOffer.amount);
                } else {
                    System.out.println("Error processing acceptance.");
                }
                break;
            } else if ("d".equals(response)) {
                boolean statusUpdated = SponsorshipOfferDAO.updateOfferStatus(selectedOffer.offerId, "declined");
                if (statusUpdated) System.out.println("Offer declined.");
                else System.out.println("Error processing decline.");
                break;
            } else {
                System.out.println("Invalid response. Please enter 'a' or 'd'.");
            }
        }
    }

    private static void displayTeamStatistics(int teamId) {
        map.loadUserTeamMap();
        Team userTeam = map.getTeamByUserId(teamId);

        if (userTeam != null) {
            System.out.println("Your Team Statistics:");
            System.out.println("Team Name: " + userTeam.teamName);
            System.out.println("Budget: $" + userTeam.budget);
            System.out.println("Origin: " + userTeam.origin);
            System.out.println("Founded: " + userTeam.foundingYear);
            System.out.println("Email: " + userTeam.email);
        } else {
            System.out.println("User has no assigned team.");
        }
        System.out.println();
    }

    private static void viewMyTeamStatistics(int teamId) {
        Team team = TeamDAO.getTeamById(teamId);
        if (team == null) {
            System.out.println("❌ Team not found.");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("🏁 TEAM STATISTICS & INFORMATION 🏁");
        System.out.println("=".repeat(50));

        System.out.println("📋 TEAM DETAILS:");
        System.out.printf("   🏆 Team Name: %s\n", team.teamName);
        System.out.printf("   💰 Budget: $%,d\n", team.budget);
        System.out.printf("   🌍 Origin: %s\n", team.origin);
        System.out.printf("   📅 Founded: %d\n", team.foundingYear);
        System.out.printf("   📧 Email: %s\n", team.email);

        List<Car> cars = CarDAO.getCarsByTeam(teamId);
        System.out.println("\n🚗 OWNED CARS (" + cars.size() + "):");
        if (cars.isEmpty()) {
            System.out.println("   ⚠ No cars owned");
        } else {
            for (Car c : cars) {
                System.out.printf("   • %s (Power: %d, Max Speed: %d km/h, Price: $%,d)\n", c.carName, c.enginePower, c.maxSpeed, c.price);
            }
        }

        List<Driver> drivers = DriverDAO.getDriversByTeam(teamId);
        System.out.println("\n👨‍💼 RENTED DRIVERS (" + drivers.size() + "):");
        if (drivers.isEmpty()) {
            System.out.println("   ⚠ No drivers rented");
        } else {
            for (Driver d : drivers) {
                System.out.printf("   • %s (Skill Level: %d, Rental Price: $%,d)\n",
                        d.driverName, d.skillLevel, d.rentalPrice);
            }
        }

        List<Standing> standings = StandingDAO.getSeasonStandings();
        boolean foundStanding = false;
        for (Standing s : standings) {
            if (s.teamId == teamId) {
                System.out.println("\n🏆 SEASON PERFORMANCE:");
                System.out.printf("   📊 Current Season Points: %d\n", s.totalPoints);
                System.out.printf("   🏁 Wins: %d\n", s.wins);

                // Calculate rank
                int rank = 1;
                for (Standing other : standings) {
                    if (other.totalPoints > s.totalPoints) rank++;
                }
                System.out.printf("   🥇 Current Rank: %d/%d\n", rank, standings.size());

                // Performance analysis
                if (rank == 1) {
                    System.out.println("   🎯 Status: 🥇 CHAMPION");
                } else if (rank == 2) {
                    System.out.println("   🎯 Status: \uD83E\uDD48 RUNNER-UP");
                } else if (rank == 3) {
                    System.out.println("   🎯 Status: \uD83E\uDD49 THIRD PLACE");
                } else if (rank <= 5) {
                    System.out.println("   🎯 Status: 📈 TOP 5 TEAM");
                } else if (rank <= 10) {
                    System.out.println("   🎯 Status: 💪 MIDFIELD TEAM");
                } else {
                    System.out.println("   🎯 Status: 🔄 DEVELOPING TEAM");
                }

                foundStanding = true;
                break;
            }
        }  if (!foundStanding) {
            System.out.println("\n⚠ No season standings available for this team");
        }

        System.out.println("=".repeat(80));
        System.out.println();
    }


    //Helper Methods
    private static String getValidNameInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            // Regex to allow alphabets, spaces, and commas
            if (input.matches("[a-zA-Z ,]+")) {
                return input;
            } else {
                System.out.println("Invalid input. Please enter alphabets only.");
            }
        }
    }

    private static int getValidIntegerInput(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine();

            try {
                int number = Integer.parseInt(input);
                return number;
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }


}
