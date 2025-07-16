package com.revForce.tuning;

import java.util.*;

public class CompleteCarTuningSystem {

    // ===================== CAR DATABASE =====================
    static class CarDatabase {
        static Map<String, Car> cars = new HashMap<>();

        static {
            cars.put("Nissan GT-R", new Car("Nissan GT-R", 1740, 565, 315, 467));
            cars.put("Toyota Supra", new Car("Toyota Supra", 1560, 382, 250, 500));
            cars.put("Honda Civic Type R", new Car("Honda Civic Type R", 1380, 320, 170, 400));
        }
    }

    static class Car {
        String name;
        double weight;
        int horsepower;
        int topSpeed;
        int torque;

        public Car(String name, double weight, int horsepower, int topSpeed, int torque) {
            this.name = name;
            this.weight = weight;
            this.horsepower = horsepower;
            this.topSpeed = topSpeed;
            this.torque = torque;
        }

        void displayCarInfo() {
            System.out.println("\n====== Car Details ======");
            System.out.println("Name: " + name);
            System.out.println("Weight: " + weight + " kg");
            System.out.println("Horsepower: " + horsepower + " HP");
            System.out.println("Top Speed: " + topSpeed + " km/h");
            System.out.println("Torque: " + torque + " Nm");
        }
    }

    // ===================== PERFORMANCE MODIFIER =====================
    static class PerformanceModifier {
        Car car;
        Scanner sc = new Scanner(System.in);

        double totalCost = 0;
        int horsepowerIncrease = 0;
        int topSpeedIncrease = 0;
        int torqueIncrease = 0;
        double weightReduction = 0;
        int frontDownforce = 0;
        int rearDownforce = 0;
        int wheelProfile = 0;
        int wheelWidth = 0;
        int rimSize = 0;
        int centerOfMassShift = 0;
        double shiftingSpeed = 0;
        int rpm = 0;
        String fuelType = "Regular Petrol";

        public PerformanceModifier(Car car) {
            this.car = car;
        }

        // ========== ENGINE MODIFICATIONS ==========
        void engineUpgrade() {
            boolean running = true;
            while (running) {
                System.out.println("\n==== Engine Modifications ====");
                System.out.println("1. Install Turbocharger");
                System.out.println("2. Upgrade Camshafts");
                System.out.println("3. Upgrade Intake Manifold");
                System.out.println("4. Change Fuel Type");
                System.out.println("5. Return to Main Menu");
                System.out.print("Select option: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: installTurbo(); break;
                    case 2: installCamshafts(); break;
                    case 3: installIntakeManifold(); break;
                    case 4: selectFuelType(); break;
                    case 5: running = false; break;
                    default: System.out.println("Invalid choice!");
                }
            }
        }

        void installTurbo() {
            System.out.println("\nAvailable Turbo Options:");
            System.out.println("1. Street Turbo (+30HP, +25Nm) - ₹25,000");
            System.out.println("2. Sport Turbo (+50HP, +40Nm) - ₹45,000");
            System.out.println("3. Elite Turbo (+80HP, +65Nm) - ₹65,000");
            System.out.println("4. Ultimate Turbo (+120HP, +90Nm) - ₹90,000");
            System.out.print("Select turbo: ");

            int turboChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;
            int torqueBoost = 0;

            switch(turboChoice) {
                case 1:
                    price = 25000;
                    hpBoost = 30;
                    torqueBoost = 25;
                    break;
                case 2:
                    price = 45000;
                    hpBoost = 50;
                    torqueBoost = 40;
                    break;
                case 3:
                    price = 65000;
                    hpBoost = 80;
                    torqueBoost = 65;
                    break;
                case 4:
                    price = 90000;
                    hpBoost = 120;
                    torqueBoost = 90;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                torqueIncrease += torqueBoost;
                System.out.println("Turbo installed successfully!");
            }
        }

