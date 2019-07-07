package com.github.davidmoten.etim;

public class Options {

    private final long mergeTimeThresholdMs;
    private final long maxTimeDiffWithoutSpeedCheckMs;
    private final double maxDistanceDiffWithoutSpeedCheckKm;

    public Options(long mergeTimeThresholdMs, long maxTimeDiffWithoutSpeedCheck,
            double maxDistanceDiffWithoutSpeedCheckKm) {
        this.mergeTimeThresholdMs = mergeTimeThresholdMs;
        this.maxTimeDiffWithoutSpeedCheckMs = maxTimeDiffWithoutSpeedCheck;
        this.maxDistanceDiffWithoutSpeedCheckKm = maxDistanceDiffWithoutSpeedCheckKm;
    }

    public long mergeTimeThresholdMs() {
        return mergeTimeThresholdMs;
    }

    public long maxTimeDiffWithoutSpeedCheckMs() {
        return maxTimeDiffWithoutSpeedCheckMs;
    }

    public double maxDistanceDiffKmWithoutSpeedCheckKm() {
        return maxDistanceDiffWithoutSpeedCheckKm;
    }

}
