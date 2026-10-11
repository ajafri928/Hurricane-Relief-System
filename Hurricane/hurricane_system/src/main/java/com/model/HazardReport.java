package com.model;
import java.time.LocalDateTime;
import java.util.UUID;


public class HazardReport {
    private UUID id;
    private HazardCategory type;
    private Location location;
    private boolean verified;
    private String source;
    private LocalDateTime timeStamp;

    public HazardReport(HazardCategory type, Location location, String source){
        //Q: Added UUID to UML because we need instance
        this.id = UUID.randomUUID();
        this.type = type;
        this.location = location;
        this.source = source;
        this.verified = false;
        this.timeStamp = LocalDateTime.now();
    }
    public HazardReport(HazardCategory type, Location location, boolean verified, String source, LocalDateTime timeStamp){
        this.id = UUID.randomUUID();
        this.type = type;
        this.location = location;
        this.verified = verified;
        this.source = source;
        this.timeStamp = timeStamp;
    }
    
    public void report(){
        //Q: Not entirely sure what should go in here
        //Q: time reported? Summary of whole report?
    }

    public void verify(){
        this.verified = true;
    }

    // Getters
    public UUID getId() { return id; }
    public HazardCategory getType() { return type; }
    public Location getLocation() { return location; }
    public boolean isVerified() { return verified; }
    public String getSource() { return source; }
    public LocalDateTime getTimeStamp() { return timeStamp; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setType(HazardCategory type) { this.type = type; }
    public void setLocation(Location location) { this.location = location; }
    public void setVerified(boolean verified) { this.verified = verified; }
    public void setSource(String source) { this.source = source; }
    public void setTimeStamp(LocalDateTime timeStamp) { this.timeStamp = timeStamp; }
}