        void installCamshafts() {
            System.out.println("\nAvailable Camshaft Options:");
            System.out.println("1. Street Camshafts (+20HP, +10Nm) - ₹12,000");
            System.out.println("2. Sport Camshafts (+25HP, +20Nm) - ₹20,000");
            System.out.println("3. Race Camshafts (+32HP, +25Nm) - ₹35,000");
            System.out.println("4. Ultimate Camshafts (+38HP, +30Nm) - ₹50,000");
            System.out.print("Select camshafts: ");

            int camChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;
            int torqueBoost = 0;

            switch(camChoice) {
                case 1:
                    price = 12000;
                    hpBoost = 20;
                    torqueBoost = 10;
                    break;
                case 2:
                    price = 20000;
                    hpBoost = 25;
                    torqueBoost = 20;
                    break;
                case 3:
                    price = 35000;
                    hpBoost = 32;
                    torqueBoost = 25;
                    break;
                case 4:
                    price = 50000;
                    hpBoost = 38;
                    torqueBoost = 30;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                torqueIncrease += torqueBoost;
                System.out.println("Camshafts installed successfully!");
            }
        }

        void installIntakeManifold() {
            System.out.println("\nAvailable Intake Manifold Options:");
            System.out.println("1. Street Intake (+5HP) - ₹8,000");
            System.out.println("2. Sport Intake (+8HP) - ₹12,000");
            System.out.println("3. Race Intake (+12HP) - ₹18,000");
            System.out.println("4. Ultimate Intake (+15HP) - ₹25,000");
            System.out.print("Select intake: ");

            int intakeChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;

            switch(intakeChoice) {
                case 1:
                    price = 8000;
                    hpBoost = 5;
                    break;
                case 2:
                    price = 12000;
                    hpBoost = 8;
                    break;
                case 3:
                    price = 18000;
                    hpBoost = 12;
                    break;
                case 4:
                    price = 25000;
                    hpBoost = 15;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                System.out.println("Intake manifold installed successfully!");
            }
        }

        void selectFuelType() {
            System.out.println("\nAvailable Fuel Types:");
            System.out.println("1. Regular Petrol (Standard)");
            System.out.println("2. High Octane (+12HP, +10Nm) - ₹2,000");
            System.out.println("3. Ethanol Blend (+20HP, +15Nm) - ₹2,500");
            System.out.print("Select fuel type: ");

            int fuelChoice = sc.nextInt();
            sc.nextLine();

            switch(fuelChoice) {
                case 1:
                    fuelType = "Regular Petrol";
                    System.out.println("Using regular petrol");
                    break;
                case 2:
                    if (confirmUpgrade(2000)) {
                        fuelType = "High Octane";
                        horsepowerIncrease += 12;
                        torqueIncrease += 10;
                        totalCost += 2000;
                        System.out.println("High octane fuel selected!");
                    }
                    break;
                case 3:
                    if (confirmUpgrade(2500)) {
                        fuelType = "Ethanol Blend";
                        horsepowerIncrease += 20;
                        torqueIncrease += 15;
                        totalCost += 2500;
                        System.out.println("Ethanol blend selected!");
                    }
                    break;
                default:
                    System.out.println("Invalid selection!");
            }
        }

        // ========== TRANSMISSION MODIFICATIONS ==========
        void transmissionUpgrade() {
            boolean running = true;
            while (running) {
                System.out.println("\n==== Transmission Modifications ====");
                System.out.println("1. Upgrade Gearbox");
                System.out.println("2. Upgrade Clutch");
                System.out.println("3. Upgrade Differential");
                System.out.println("4. Return to Main Menu");
                System.out.print("Select option: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: upgradeGearbox(); break;
                    case 2: upgradeClutch(); break;
                    case 3: upgradeDifferential(); break;
                    case 4: running = false; break;
                    default: System.out.println("Invalid choice!");
                }
            }
        }

        void upgradeGearbox() {
            System.out.println("\nAvailable Gearbox Options:");
            System.out.println("1. Street Gearbox (+5HP, +2Nm) - ₹15,000");
            System.out.println("2. Sport Gearbox (+8HP, +4Nm) - ₹25,000");
            System.out.println("3. Race Gearbox (+12HP, +7Nm) - ₹40,000");
            System.out.print("Select gearbox: ");

            int gearChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;
            int torqueBoost = 0;

            switch(gearChoice) {
                case 1:
                    price = 15000;
                    hpBoost = 5;
                    torqueBoost = 2;
                    break;
                case 2:
                    price = 25000;
                    hpBoost = 8;
                    torqueBoost = 4;
                    break;
                case 3:
                    price = 40000;
                    hpBoost = 12;
                    torqueBoost = 7;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                torqueIncrease += torqueBoost;
                System.out.println("Gearbox upgraded successfully!");
            }
        }

