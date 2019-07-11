package com.github.davidmoten.etim;

import java.util.Collections;
import java.util.Map;

import com.github.davidmoten.rtree2.geometry.Geometries;
import com.github.davidmoten.rtree2.geometry.Point;

public final class Metadata {

    private final String entityType;
    private final double lat;
    private final double lon;
    private final long time;
    private final Map<String, TimestampedString> properties;
    private final Point point;

    public Metadata(String type, double lat, double lon, long time,
            Map<String, TimestampedString> properties) {
        this.entityType = type;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.properties = Collections.unmodifiableMap(properties);
        this.point = Geometries.pointGeographic(lon, lat);
    }

    public String type() {
        return entityType;
    }

    public double lat() {
        return lat;
    }

    public double lon() {
        return lon;
    }

    public long time() {
        return time;
    }

    public Map<String, TimestampedString> properties() {
        return properties;
    }

    public Point point() {
        return point;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((entityType == null) ? 0 : entityType.hashCode());
        long temp;
        temp = Double.doubleToLongBits(lat);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(lon);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        result = prime * result + ((properties == null) ? 0 : properties.hashCode());
        result = prime * result + (int) (time ^ (time >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Metadata other = (Metadata) obj;
        if (entityType == null) {
            if (other.entityType != null)
                return false;
        } else if (!entityType.equals(other.entityType))
            return false;
        if (Double.doubleToLongBits(lat) != Double.doubleToLongBits(other.lat))
            return false;
        if (Double.doubleToLongBits(lon) != Double.doubleToLongBits(other.lon))
            return false;
        if (properties == null) {
            if (other.properties != null)
                return false;
        } else if (!properties.equals(other.properties))
            return false;
        if (time != other.time)
            return false;
        return true;
    }

}
