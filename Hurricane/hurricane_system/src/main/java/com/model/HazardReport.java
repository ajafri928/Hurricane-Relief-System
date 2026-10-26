package com.model;
import java.time.LocalDateTime;
import java.util.UUID;

/** A reported hazard and whether it's verified. */
public class HazardReport {
    private UUID id;
    private HazardCategory type;
    private Location location;
    private boolean verified;
    private String source;
    private LocalDateTime timeStamp;

    /** @param type hazard type @param location hazard location @param source who reported it */
    public HazardReport(HazardCategory type, Location location, String source){
        //Q: Added UUID to UML because we need instance
        this.id = UUID.randomUUID();
        this.type = type;
        this.location = location;
        this.source = source;
        this.verified = false;
        this.timeStamp = LocalDateTime.now();
    }

    /** @param type hazard type @param location hazard location @param verified if confirmed @param source who reported it @param timeStamp when reported */
    public HazardReport(HazardCategory type, Location location, boolean verified, String source, LocalDateTime timeStamp){
        this.id = UUID.randomUUID();
        this.type = type;
        this.location = location;
        this.verified = verified;
        this.source = source;
        this.timeStamp = timeStamp;
    }

    /** Submits the report. */
    public void report(){
        //Q: Not entirely sure what should go in here
        //Q: time reported? Summary of whole report?
    }

    /** Marks the hazard as verified. */
    public void verify(){
        this.verified = true;
    }

    /** @return id */
    public UUID getId() { return id; }
    /** @return type */
    public HazardCategory getType() { return type; }
    /** @return location */
    public Location getLocation() { return location; }
    /** @return if verified */
    public boolean isVerified() { return verified; }
    /** @return source */
    public String getSource() { return source; }
    /** @return time reported */
    public LocalDateTime getTimeStamp() { return timeStamp; }

    /** @param id id */
    public void setId(UUID id) { this.id = id; }
    /** @param type type */
    public void setType(HazardCategory type) { this.type = type; }
    /** @param location location */
    public void setLocation(Location location) { this.location = location; }
    /** @param verified if verified */
    public void setVerified(boolean verified) { this.verified = verified; }
    /** @param source source */
    public void setSource(String source) { this.source = source; }
    /** @param timeStamp time reported */
    public void setTimeStamp(LocalDateTime timeStamp) { this.timeStamp = timeStamp; }
}