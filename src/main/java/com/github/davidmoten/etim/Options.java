package com.github.davidmoten.etim;

import java.util.Map;

public final class Options {

    private final long maxTimeDiffWithoutSpeedCheckMs;
    private final double maxDistanceDiffWithoutSpeedCheckKm;
    private final Map<String, EntityType> entityTypes;
    private final Map<String, IdentifierType> identifierTypes;
    private final long maxAgeMs;
    private final Clock clock;
    private final int rTreeMaxChildren;
    private final int maxEntities;

    public Options(long maxTimeDiffWithoutSpeedCheck, double maxDistanceDiffWithoutSpeedCheckKm, long maxAgeMs,
            int rTreeMaxChildren, int maxEntities, Map<String, EntityType> entityTypes,
            Map<String, IdentifierType> identifierTypes, Clock clock) {
        this.maxTimeDiffWithoutSpeedCheckMs = maxTimeDiffWithoutSpeedCheck;
        this.maxDistanceDiffWithoutSpeedCheckKm = maxDistanceDiffWithoutSpeedCheckKm;
        this.rTreeMaxChildren = rTreeMaxChildren;
        this.maxEntities = maxEntities;
        this.entityTypes = entityTypes;
        this.identifierTypes = identifierTypes;
        this.maxAgeMs = maxAgeMs;
        this.clock = clock;
    }

    public long maxAgeMs() {
        return maxAgeMs;
    }

    public long maxTimeDiffWithoutSpeedCheckMs() {
        return maxTimeDiffWithoutSpeedCheckMs;
    }

    public double maxDistanceDiffKmWithoutSpeedCheckKm() {
        return maxDistanceDiffWithoutSpeedCheckKm;
    }

    public EntityType getEntityType(String name) {
        return entityTypes.get(name);
    }

    public IdentifierType getIdentifierType(String name) {
        return identifierTypes.get(name);
    }

    public Clock clock() {
        return clock;
    }

    public int rTreeMaxChildren() {
        return rTreeMaxChildren;
    }

    public int maxEntities() {
        return maxEntities;
    }

}
