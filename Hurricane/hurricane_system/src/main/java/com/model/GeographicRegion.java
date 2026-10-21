package com.model;

import java.util.UUID;

public class GeographicRegion {
    private String name;
    private String boundary;
    private ZoneType zoneType;
    private String evacuationStatus;

    public GeographicRegion(String name, String boundary){
        this.name = name;
        this.boundary = boundary;
    }

    public GeographicRegion(String name, String boundary, ZoneType zoneType, String evacuationStatus){
        this.name = name;
        this.boundary = boundary;
        this.zoneType = zoneType;
        this.evacuationStatus = evacuationStatus;
    }

    public void drawBoundary(String coordinates){
        this.boundary = coordinates;
    }

    public String getName() { return name; }
    public ZoneType getZoneType() { return zoneType; }
    public void setZoneType(ZoneType zoneType) { this.zoneType = zoneType; }

}
