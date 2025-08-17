import dao.*;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.sql.Date;

public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("====== RevForce Motorsports ======");
            System.out.println("1. Login as Admin");
            System.out.println("2. Login as User");
            System.out.println("3. Create New Account");
            System.out.println("4. Exit");
            System.out.print("Choose option: ");
            String option = sc.nextLine();

            switch (option) {
                case "1":
                    if(adminLogin(sc)) adminMenu(sc);
                    else System.out.println("Incorrect admin credentials.");
                    break;

                case "2":
                    User user = userLogin(sc);
                    if (user != null && user.teamId != 0) userMenu(sc, user);
                    else System.out.println("Invalid username or password, or no team assigned.");
                    break;

                case "3":
                    createAccount(sc);
                    break;

                case "4":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
            System.out.println();
        }
        sc.close();
    }



    private static boolean adminLogin(Scanner sc) {
        System.out.print("Enter admin username: ");
        String adminUser = sc.nextLine();
        System.out.print("Enter admin password: ");
        String adminPass = sc.nextLine();
        boolean isFound = UserDAO.authenticateAdmin(adminUser, adminPass);
        if (isFound) {
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Login failed.");
        }
        return false;
    }

    private static void adminMenu(Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("Admin Menu:");
            System.out.println("1. Add New Track");
            System.out.println("2. Add New Car");
            System.out.println("3. Add New Driver");
            System.out.println("4. Add Dummy Sponsor");
            System.out.println("5. Schedule Race");
            System.out.println("6. Cancel Race");
            System.out.println("7. View Team Rankings");
            System.out.println("8. Generate Sponsorship Offers");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");
            String input = sc.nextLine();
            switch (input) {
                case "1": addNewTrack(sc); break;
                case "2": addCar(sc); break;
                case "3": addDriver(sc); break;
                case "4": addDummySponsor(sc); break;
                case "5" : scheduleRace(sc); break;
                case "6" : cancelRace(sc); break;
                case "7": viewTeamRankings(sc); break;
                case "8": generateSponsorshipOffers(); break;
                case "9": running = false; System.out.println("Admin logged out."); break;
                default: System.out.println("Invalid choice."); break;
            }
        }
    }

    private static void addNewTrack(Scanner sc) {
        System.out.print("Enter Track Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Location: ");
        String location = sc.nextLine();

        float lengthKm = 0f;
        while (true) {
            System.out.print("Enter Length (km): ");
            String s = sc.nextLine();
            try { lengthKm = Float.parseFloat(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        System.out.print("Enter Difficulty Level (Easy/Medium/Hard): ");
        String difficulty = sc.nextLine();
        if (!difficulty.matches("Easy|Medium|Hard")) difficulty = "Medium";

        System.out.print("Enter Track Type (Street/Circuit/Off-road): ");
        String type = sc.nextLine();
        if (!type.matches("Street|Circuit|Off-road")) type = "Circuit";

        System.out.print("Enter Track Image URL: ");
        String url = sc.nextLine();

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

        System.out.printf("%-5s %-20s %-15s %-8s %-10s %-10s%n", "ID", "Name", "Location", "Length", "Type", "Difficulty");
        System.out.println("-------------------------------------------------------------------------------");
        for (Track t : tracks) {
            System.out.printf("%-5d %-20s %-15s %-8.2f %-10s %-10s%n",
                    t.trackId, t.name, t.location, t.lengthKm, t.trackType, t.difficultyLevel);
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

        System.out.print("Enter Race ID to cancel: ");
        int raceId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter cancellation reason (optional): ");
        String reason = sc.nextLine();

        boolean success = RaceDAO.cancelRace(raceId, reason);
        if (success) {
            System.out.println("Race cancelled and moved to history.");
        } else {
            System.out.println("Failed to cancel race.");
        }
    }

    private static void addCar(Scanner sc) {
        System.out.print("Enter Car Name: ");
        String name = sc.nextLine();

        long price = 0;
        while (true) {
            System.out.print("Enter Price: ");
            String s = sc.nextLine();
            try { price = Long.parseLong(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int enginePower = 0;
        while (true) {
            System.out.print("Enter Engine Power: ");
            String s = sc.nextLine();
            try { enginePower = Integer.parseInt(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int maxSpeed = 0;
        while (true) {
            System.out.print("Enter Max Speed (km/h): ");
            String s = sc.nextLine();
            try { maxSpeed = Integer.parseInt(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        if (CarDAO.addCar(name, price, enginePower, maxSpeed)) System.out.println("Car added successfully.");
        else System.out.println("Failed to add car.");
    }

    private static void addDriver(Scanner sc) {
        System.out.print("Enter Driver Name: ");
        String name = sc.nextLine();

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

        System.out.print("Enter Driver Nationality: ");
        String nationality = sc.nextLine();

        long rentalPrice = 0;
        while (true) {
            System.out.print("Enter Rental Price per Race: ");
            String s = sc.nextLine();
            try { rentalPrice = Long.parseLong(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        boolean available = true; // Assuming new driver is available by default

        if (DriverDAO.addDriver(name, skillLevel, 0, rentalPrice, available, Date.valueOf(java.time.LocalDate.now())))
            System.out.println("Driver added successfully.");
        else
            System.out.println("Failed to add driver.");
    }

    private static void addDummySponsor(Scanner sc) {
        System.out.print("Enter Sponsor Name: ");
        String name = sc.nextLine();

        System.out.print("Enter Industry: ");
        String industry = sc.nextLine();

        long contractValue = 0;
        while (true) {
            System.out.print("Enter Contract Value: ");
            String s = sc.nextLine();
            try { contractValue = Long.parseLong(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        int duration = 12;
        while (true) {
            System.out.print("Enter Contract duration (months): ");
            String s = sc.nextLine();
            try { duration = Integer.parseInt(s); break; }
            catch (NumberFormatException e) { System.out.println("Invalid input."); }
        }

        if (SponsorDAO.addSponsor(name, industry, contractValue, duration))
            System.out.println("Sponsor added successfully.");
        else
            System.out.println("Failed to add sponsor.");
    }

    private static void viewTeamRankings(Scanner sc) {
        System.out.print("Sort by (points/wins): ");
        String sortBy = sc.nextLine();
        if (!sortBy.equalsIgnoreCase("points") && !sortBy.equalsIgnoreCase("wins")) {
            sortBy = "points";
        }

        List<Standing> standings = StandingDAO.getSeasonStandings();

        if (standings.isEmpty()) {
            System.out.println("No rankings found.");
            return;
        }

        System.out.printf("%-6s %-20s %-10s %-10s\n", "Rank", "Team", "Points", "Wins");
        int rank = 1;
        for (Standing s : standings) {
            System.out.printf("%-6d %-20s %-10d %-10d\n", rank++, s.teamName, s.totalPoints, s.wins);
        }
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

        Date offerDate = new Date(System.currentTimeMillis());

        for (int i = 0; i < standings.size(); i++) {
            Standing teamStanding = standings.get(i);
            model.Sponsor chosenSponsor;
            long amount;
            int duration;
            if (i < 3) {
                // Top teams get highest value sponsors
                chosenSponsor = sponsors.stream()
                        .max(Comparator.comparingLong(a -> a.contractValue))
                        .orElse(sponsors.get(0));
                amount = chosenSponsor.contractValue - (i * 500_000L);
                duration = chosenSponsor.contractDurationMonths;
            } else {
                // Smaller teams get random smaller sponsors
                chosenSponsor = sponsors.get(i % sponsors.size());
                amount = Math.max(500_000L, chosenSponsor.contractValue / 4);
                duration = chosenSponsor.contractDurationMonths;
            }

            SponsorshipOfferDAO.insertOffer(teamStanding.teamId, chosenSponsor.name, chosenSponsor.industry,  amount,duration, offerDate);
        }
        System.out.println("Sponsorship offers generated.");
    }



    private static User userLogin(Scanner sc) {
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        User u = UserDAO.authenticate(username, password);
        if (u != null) System.out.println("Login successful.");
        else System.out.println("Login failed.");
        return u;
    }

    private static void createAccount(Scanner sc) {
        System.out.println("---- Create New Account ----");

        String fullName;
        while (true) {
            try {
                System.out.print("Enter Full Name (or type 'back' to cancel): ");
                fullName = sc.nextLine().trim();
                if (fullName.equalsIgnoreCase("back")) return;
                if (fullName.isEmpty()) throw new IllegalArgumentException("Name cannot be empty.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

        String userName;
        while (true) {
            try {
                System.out.print("Enter Username (no numbers allowed, or 'back' to cancel): ");
                userName = sc.nextLine().trim();
                if (userName.equalsIgnoreCase("back")) return;
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

        String emailID;
        while (true) {
            try {
                System.out.print("Enter Email ID (or 'back' to cancel): ");
                emailID = sc.nextLine().trim();
                if (emailID.equalsIgnoreCase("back")) return;
                if (!emailID.matches("^[A-Za-z0-9+_.-]+@(.+)$"))
                    throw new IllegalArgumentException("Invalid email format.");
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }

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

        // Team Information
        System.out.println("\n--- Team Information ---");

        String teamName;
        while (true) {
            try {
                System.out.print("Enter Team Name (or 'back' to cancel): ");
                teamName = sc.nextLine().trim();
                if (teamName.equalsIgnoreCase("back")) return;
                if (teamName.isEmpty()) throw new IllegalArgumentException("Team name cannot be empty.");
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
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("⚠ " + e.getMessage());
            }
        }


        User newUser = new User(fullName, userName, emailID, password);

        if (UserDAO.registerUser(newUser, teamName, teamOrigin)) {
            System.out.println("Account created successfully.");
        } else {
            System.out.println("Failed to create account. Username or email may be in use.");
        }
    }

    private static void userMenu(Scanner sc, User user) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("User Menu:");
            System.out.println("01. View Team Statistics");
            System.out.println("02. Buy Car");
            System.out.println("03. Rent Driver");
            System.out.println("04. View Upcoming Races");
            System.out.println("05. Register Team for Upcoming Race");
            System.out.println("06. View Race Results");
            System.out.println("07. View Sponsorship Requests");
            System.out.println("08. View My Sponsors");
            System.out.println("09. Search Team by Username");
            System.out.println("10. View Driver Statistics");
            System.out.println("11. View Car Statistics");
            System.out.println("12. View Season Standings"); //Same For Admin & User
            System.out.println("13. View Team Standings");
            System.out.println("14. Logout");
            System.out.print("Option: ");
            String input = sc.nextLine();
            switch (input) {
                case "1":
                    viewTeamStatistics(user.teamId);
                    break;
                case "2":
                    buyCar(sc, user);
                    break;
                case "3":
                    rentDriver(sc, user);
                    break;
                case "4":
                    viewUpcomingRaces();
                    break;
                case "5":
                    registerTeamForRace(sc, user);
                    break;
                case "6":
                    viewRaceResults();
                    break;
                case "7":
                    handleSponsorshipRequests(sc, user);
                    break;
                case "8":
                    viewSponsors(sc, user);
                    break;
                case "9":
                    System.out.print("Enter username to search: ");
                    String username = sc.nextLine();
                    User searchedUser = UserDAO.getUserByUsername(username);
                    if (searchedUser == null || searchedUser.teamId == 0) {
                        System.out.println("User or team not found.");
                    } else {
                        displayTeamStatistics(searchedUser.teamId);
                    }
                    break;
                case "10":
                    viewDriverStatistics(sc, user.teamId);
                    break;
                case "11":
                    viewCarStatistics(sc, user.teamId);
                    break;
                case "12" :
                    viewLeaderBoard();
                    break;
                case "13" :
                    viewTeamRankings(sc);
                    break;
                case "14":
                    loggedIn = false;
                    System.out.println("Logged out.");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void buyCar(Scanner sc, User user) {
        List<Car> cars = CarDAO.getAllAvailableCars();
        if (cars.isEmpty()) {
            System.out.println("No cars available for purchase.");
            return;
        }

        System.out.println("Available Cars:");
        for (Car car : cars) {
            System.out.printf("%d: %s, Power: %d, Max Speed: %d, Price: %,d\n",
                    car.carId, car.carName, car.enginePower, car.maxSpeed, car.price);
        }

        System.out.print("Enter Car ID to purchase or 0 to cancel: ");
        String s = sc.nextLine();
        int carId;
        try {
            carId = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (carId == 0) return;

        Car car = CarDAO.getCarById(carId);
        if (car == null) {
            System.out.println("Car not found.");
            return;
        }

        Team team = TeamDAO.getTeamById(user.teamId);
        if (team.budget < car.price) {
            System.out.println("Not enough budget to buy this car.");
            return;
        }

        if (CarDAO.assignCarToTeam(carId, team.teamId, car.price)) {
            TeamDAO.updateBudget(team.teamId, team.budget - car.price);
            System.out.println("Car purchased successfully.");
        } else {
            System.out.println("Failed to purchase car.");
        }
    }

    private static void rentDriver(Scanner sc, User user) {
        List<Driver> drivers = DriverDAO.getAvailableDrivers();
        if (drivers.isEmpty()) {
            System.out.println("No drivers available for rent.");
            return;
        }

        System.out.println("Available Drivers:");
        for (Driver driver : drivers) {
            System.out.printf("%d: %s, Skill Level: %d, Rental Price: %,d\n",
                    driver.driverId, driver.driverName, driver.skillLevel, driver.rentalPrice);
        }

        System.out.print("Enter Driver ID to rent or 0 to cancel: ");
        String s = sc.nextLine();
        int driverId;
        try {
            driverId = Integer.parseInt(s);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (driverId == 0) return;

        Driver driver = DriverDAO.getDriverById(driverId);
        if (driver == null) {
            System.out.println("Driver not found.");
            return;
        }

        Team team = TeamDAO.getTeamById(user.teamId);
        if (team.budget < driver.rentalPrice) {
            System.out.println("Not enough budget to rent this driver.");
            return;
        }

        if (DriverDAO.assignDriverToTeam(driverId, team.teamId, driver.rentalPrice)) {
            TeamDAO.updateBudget(team.teamId, team.budget - driver.rentalPrice);
            System.out.println("Driver rented successfully.");
        } else {
            System.out.println("Failed to rent driver.");
        }
    }

    private static boolean viewUpcomingRaces() {
        System.out.println("Feature to view upcoming races to be implemented.");
        var races = RaceDAO.getAllRaces();
        if (races.isEmpty()) {
            System.out.println("No upcoming races.");
            return false;
        }
        System.out.println("Upcoming Races:");
        for (var race : races) System.out.println(race);
        return true;
    }

    private static void registerTeamForRace(Scanner sc, User user) {
        System.out.println("Feature to register team for race to be implemented.");
         if(viewUpcomingRaces()) {
             int raceId = 0;
             while (true) {
                 System.out.print("Enter Race ID to register (0 to cancel): ");
                 String input = sc.nextLine();
                 try {
                     raceId = Integer.parseInt(input);
                     if (raceId == 0) return;
                     var races = RaceDAO.getAllRaces();
                     boolean valid = false;
                     for (var r : races) if (r.raceId == raceId) valid = true;
                     if (valid) break;
                     else System.out.println("Invalid Race ID.");
                 } catch (NumberFormatException e) {
                     System.out.println("Enter a valid number.");
                 }
             }

             if (RaceParticipationDAO.registerTeamForRace(raceId, user.teamId)) {
                 System.out.println("Registered for race.");
             } else {
                 System.out.println("Registration failed or already registered.");
             }
         }
    }

    private static void viewSponsors(Scanner sc, User user) {
        List<SponsorshipOffer> sponsors = SponsorshipOfferDAO.getAcceptedSponsorsByTeam(user.teamId);
        if (sponsors.isEmpty()) {
            System.out.println("You currently have no sponsors.");
        } else {
            for (SponsorshipOffer s : sponsors) {
                System.out.println(s);
            }
        }
    }

    private static void viewLeaderBoard() {
        System.out.println("Season Points Standings:");
        List<model.Standing> standings = StandingDAO.getSeasonStandings();
        if (standings.isEmpty()) {
            System.out.println("No standings available.");
            return;
        }
        int rank = 1;
        for (model.Standing s : standings) {
            System.out.printf("%d. %s\n", rank++, s);
        }
    }

    private static void viewRaceResults() {
        System.out.println("Feature to view race results to be implemented.");
        List<dao.RaceResultDAO.RaceResult> results = RaceResultDAO.getAllRaceResults();
        if (results.isEmpty()) {
            System.out.println("No race results available.");
            return;
        }
        System.out.println("Race Results:");
        int currentRaceId = -1;
        for (dao.RaceResultDAO.RaceResult res : results) {
            if (res.raceId != currentRaceId) {
                currentRaceId = res.raceId;
                System.out.printf("\nRace %d | Track: %s | Date: %s %s | Laps: %d\n", currentRaceId, res.trackName, res.raceDate, res.raceTime, res.laps);
                System.out.println("Position | Team Name | Points");
            }
            System.out.printf("%8d | %9s | %6d\n", res.position, res.teamName, res.points);
        }
    }

    private static void viewDriverStatistics(Scanner sc, int teamId) {
        System.out.println("Feature to view driver statistics to be implemented.");

        List<Driver> drivers = DriverDAO.getDriversByTeam(teamId);
        if (drivers.isEmpty()) {
            System.out.println("No Driver found in your team.");
            return;
        }
        listDriverIdsAndNames(drivers);

        System.out.print("Enter Driver ID to view statistics: ");

        String input = sc.nextLine();
        int driverId;
        try {
            driverId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        DriverStatsDAO.DriverStats stats = DriverStatsDAO.getDriverStats(driverId, teamId);
        if (stats == null) {
            System.out.println("Driver not found or no stats available.");
            return;
        }
        System.out.println("Driver Stats:");
        System.out.println("Name: " + stats.driverName);
        System.out.println("Total Races: " + stats.totalRaces);
        System.out.println("Wins: " + stats.wins);
        System.out.println("Total Points: " + stats.totalPoints);
    }

    public static void listDriverIdsAndNames(List<Driver> drivers) {
        System.out.printf("%-5s %-30s%n", "ID", "Driver Name");
        System.out.println("----------------------------------");
        for (Driver driver : drivers) {
            System.out.printf("%-5d %-30s%n", driver.driverId, driver.driverName);
        }
    }

    private static void viewCarStatistics(Scanner sc, int teamId) {
        System.out.println("Feature to view car statistics to be implemented.");

        List<Car> cars = CarDAO.getCarsByTeam(teamId);
        if (cars.isEmpty()) {
            System.out.println("No cars found in your team.");
            return;
        }
        listCarIdsAndNames(cars);

        System.out.print("Enter Car ID to view statistics: ");
        String input = sc.nextLine();
        int carId;
        try {
            carId = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        CarStatsDAO.CarStats stats = CarStatsDAO.getCarStats(carId, teamId);
        if (stats == null) {
            System.out.println("Car not found or no stats available.");
            return;
        }
        System.out.println("Car Stats:");
        System.out.println("Name: " + stats.carName);
        System.out.println("Total Races: " + stats.totalRaces);
        System.out.println("Wins: " + stats.wins);
        System.out.println("Total Points: " + stats.totalPoints);
    }

    public static void listCarIdsAndNames(List<Car> cars) {
        System.out.printf("%-5s %-30s%n", "ID", "Car Name");
        System.out.println("----------------------------------");
        for (Car car : cars) {
            System.out.printf("%-5d %-30s%n", car.carId, car.carName);
        }
    }

    private static void handleSponsorshipRequests(Scanner sc, User user) {
        List<SponsorshipOffer> offers = SponsorshipOfferDAO.getPendingOffersByTeam(user.teamId);
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

    private static void viewTeamStatistics(int teamId) {
        System.out.println("Your Team Statistics:\n");
        displayTeamStatistics(teamId);
    }

    private static void displayTeamStatistics(int teamId) {
        Team team = TeamDAO.getTeamById(teamId);
        if (team == null) {
            System.out.println("Team not found.");
            return;
        }

        System.out.println("Team Name: " + team.teamName);
        System.out.println("Budget: $" + team.budget);
        System.out.println("Origin: " + team.origin);
        System.out.println("Founded: " + team.foundingYear);
        System.out.println("Email: " + team.email);

        List<Car> cars = CarDAO.getCarsByTeam(teamId);
        System.out.println("\nOwned Cars (" + cars.size() + "):");
        for (Car c : cars) {
            System.out.printf(" - %s (Power: %d, Max Speed: %d km/h, Price: $%,d)\n",
                    c.carName, c.enginePower, c.maxSpeed, c.price);
        }

        List<Driver> drivers = DriverDAO.getDriversByTeam(teamId);
        System.out.println("\nRented Drivers (" + drivers.size() + "):");
        for (Driver d : drivers) {
            System.out.printf(" - %s (Skill Level: %d, Rental Price: $%,d)\n",
                    d.driverName, d.skillLevel, d.rentalPrice);
        }

        List<Standing> standings = StandingDAO.getSeasonStandings();
        for (Standing s : standings) {
            if (s.teamId == teamId) {
                System.out.println("\nCurrent Season Points: " + s.totalPoints);
                System.out.println("Wins: " + s.wins);
                break;
            }
        }
        System.out.println();
    }
}
