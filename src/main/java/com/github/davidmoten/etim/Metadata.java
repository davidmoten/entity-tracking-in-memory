package com.github.davidmoten.etim;

import java.util.Map;

public final class Metadata {

    private final String type;
    private final double lat;
    private final double lon;
    private final long time;
    private final Map<String, String> properties;

    public Metadata(String type, double lat, double lon, long time,
            Map<String, String> properties) {
        this.type = type;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
        this.properties = properties;
    }

    public String type() {
        return type;
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

    public Map<String, String> properties() {
        return properties;
    }

}
