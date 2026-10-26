package com.model;

import java.time.LocalDateTime;
import java.util.UUID;

/** A hurricane and its current track. */
public class HurricaneEvent {
    private UUID id;
    private String name;
    private int category;
    private String status;
    private String startDate;
    private String endDate;
    private Location location;
    private float windSpeed;
    private String direction;
    private String path;
    private LocalDateTime estimatedArrivalTime;

    /** @param id id @param name name @param category category 1-5 @param status status */
    public HurricaneEvent(UUID id, String name, int category, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
    }

    /** @param location current location @param direction direction moving @param path expected path */
    public void updateTrack(Location location, String direction, String path) {
        this.location = location;
        this.direction = direction;
        this.path = path;
    }

    /** @return id */
    public UUID getId() { return id; }
    /** @return name */
    public String getName() { return name; }
    /** @return category */
    public int getCategory() { return category; }
    /** @return status */
    public String getStatus() { return status; }
    /** @return start date */
    public String getStartDate() { return startDate; }
    /** @return end date */
    public String getEndDate() { return endDate; }
    /** @return location */
    public Location getLocation() { return location; }
    /** @return wind speed */
    public float getWindSpeed() { return windSpeed; }
    /** @return direction */
    public String getDirection() { return direction; }
    /** @return path */
    public String getPath() { return path; }
    /** @return estimated arrival time */
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }

    /** @param location location */
    public void setLocation(Location location) { this.location = location; }
    /** @param windSpeed wind speed */
    public void setWindSpeed(float windSpeed) { this.windSpeed = windSpeed; }
    /** @param direction direction */
    public void setDirection(String direction) { this.direction = direction; }
    /** @param path path */
    public void setPath(String path) { this.path = path; }
    /** @param estimatedArrivalTime estimated arrival time */
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { 
        this.estimatedArrivalTime = estimatedArrivalTime; 
    }

    /** @return the event as a string */
    @Override
    public String toString() {
        return "HurricaneEvent {" +
                "\n  ID: " + id +
                "\n  Name: '" + name + '\'' +
                "\n  Category: " + category +
                "\n  Status: '" + status + '\'' +
                "\n}";
    }
}