        void upgradeClutch() {
            System.out.println("\nAvailable Clutch Options:");
            System.out.println("1. Street Clutch (+7HP, +3Nm) - ₹10,000");
            System.out.println("2. Sport Clutch (+10HP, +5Nm) - ₹18,000");
            System.out.println("3. Race Clutch (+12HP, +7Nm) - ₹25,000");
            System.out.print("Select clutch: ");

            int clutchChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;
            int torqueBoost = 0;

            switch(clutchChoice) {
                case 1:
                    price = 10000;
                    hpBoost = 7;
                    torqueBoost = 3;
                    break;
                case 2:
                    price = 18000;
                    hpBoost = 10;
                    torqueBoost = 5;
                    break;
                case 3:
                    price = 25000;
                    hpBoost = 12;
                    torqueBoost = 7;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                torqueIncrease += torqueBoost;
                System.out.println("Clutch upgraded successfully!");
            }
        }

        void upgradeDifferential() {
            System.out.println("\nAvailable Differential Options:");
            System.out.println("1. Street Differential (+5HP, +2Nm) - ₹8,000");
            System.out.println("2. Sport Differential (+8HP, +4Nm) - ₹15,000");
            System.out.println("3. Race Differential (+12HP, +7Nm) - ₹22,000");
            System.out.print("Select differential: ");

            int diffChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            int hpBoost = 0;
            int torqueBoost = 0;

            switch(diffChoice) {
                case 1:
                    price = 8000;
                    hpBoost = 5;
                    torqueBoost = 2;
                    break;
                case 2:
                    price = 15000;
                    hpBoost = 8;
                    torqueBoost = 4;
                    break;
                case 3:
                    price = 22000;
                    hpBoost = 12;
                    torqueBoost = 7;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                horsepowerIncrease += hpBoost;
                torqueIncrease += torqueBoost;
                System.out.println("Differential upgraded successfully!");
            }
        }

        // ========== BODY MODIFICATIONS ==========
        void bodyModification() {
            boolean running = true;
            while (running) {
                System.out.println("\n==== Body Modifications ====");
                System.out.println("1. Weight Reduction");
                System.out.println("2. Aerodynamic Adjustments");
                System.out.println("3. Center of Mass Adjustment");
                System.out.println("4. Return to Main Menu");
                System.out.print("Select option: ");

                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1: reduceWeight(); break;
                    case 2: adjustAerodynamics(); break;
                    case 3: adjustCenterOfMass(); break;
                    case 4: running = false; break;
                    default: System.out.println("Invalid choice!");
                }
            }
        }

        void reduceWeight() {
            System.out.println("\nAvailable Weight Reduction Options:");
            System.out.println("1. Aluminum Panels (-25kg) - ₹20,000");
            System.out.println("2. Titanium Parts (-40kg) - ₹35,000");
            System.out.println("3. Carbon Fiber (-75kg) - ₹60,000");
            System.out.print("Select option: ");

            int weightChoice = sc.nextInt();
            sc.nextLine();

            double price = 0;
            double reduction = 0;

            switch(weightChoice) {
                case 1:
                    price = 20000;
                    reduction = 25;
                    break;
                case 2:
                    price = 35000;
                    reduction = 40;
                    break;
                case 3:
                    price = 60000;
                    reduction = 75;
                    break;
                default:
                    System.out.println("Invalid selection!");
                    return;
            }

            if (confirmUpgrade(price)) {
                totalCost += price;
                weightReduction += reduction;
                System.out.println("Weight reduced successfully!");
            }
        }

        void adjustAerodynamics() {
            System.out.println("\nEnter front downforce (50-300 kg):");
            frontDownforce = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter rear downforce (50-300 kg):");
            rearDownforce = sc.nextInt();
            sc.nextLine();

            double price = (frontDownforce + rearDownforce) * 35;

            if (confirmUpgrade(price)) {
                totalCost += price;
                System.out.println("Aerodynamics adjusted!");
            }
        }

