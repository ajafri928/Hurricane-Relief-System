package com.model;

import java.time.LocalDateTime;
import java.util.UUID;

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

    // Primary constructor used by DataLoader
    public HurricaneEvent(UUID id, String name, int category, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
    }

    public void updateTrack(Location location, String direction, String path) {
        this.location = location;
        this.direction = direction;
        this.path = path;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public int getCategory() { return category; }
    public String getStatus() { return status; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public Location getLocation() { return location; }
    public float getWindSpeed() { return windSpeed; }
    public String getDirection() { return direction; }
    public String getPath() { return path; }
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }

    public void setLocation(Location location) { this.location = location; }
    public void setWindSpeed(float windSpeed) { this.windSpeed = windSpeed; }
    public void setDirection(String direction) { this.direction = direction; }
    public void setPath(String path) { this.path = path; }
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) { 
        this.estimatedArrivalTime = estimatedArrivalTime; 
    }

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
