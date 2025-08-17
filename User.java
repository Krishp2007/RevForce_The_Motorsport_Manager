package model;

public class User {
    public int userId;
    public String firstName;
    public String lastName;
    public String username;
    public String email;
    public String password;
    public String role;
    public int teamId;

    public User() {}

    public User(String fullName, String username, String email, String password) {
        String[] names = fullName.trim().split(" ", 2);
        this.firstName = names.length > 0 ? names[0] : "";
        this.lastName  = names.length > 1 ? names[1] : "";
        this.username  = username;
        this.email     = email;
        this.password  = password;
    }

    public User(String fullName, String username, String email, String password, String role, int teamId) {
        String[] names = fullName.trim().split(" ", 2);
        this.firstName = names.length > 0 ? names[0] : "";
        this.lastName  = names.length > 1 ? names[1] : "";
        this.username  = username;
        this.email     = email;
        this.password  = password;
        this.role      = role;
        this.teamId    = teamId;
    }
}
