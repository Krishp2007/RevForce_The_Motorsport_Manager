package com.revForce.motorsportmanager.team;

import com.revForce.motorsportmanager.db.DriverDAO;

import java.util.List;

public class DriverManager {
    private DriverDAO dao;

    public DriverManager() {
        dao = new DriverDAO();
    }

    public void addDriver(Driver driver) {
        dao.addDriver(driver);
    }

    public void updateDriver(Driver driver) {
        dao.updateDriver(driver);
    }

    public void deleteDriver(int id) {
        dao.deleteDriver(id);
    }

    public List<Driver> getAllDrivers() {
        return dao.getAllDrivers();
    }

    public Driver getDriverById(int id) {
        return dao.getDriverById(id);
    }

    public List<Driver> getDriversBySkill(int minSkill) {
        return dao.getDriversBySkillThreshold(minSkill);
    }
}
