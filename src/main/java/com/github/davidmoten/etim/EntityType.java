package com.github.davidmoten.etim;

public final class EntityType {

    private final String name;
    private final double maxSpeedKmPerHour;
    private final long autoMergeThresholdMs;

    EntityType(String name, double maxSpeedKmPerHour, long autoMergeThresholdMs) {
        this.name = name;
        this.maxSpeedKmPerHour = maxSpeedKmPerHour;
        this.autoMergeThresholdMs = autoMergeThresholdMs;
    }
    public String name() {
        return name;
    }

    public double maxSpeedKmPerHour() {
        return maxSpeedKmPerHour;
    }

    public long autoMergeThresholdMs() {
        return autoMergeThresholdMs;
    }
    
}
