package com.github.davidmoten.etim;

public final class TimestampedString {

    private final long time;
    private final String value;

    public TimestampedString(long time, String value) {
        this.time = time;
        this.value = value;
    }
    
    public long time() {
        return time;
    }
    
    public String value() {
        return value;
    }

}
