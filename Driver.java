import java.time.LocalDate;

public class Driver {
    private int id;
    private String name;
    private int age; // Derived from birthdate or directly stored for simplicity
    private String nationality;
    private int skillLevel; // e.g., 1-100, or rookie/pro/expert
    private int teamId; // Foreign key to Team
    private LocalDate dateOfBirth; // More precise than just age
    private String licenseNumber; // Unique identifier for professional drivers
    private double experienceYears; // Years of racing experience

    // Transient field for full object representation (loaded by manager methods)
    private Team team;

    public Driver(int id, String name, LocalDate dateOfBirth, String nationality, int skillLevel,
                  int teamId, String licenseNumber, double experienceYears) {
        this.id = id;
        this.name = name;
        this.dateOfBirth = dateOfBirth;
        this.age = calculateAge(dateOfBirth);
        this.nationality = nationality;
        this.skillLevel = skillLevel;
        this.teamId = teamId;
        this.licenseNumber = licenseNumber;
        this.experienceYears = experienceYears;
    }

    // Constructor for new drivers (ID auto-generated)
    public Driver(String name, LocalDate dateOfBirth, String nationality, int skillLevel,
                  int teamId, String licenseNumber, double experienceYears) {
        this(-1, name, dateOfBirth, nationality, skillLevel, teamId, licenseNumber, experienceYears);
    }

    // Helper to calculate age from birth date
    private int calculateAge(LocalDate dob) {
        if (dob == null) return 0;
        return LocalDate.now().getYear() - dob.getYear();
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; } // Auto-calculated
    public String getNationality() { return nationality; }
    public int getSkillLevel() { return skillLevel; }
    public int getTeamId() { return teamId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public String getLicenseNumber() { return licenseNumber; }
    public double getExperienceYears() { return experienceYears; }

    // Getter for transient object
    public Team getTeam() { return team; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
        this.age = calculateAge(dateOfBirth); // Update age when DOB is set
    }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setSkillLevel(int skillLevel) { this.skillLevel = skillLevel; }
    public void setTeamId(int teamId) { this.teamId = teamId; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }
    public void setExperienceYears(double experienceYears) { this.experienceYears = experienceYears; }

    // Setter for transient object
    public void setTeam(Team team) { this.team = team; }

    // Business logic example: Evaluate if driver is a veteran
    public boolean isVeteran() {
        return experienceYears >= 5 && skillLevel >= 80;
    }

    // Business logic example: Improve skill (simple simulation)
    public void improveSkill(int points) {
        this.skillLevel = Math.min(100, this.skillLevel + points);
        System.out.println(this.name + "'s skill level increased to " + this.skillLevel);
    }

    @Override
    public String toString() {
        return "Driver{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", nationality='" + nationality + '\'' +
                ", skillLevel=" + skillLevel +
                ", teamId=" + teamId +
                ", licenseNumber='" + licenseNumber + '\'' +
                '}';
    }
}
