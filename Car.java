package model;

public class Car {
    public int carId;
    public String carName;
    public long price;
    public int enginePower;
    public int maxSpeed;
    public int teamId;  // -1 if unowned

    public Car() {}

    public Car(int carId, String carName, long price, int enginePower, int maxSpeed, int teamId) {
        this.carId = carId;
        this.carName = carName;
        this.price = price;
        this.enginePower = enginePower;
        this.maxSpeed = maxSpeed;
        this.teamId = teamId;
    }

    @Override
    public String toString() {
        return String.format("%d - %s | Price: %,d | Engine Power: %d | Max Speed: %d km/h | Owned by Team: %s",
                carId, carName, price, enginePower, maxSpeed,
                (teamId == -1 ? "None" : Integer.toString(teamId)));
    }
}