        void adjustCenterOfMass() {
            System.out.println("\nEnter center of mass adjustment (-30 to +30%):");
            centerOfMassShift = sc.nextInt();
            sc.nextLine();

            double price = Math.abs(centerOfMassShift) * 500;

            if (confirmUpgrade(price)) {
                totalCost += price;
                System.out.println("Center of mass adjusted!");
            }
        }

        // ========== WHEEL MODIFICATIONS ==========
        void wheelModification() {
            System.out.println("\nEnter wheel profile (25-55 mm):");
            wheelProfile = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter wheel width (175-325 mm):");
            wheelWidth = sc.nextInt();
            sc.nextLine();

            System.out.println("Enter rim size (15-22 inches):");
            rimSize = sc.nextInt();
            sc.nextLine();

            double price = wheelProfile * 100 + wheelWidth * 50 + rimSize * 150;

            if (confirmUpgrade(price)) {
                totalCost += price;
                System.out.println("Wheels modified successfully!");
            }
        }

        // ========== HELPER METHODS ==========
        boolean confirmUpgrade(double price) {
            System.out.println("This upgrade costs ₹" + price + ". Confirm? (y/n)");
            return sc.nextLine().equalsIgnoreCase("y");
        }

        void displayFinalSpecs() {
            System.out.println("\n====== FINAL MODIFICATIONS ======");
            System.out.println("Car: " + car.name);

            // Display performance upgrades
            System.out.println("\nPerformance Upgrades:");
            System.out.printf("Horsepower: %d (+%d)%n",
                    car.horsepower + horsepowerIncrease, horsepowerIncrease);
            System.out.printf("Torque: %d (+%d)%n",
                    car.torque + torqueIncrease, torqueIncrease);
            System.out.printf("Top Speed: %d (+%d)%n",
                    car.topSpeed + topSpeedIncrease, topSpeedIncrease);
            System.out.printf("Weight: %.1f (-%.1f)%n",
                    car.weight - weightReduction, weightReduction);

            // Display handling upgrades
            System.out.println("\nHandling Upgrades:");
            System.out.println("Front Downforce: " + frontDownforce + " kg");
            System.out.println("Rear Downforce: " + rearDownforce + " kg");
            System.out.println("Center of Mass Shift: " + centerOfMassShift + "%");
            System.out.println("Wheel Size: " + rimSize + "\"");
            System.out.println("Wheel Width: " + wheelWidth + "mm");
            System.out.println("Wheel Profile: " + wheelProfile + "mm");

            System.out.println("\nTotal Cost: ₹" + totalCost);
        }
    }

    // ===================== MAIN APPLICATION =====================
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.println("=== CAR TUNING SYSTEM ===");
        System.out.println("\nAvailable Cars:");
        for (String carName : CarDatabase.cars.keySet()) {
            System.out.println("- " + carName);
        }

        System.out.print("\nSelect car to modify: ");
        String selectedCar = sc.nextLine();

        Car car = CarDatabase.cars.get(selectedCar);
        if (car == null) {
            System.out.println("Invalid car selection!");
            return;
        }

        car.displayCarInfo();
        PerformanceModifier modifier = new PerformanceModifier(car);

        boolean running = true;
        while (running) {
            System.out.println("\n=== MAIN MENU ===");
            System.out.println("1. Engine Modifications");
            System.out.println("2. Transmission Upgrades");
            System.out.println("3. Body Modifications");
            System.out.println("4. Wheel Modifications");
            System.out.println("5. View Final Specifications");
            System.out.println("6. Exit");
            System.out.print("Select option: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1: modifier.engineUpgrade(); break;
                case 2: modifier.transmissionUpgrade(); break;
                case 3: modifier.bodyModification(); break;
                case 4: modifier.wheelModification(); break;
                case 5: modifier.displayFinalSpecs(); break;
                case 6: running = false; break;
                default: System.out.println("Invalid option!");
            }
        }

        System.out.println("\nThank you for using our tuning system!");
    }
}
