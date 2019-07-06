package com.github.davidmoten.etim;

public enum EntityType {

    VESSEL(100), VEHICLE(200), AIRCRAFT(1000), BEACON(50), TRACKING_DEVICE(1000);

    private final double maxSpeedKmPerHour;

    private EntityType(double maxSpeedKmPerHour) {
        this.maxSpeedKmPerHour = maxSpeedKmPerHour;
    }

    public double maxSpeedKmPerHour() {
        return maxSpeedKmPerHour;
    }
}
