package com.github.davidmoten.etim;

class IdentifierType {

    final EntityType entityType;
    final String name;
    final int priority;

    IdentifierType(EntityType entityType, String name, int priority) {
        this.entityType = entityType;
        this.name = name;
        this.priority = priority;
    }

}
