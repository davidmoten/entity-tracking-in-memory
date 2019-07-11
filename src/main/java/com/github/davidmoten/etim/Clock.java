package com.github.davidmoten.etim;

public interface Clock {

    default long now() {
        return System.currentTimeMillis();
    }

}
