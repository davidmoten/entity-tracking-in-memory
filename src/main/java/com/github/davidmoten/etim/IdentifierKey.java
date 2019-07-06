package com.github.davidmoten.etim;

public enum IdentifierKey {

    VESSEL_IMO_NUMBER(1), VESSEL_MMSI(2), VESSEL_CALLSIGN(3), VESSEL_INMARSAT_NO_PRIMARY(4),
    VESSEL_INMARSAT_NO_SECONDARY(5), AIRCRAFT_REGISTRATION(1), AIRCRAFT_CALLSIGN(2),
    VEHICLE_REGISTRATION(1), TRACKING_DEVICE_SERIAL_NO(1);

    private final int priority;

    private IdentifierKey(int priority) {
        this.priority = priority;
    }
    
    public int priority() {
        return priority;
    }

}
