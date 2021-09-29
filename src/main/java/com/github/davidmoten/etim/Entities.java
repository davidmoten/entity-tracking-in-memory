package com.github.davidmoten.etim;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.davidmoten.grumpy.core.Position;
import com.github.davidmoten.rtree2.RTree;
import com.github.davidmoten.rtree2.geometry.Point;
import com.github.davidmoten.viem.EntityState;
import com.github.davidmoten.viem.ViemSystem;

public final class Entities implements ViemSystem<String, String, Metadata> {

    private final Set<EntityState<String, String, Metadata>> entities = new HashSet<>();
    private final Map<KeyValue, EntityState<String, String, Metadata>> map = new HashMap<>();
    private final Options options;
    private final TreeMap<Long, List<EntityState<String, String, Metadata>>> orderedByTime = new TreeMap<>();

    private volatile RTree<EntityState<String, String, Metadata>, Point> tree;

    public Entities(Options options) {
        this.options = options;
        this.tree = RTree.maxChildren(options.rTreeMaxChildren()).star().create();
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
        return options.getIdentifierType(a).priority() > options.getIdentifierType(b).priority();
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
        EntityType entityType = options.getEntityType(a.type());
        boolean autoMerge = timeDiffMs >= entityType.autoMergeThresholdMs();
        if (autoMerge) {
            return true;
        }
        Position apos = Position.create(a.lat(), a.lon());
        Position bpos = Position.create(b.lat(), b.lon());
        double distanceKm = apos.getDistanceToKm(bpos);
        if (timeDiffMs < options.maxTimeDiffWithoutSpeedCheckMs()
                && distanceKm < options.maxDistanceDiffKmWithoutSpeedCheckKm()) {
            return true;
        } else {
            double timeDiffHours = (double) timeDiffMs / TimeUnit.HOURS.toMillis(1);
            double speedKmPerHour = distanceKm / timeDiffHours;
            return speedKmPerHour <= entityType.maxSpeedKmPerHour();
        }
    }

    @Override
    public Metadata merge(Metadata a, Metadata b) {
        Metadata max = a.time() > b.time() ? a : b;
        Map<String, TimestampedString> props = new HashMap<>(a.properties());
        for (Entry<String, TimestampedString> entry : b.properties().entrySet()) {
            TimestampedString ts = a.properties().get(entry.getKey());
            if (ts == null || entry.getValue().time() > a.time()) {
                // favour the b value
                props.put(entry.getKey(), entry.getValue());
            }
        }
        return new Metadata(max.type(), max.lat(), max.lon(), max.time(), props);
    }

    @Override
    public ViemSystem<String, String, Metadata> update(List<EntityState<String, String, Metadata>> matches,
            Set<EntityState<String, String, Metadata>> newEntityStates) {
        RTree<EntityState<String, String, Metadata>, Point> tree2 = tree;
        entities.removeAll(matches);
        for (EntityState<String, String, Metadata> e : entities) {
            tree2 = tree2.delete(e, e.metadata().point());
            removeFromOrderedByTime(e);
            for (Entry<String, String> entry : e.identifiers().entrySet()) {
                map.remove(new KeyValue(entry.getKey(), entry.getValue()));
            }
        }
        entities.addAll(newEntityStates);
        for (EntityState<String, String, Metadata> e : newEntityStates) {
            tree2 = tree2.add(e, e.metadata().point());
            addToOrderedByTime(e);
            for (Entry<String, String> entry : e.identifiers().entrySet()) {
                map.put(new KeyValue(entry.getKey(), entry.getValue()), e);
            }
        }
        tree = evictExpired(tree2);
        return this;
    }

    private RTree<EntityState<String, String, Metadata>, Point> evictExpired(
            RTree<EntityState<String, String, Metadata>, Point> tr) {
        if (options.maxAgeMs() > 0) {
            while (!orderedByTime.isEmpty()) {
                long t = orderedByTime.firstKey();
                if (t < options.clock().now() - options.maxAgeMs()) {
                    tr = evictTime(tr, t);
                } else {
                    // oldest is not old so don't evict any more
                    return tr;
                }
            }
        }
        // evict due max size reached
        while (options.maxEntities() > 0 && entities.size() > options.maxEntities()) {
            // evict oldest (may evict more than strictly necessary)
            // TODO just evict as many as necessary (though selection of which ones will be
            // arbitrary)
            tr = evictTime(tr, orderedByTime.firstKey());
        }
        return tr;
    }

    private RTree<EntityState<String, String, Metadata>, Point> evictTime(
            RTree<EntityState<String, String, Metadata>, Point> tr, long t) {
        List<EntityState<String, String, Metadata>> list = orderedByTime.remove(t);
        // remove from entities, map and tree2;
        entities.removeAll(list);
        for (EntityState<String, String, Metadata> e : list) {
            tr = tr.delete(e, e.metadata().point());
            for (Entry<String, String> entry : e.identifiers().entrySet()) {
                map.remove(new KeyValue(entry.getKey(), entry.getValue()));
            }
        }
        return tr;
    }

    private void addToOrderedByTime(EntityState<String, String, Metadata> e) {
        if (options.maxAgeMs() > 0) {
            List<EntityState<String, String, Metadata>> list = orderedByTime.get(e.metadata().time());
            if (list == null) {
                list = new ArrayList<>();
                orderedByTime.put(e.metadata().time(), list);
            }
            list.add(e);
        }
    }

    private void removeFromOrderedByTime(EntityState<String, String, Metadata> e) {
        if (options.maxAgeMs() > 0) {
            List<EntityState<String, String, Metadata>> list = orderedByTime.get(e.metadata().time());
            if (list != null) {
                list.remove(e);
                if (list.isEmpty()) {
                    orderedByTime.remove(e.metadata().time());
                }
            }
        }
    }

    // thread-safe
    public RTree<EntityState<String, String, Metadata>, Point> rtree() {
        return tree;
    }

}
