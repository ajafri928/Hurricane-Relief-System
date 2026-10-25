package com.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

/**
 * A request for help submitted by a user during a hurricane.
 */
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
    private User responder;

    /**
     * Makes a brand-new request.
     *
     * @param location   where help is needed
     * @param assistance the kind of help needed
     * @param people     how many people need help
     */
    public ReliefRequest(Location location, String assistance, int people) {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now().toString();
        this.location = location;
        this.assistance = assistance;
        this.people = people;
        this.comments = new ArrayList<>();
    }

    /**
     * Rebuilds a saved request (used by DataLoader).
     *
     * @param id            the request's id
     * @param createdAt     when it was created
     * @param location      where help is needed
     * @param assistance    the kind of help needed
     * @param description   extra details
     * @param people        how many people need help
     * @param specialNeeds  whether anyone has special needs
     * @param priorityScore the priority score
     * @param urgency       the urgency tier
     * @param status        the current status
     */
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

    /**
     * Rebuilds a saved request along with who submitted it (used by DataLoader).
     *
     * @param id            the request's id
     * @param createdAt     when it was created
     * @param location      where help is needed
     * @param assistance    the kind of help needed
     * @param description   extra details
     * @param people        how many people need help
     * @param specialNeeds  whether anyone has special needs
     * @param priorityScore the priority score
     * @param urgency       the urgency tier
     * @param status        the current status
     * @param submittedBy   the user who submitted it
     */
    public ReliefRequest(UUID id, String createdAt, Location location,
                         String assistance, String description, int people,
                         boolean specialNeeds, int priorityScore, UrgencyTier urgency,
                         ReliefRequestStatus status, User submittedBy) {
        this(id, createdAt, location, assistance, description, people,
             specialNeeds, priorityScore, urgency, status);
        this.submittedBy = submittedBy;
    }

    // Marks the request as submitted.
    public void submit() {
        status = ReliefRequestStatus.SUBMITTED;
    }

    // Cancels the request.
    public void cancel() {
        // Stub: there's no CANCELLED status in ReliefRequestStatus yet
    }

    /**
     * Changes the request's status.
     *
     * @param status the new status
     */
    public void updateStatus(ReliefRequestStatus status) {
        this.status = status;
    }

    // Escalates the request to 911.
    public void escalate() {
        status = ReliefRequestStatus.ESCALATED_TO_911;
    }

    /**
     * A responder claims the request.
     *
     * @param responder the volunteer or professional taking it
     */
    public void accept(User responder) {
        this.responder = responder;
        status = ReliefRequestStatus.CLAIMED;
    }

    // The responder drops the request so someone else can take it.
    public void decline() {
        responder = null;
        status = ReliefRequestStatus.SUBMITTED;
    }

    // Marks the request as resolved.
    public void complete() {
        // Stub
    }

    /**
     * Adds a comment to the request.
     *
     * @param comment the comment to add
     */
    public void addComment(RequestComment comment) {
        comments.add(comment);
    }

    // Returns a copy of the request's comments
    public ArrayList<RequestComment> getComments() {
        return new ArrayList<>(comments);
    }

    // Returns the user who submitted the request
    public User getSubmittedBy() {
        return submittedBy;
    }

    // Returns the responder who claimed the request, or null
    public User getResponder() {
        return responder;
    }

    // Returns the shelter the request was sent to
    public Shelter getDestinationShelter() {
        return destinationShelter;
    }

    // Sets the shelter to send the request to
    public void setDestinationShelter(Shelter destinationShelter) {
        this.destinationShelter = destinationShelter;
    }

    // Returns extra details about the request
    public String getDescription() {
        return description;
    }

    // Returns true if anyone has special needs
    public boolean hasSpecialNeeds() {
        return specialNeeds;
    }

    // Returns the request's id
    public UUID getId() { return id; }

    // Returns when the request was created
    public String getCreatedAt() { return createdAt; }

    // Returns where help is needed
    public Location getLocation() { return location; }

    // Returns the kind of help needed
    public String getAssistance() { return assistance; }

    // Returns how many people need help
    public int getPeople() { return people; }

    // Returns the priority score
    public int getPriorityScore() { return priorityScore; }

    // Returns the urgency tier
    public UrgencyTier getUrgency() { return urgency; }

    // Returns the current status
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
