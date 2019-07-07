package com.github.davidmoten.etim;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.viem.EntityState;
import com.github.davidmoten.viem.System;

public final class Entities implements System<String, String, Metadata> {

    private final Set<EntityState<String, String, Metadata>> entities = new HashSet<>();
    private final Map<KeyValue, EntityState<String, String, Metadata>> map = new HashMap<>();
    private final Map<String, IdentifierType> identifierTypes;
    private final Map<String, EntityType> entityTypes;
    private final Options options;

    public Entities(Map<String, IdentifierType> identifierTypes,
            Map<String, EntityType> entityTypes, Options options) {
        this.identifierTypes = identifierTypes;
        this.entityTypes = entityTypes;
        this.options = options;
    }

    @Override
    public Iterable<EntityState<String, String, Metadata>> entityStates() {
        return entities;
    }

    @Override
    public Set<EntityState<String, String, Metadata>> matches(Map<String, String> identifiers) {
        return identifiers //
                .entrySet() //
                .stream() //
                .flatMap(entry -> {
                    KeyValue kv = new KeyValue(entry.getKey(), entry.getValue());
                    EntityState<String, String, Metadata> e = map.get(kv);
                    if (e == null) {
                        return Stream.empty();
                    } else {
                        return Stream.of(e);
                    }
                }).collect(Collectors.toSet());
    }

    @Override
    public boolean keyGreaterThan(String a, String b) {
        return identifierTypes.get(a).priority > identifierTypes.get(b).priority;
    }

    @Override
    public boolean metadataGreaterThan(Metadata a, Metadata b) {
        return a.time() > b.time();
    }

    @Override
    public boolean mergeable(Metadata a, Metadata b) {
        if (a.type() != b.type()) {
            throw new IllegalArgumentException("cannot merge different entity types");
        }
        long timeDiffMs = Math.abs(a.time() - b.time());
        boolean expired = timeDiffMs > options.mergeTimeThresholdMs();
        if (expired) {
            return true;
        }
        Position apos = Position.create(a.lat(), a.lon());
        Position bpos = Position.create(b.lat(), b.lon());
        double distanceKm = apos.getDistanceToKm(bpos);
        if (timeDiffMs < options.maxTimeDiffWithoutSpeedCheckMs()
                && distanceKm < options.maxDistanceDiffKmWithoutSpeedCheckKm()) {
            return true;
        } else {
            double speedKmPerHour = distanceKm / timeDiffMs * 1000 * 60 * 60;
            return speedKmPerHour <= entityTypes.get(a.type()).maxSpeedKmPerHour;
        }
    }

    @Override
    public Metadata merge(Metadata a, Metadata b) {
        Metadata max = a.time() > b.time() ? a : b;
        Metadata min = a.time() > b.time() ? b : a;
        Map<String, String> props = new HashMap<>(min.properties());
        for (Entry<String, String> entry : max.properties().entrySet()) {
            props.put(entry.getKey(), entry.getValue());
        }
        return new Metadata(max.type(), max.lat(), max.lon(), max.time(), props);
    }

    @Override
    public System<String, String, Metadata> update(
            List<EntityState<String, String, Metadata>> matches,
            Set<EntityState<String, String, Metadata>> newEntityStates) {
        entities.removeAll(matches);
        for (EntityState<String, String, Metadata> e : entities) {
            for (Entry<String, String> entry : e.identifiers().entrySet()) {
                map.remove(new KeyValue(entry.getKey(), entry.getValue()));
            }
        }
        entities.addAll(newEntityStates);
        for (EntityState<String, String, Metadata> e : newEntityStates) {
            for (Entry<String, String> entry : e.identifiers().entrySet()) {
                map.put(new KeyValue(entry.getKey(), entry.getValue()), e);
            }
        }
        return this;
    }

}
