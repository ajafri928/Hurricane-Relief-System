package com.model;

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

<<<<<<< HEAD
    public HurricaneEvent(String name, int category, Location location, String direction){
    this.name = name;
    this.category = category;
    this.location = location;
    this.direction = direction;
    }

    public HurricaneEvent(String name, int category, Location location, float windSpeed, String direction, String path, LocalDateTime estimatedArrivalTime){
    this.name = name;
    this.category = category;
    this.location = location;
    this.windSpeed = windSpeed;
    this.direction = direction;
    this.path = path;
    this.estimatedArrivalTime = estimatedArrivalTime;
    }

    public void updateTrack(Location location, String direction, String path){
=======
    public void updateTrack(Location location, String direction, String path) {
>>>>>>> 8d747f88a80de8f100aa3ccbb8711c5218ab7601
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
<<<<<<< HEAD
    public LocalDateTime getEstimatedArrivalTime() { return estimatedArrivalTime; }
=======
    public String getEstimatedArrivalTime() { return estimatedArrivalTime; }
>>>>>>> 8d747f88a80de8f100aa3ccbb8711c5218ab7601

    public void setLocation(Location location) { this.location = location; }
    public void setWindSpeed(float windSpeed) { this.windSpeed = windSpeed; }
    public void setDirection(String direction) { this.direction = direction; }
    public void setPath(String path) { this.path = path; }
<<<<<<< HEAD
    public void setEstimatedArrivalTime(LocalDateTime estimatedArrivalTime) {
    this.estimatedArrivalTime = estimatedArrivalTime; }
=======
    public void setEstimatedArrivalTime(String estimatedArrivalTime) { 
        this.estimatedArrivalTime = estimatedArrivalTime; 
    }
>>>>>>> 8d747f88a80de8f100aa3ccbb8711c5218ab7601

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
