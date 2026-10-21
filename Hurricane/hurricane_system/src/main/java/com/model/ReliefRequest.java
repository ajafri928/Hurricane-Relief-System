package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class ReliefRequest {
    private UUID id;
    private String createdAt; 
    private Location location;
    private String assistance;
    private String description;
    private int people;
    private boolean specialNeeds;
    private Shelter destinationShelter;
    private String estimatedArrival;
    private int priorityScore;
    private UrgencyTier urgency;
    private ReliefRequestStatus status;
    private ArrayList<RequestComment> comments;
    private User submittedBy;

    public ReliefRequest(Location location, String assistance, int people) {
        this.id = UUID.randomUUID();
        this.location = location;
        this.assistance = assistance;
        this.people = people;
    }

    // Constructor 2 (Used by DataLoader)
    public ReliefRequest(UUID id, String createdAt, Location location, String assistance, 
                         String description, int people, boolean specialNeeds, 
                         int priorityScore, UrgencyTier urgency, ReliefRequestStatus status) {
        this.id = id;
        this.createdAt = createdAt;
        this.location = location;
        this.assistance = assistance;
        this.description = description;
        this.people = people;
        this.specialNeeds = specialNeeds;
        this.priorityScore = priorityScore;
        this.urgency = urgency;
        this.status = status;
        this.comments = new ArrayList<>();
        
    }

    public ReliefRequest(UUID id, String createdAt, Location location,
        String assistance, String description, int people,
        boolean specialNeeds, int priorityScore, UrgencyTier urgency,
        ReliefRequestStatus status, User submittedBy) {

        this(id, createdAt, location, assistance, description, people,
            specialNeeds, priorityScore, urgency, status);

        this.submittedBy = submittedBy;
    }

    public void submit(){
        // Stub
    }

    public void cancel(){
        // Stub
    }
    
    public void updateStatus(ReliefRequestStatus status) {
        // Stub
    }

    public void escalate() {
        // Stub
    }

    public void accept(User responder) {
        // Stub
    }

    public void decline() {
        // Stub
    }
    
    public void complete() {
        // Stub
    }

    public User getSubmittedBy() {
        return submittedBy;
    }

    public Shelter getDestinationShelter(){
        return destinationShelter;
    }

    public void setDestinationShelter(Shelter destinationShelter){
        this.destinationShelter = destinationShelter;
    }

    public void addComment(RequestComment comment) {
        // Stub
    }

    public ArrayList<RequestComment> getComments() {
        return new ArrayList<>(comments);
    }

        public String getDescription() {
        return description;
    }

    public boolean hasSpecialNeeds() {
        return specialNeeds;
    }

    public void setDestinationShelter(Shelter shelter) {
    this.destinationShelter = shelter;
}
    

    public UUID getId() { return id; }
    public String getCreatedAt() { return createdAt; }
    public Location getLocation() { return location; }
    public String getAssistance() { return assistance; }
    public int getPeople() { return people; }
    public int getPriorityScore() { return priorityScore; }
    public UrgencyTier getUrgency() { return urgency; }
    public ReliefRequestStatus getStatus() { return status; }

    @Override
    public String toString() {
        return "ReliefRequest {" +
                "\n  ID: " + id +
                "\n  Assistance: '" + assistance + '\'' +
                "\n  People Count: " + people +
                "\n  Priority Score: " + priorityScore +
                "\n  Urgency: " + urgency +
                "\n  Status: " + status +
                "\n  Submitted By: " +
                    (submittedBy != null ? submittedBy.getId() : "N/A") +
                "\n  Location: " + (location != null ? location.toString() : "N/A") +
                "\n}";
    }

}
