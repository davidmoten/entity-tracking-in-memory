package com.github.davidmoten.etim;

public final class IdentifierType {

    private final EntityType entityType;
    private final String name;
    private final int priority;

    IdentifierType(EntityType entityType, String name, int priority) {
        this.entityType = entityType;
        this.name = name;
        this.priority = priority;
    }

    public EntityType entityType() {
        return entityType;
    }

    public String name() {
        return name;
    }

    public int priority() {
        return priority;
    }

}
