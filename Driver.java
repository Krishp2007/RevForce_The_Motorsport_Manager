package com.revForce.motorsportmanager.team;

public class Driver {
    private int id;
    private String name;
    private int age;
    private String nationality;
    private int skillLevel;

    // Constructor, getters, setters...

    public Driver(int id, String name, int age, String nationality, int skillLevel) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.nationality = nationality;
        this.skillLevel = skillLevel;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }
    public String getNationality() { return nationality; }
    public int getSkillLevel() { return skillLevel; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setAge(int age) { this.age = age; }
    public void setNationality(String nationality) { this.nationality = nationality; }
    public void setSkillLevel(int skillLevel) { this.skillLevel = skillLevel; }
}
