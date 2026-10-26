package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Facade for the hurricane relief system. The UI and drivers only talk to this
 * class, which passes work to the management singletons and keeps track of who
 * is signed in and which relief request and shelter are currently selected.
 *
 * @author Abaan Jafri
 */
public class ReliefSystem {

    private static ReliefSystem facade;

    private UserManagement userManagement;
    private ReliefManagement reliefManagement;
    private ShelterManagement shelterManagement;
    private HurricaneEventManagement eventManagement;

    private User currentUser;
    private ReliefRequest currentRequest;
    private Shelter currentShelter;
    private ArrayList<HazardReport> hazardReports = new ArrayList<>();

    // Private so the facade can only be made through getInstance()
    private ReliefSystem() {
        userManagement = UserManagement.getInstance();
        reliefManagement = ReliefManagement.getInstance();
        shelterManagement = ShelterManagement.getInstance();
        eventManagement = HurricaneEventManagement.getInstance();
    }

    /**
     * Returns the one shared ReliefSystem.
     *
     * @return the ReliefSystem facade
     */
    public static ReliefSystem getInstance() {
        if (facade == null) {
            facade = new ReliefSystem();
        }
        return facade;
    }

    // Throws if nobody is signed in
    private void requireLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is signed in");
        }
    }

    // Throws unless the signed-in user is an admin
    private Admin requireAdmin() {
        requireLogin();
        if (!currentUser.getRoles().contains(UserRole.ADMIN)) {
            throw new IllegalStateException("This action requires an admin");
        }
        // Admin is its own class (not a User subclass), so build one for the signed-in
        // admin
        return new Admin(currentUser.getFirstName(), currentUser.getLastName());
    }

    // Throws unless the signed-in user is a volunteer or professional
    private void requireResponder() {
        requireLogin();
        if (!currentUser.isResponder()) {
            throw new IllegalStateException("Only volunteers and professionals can do this");
        }
    }

    // Returns the selected request, or throws if none is selected
    private ReliefRequest requireRequest() {
        if (currentRequest == null) {
            throw new IllegalStateException("No relief request is selected");
        }
        return currentRequest;
    }

    // Returns the selected shelter, or throws if none is selected
    private Shelter requireShelter() {
        if (currentShelter == null) {
            throw new IllegalStateException("No shelter is selected");
        }
        return currentShelter;
    }

    /**
     * Creates a new user account.
     *
     * @param userName  the username, which must not already be taken
     * @param firstName the user's first name
     * @param lastName  the user's last name
     * @param password  the password, which cannot be blank
     * @param location  the user's home location
     * @return the new User, or null if the username is taken or a field is blank
     */
    public User registerUser(String userName, String firstName, String lastName,
            String password, Location location) {
        if (!userManagement.addUser(userName, firstName, lastName, password, location)) {
            return null;
        }
        return userManagement.getUser(userName.trim(), password);
    }

    /**
     * Signs a user in.
     *
     * @param userName the username
     * @param password the password
     * @return true if the username and password match an account
     */
    public boolean signIn(String userName, String password) {
        currentUser = userManagement.getUser(userName, password);
        return currentUser != null;
    }

    /** Saves everything and signs the user out. */
    public void signOut() {
        userManagement.saveUsers();
        reliefManagement.save();
        shelterManagement.save();
        currentUser = null;
        currentRequest = null;
        currentShelter = null;
    }

    /**
     * Returns the user who is signed in.
     *
     * @return the signed-in user, or null if nobody is signed in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Adds an emergency contact to the signed-in user.
     *
     * @param name         the contact's name
     * @param phone        the contact's phone number
     * @param relationship how the contact is related to the user
     */
    public void addEmergencyContact(String name, String phone, String relationship) {
        requireLogin();
        currentUser.addEmergencyContact(name, phone, relationship);
    }

    /**
     * Sets the signed-in user's safety status.
     *
     * @param category the user's safety category
     */
    public void markUserSafe(SafetyCategory category) {
        requireLogin();
        currentUser.markSafe(category);
    }

    /** Shares the signed-in user's location. */
    public void shareUserLocation() {
        requireLogin();
        currentUser.shareLocation();
    }

    /**
     * Submits a new relief request for the signed-in user and selects it.
     *
     * @param location     where help is needed
     * @param assistance   the kind of help needed
     * @param persons      the people who need help
     * @param pets         the pets that need help
     * @param contactPhone the phone number to reach the requester
     * @return the new relief request
     */
    public ReliefRequest submitReliefRequest(Location location, String assistance,
            ArrayList<Person> persons, ArrayList<Pet> pets, String contactPhone) {
        requireLogin();
        ReliefRequest request = new ReliefRequest(location, assistance);
        for (Person person : persons) {
            request.addPerson(person);
        }
        for (Pet pet : pets) {
            request.addPet(pet);
        }
        request.setContactPhone(contactPhone);
        request.submit();
        reliefManagement.addReliefRequest(request);
        currentRequest = request;
        return request;
    }

    /**
     * Selects an existing relief request by its id.
     *
     * @param requestId the request's id
     * @return the selected request
     */
    public ReliefRequest selectRequest(UUID requestId) {
        ReliefRequest request = null;
        for (ReliefRequest r : reliefManagement.getReliefRequests()) {
            if (r.getId().equals(requestId)) {
                request = r;
                break;
            }
        }
        if (request == null) {
            throw new IllegalArgumentException("No request with id " + requestId);
        }
        currentRequest = request;
        return request;
    }

    /**
     * Returns the status of the selected relief request.
     *
     * @return the selected request's status
     */
    public ReliefRequestStatus getReliefStatus() {
        return requireRequest().getStatus();
    }

    /**
     * Changes the status of the selected relief request.
     *
     * @param status the new status
     */
    public void updateRequestStatus(ReliefRequestStatus status) {
        requireRequest().updateStatus(status);
    }

    /** Lets a volunteer or professional claim the selected request. */
    public void claimRequest() {
        requireResponder();
        requireRequest().accept(currentUser);
    }

    /** Marks that help is on the way for the selected request. */
    public void respondToRequest() {
        requireResponder();
        requireRequest().updateStatus(ReliefRequestStatus.HELP_EN_ROUTE);
    }

    /** Escalates the selected request. */
    public void escalateRequest() {
        requireRequest().escalate();
    }

    /** Cancels the selected request. */
    public void cancelRequest() {
        requireRequest().cancel();
    }

    /**
     * Adds a comment from the signed-in user to the selected request.
     *
     * @param text the comment text
     */
    public void addRequestComment(String text) {
        requireLogin();
        requireRequest().addComment(new RequestComment(currentUser, text));
    }

    /**
     * Returns every comment on the selected request.
     *
     * @return the selected request's comments
     */
    public ArrayList<RequestComment> getRequestComments() {
        return requireRequest().getComments();
    }

    /**
     * Returns every request that has been submitted but not handled yet.
     *
     * @return the open relief requests
     */
    public ArrayList<ReliefRequest> listOpenRequests() {
        return reliefManagement.getRequestsByStatus(ReliefRequestStatus.SUBMITTED);
    }

    /**
     * Finds the closest shelter with room for the given number of people.
     *
     * @param location where the people are
     * @param people   how many people need space
     * @return the nearest shelter with room, or null if none found
     */
    public Shelter findSafeShelter(Location location, int people,
            boolean needsPetFriendly, boolean needsMedical) {
        return shelterManagement.findNearestAvailable(location, people,
                needsPetFriendly, needsMedical);
    }

    /**
     * Selects a shelter by its id.
     *
     * @param shelterId the shelter's id
     * @return the selected shelter
     */
    public Shelter selectShelter(UUID shelterId) {
        Shelter shelter = null;
        for (Shelter s : shelterManagement.getShelters()) {
            if (s.getId().equals(shelterId)) {
                shelter = s;
                break;
            }
        }
        if (shelter == null) {
            throw new IllegalArgumentException("No shelter with id " + shelterId);
        }
        currentShelter = shelter;
        return shelter;
    }

    /**
     * Sends the selected request to the given shelter.
     *
     * @param shelterId the shelter's id
     */
    public void assignShelterToRequest(UUID shelterId) {
        ReliefRequest request = requireRequest();
        request.setDestinationShelter(selectShelter(shelterId));
    }

    /**
     * Checks a citizen into the selected shelter.
     *
     * @param qrCode the citizen's QR code
     */
    public void checkInCitizen(String qrCode) {
        requireShelter().checkIn(qrCode);
    }

    /**
     * Checks a citizen out of the selected shelter.
     *
     * @param citizenId the citizen's id
     */
    public void checkOutCitizen(UUID citizenId) {
        requireShelter().checkOut(citizenId.toString());
    }

    /**
     * Replaces the resource of the same type, or adds it if it's new.
     *
     * @param resource the updated resource
     */
    public void updateShelterInventory(ShelterResource resource) {
        ArrayList<ShelterResource> resources = requireShelter().getShelterResources();
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getType().equalsIgnoreCase(resource.getType())) {
                resources.set(i, resource);
                return;
            }
        }
        resources.add(resource);
    }

    /**
     * Returns the selected shelter's resources that are running low.
     *
     * @return the low-stock resources
     */
    public ArrayList<ShelterResource> getLowStockResources() {
        return requireShelter().getLowStockResources();
    }

    /**
     * Lets an admin change the selected shelter's operational status.
     *
     * @param status the new status
     */
    public void changeShelterStatus(String status) {
        requireAdmin().changeShelterStatus(requireShelter(), status);
    }

    /**
     * Reports a hazard from the signed-in user.
     *
     * @param type     the kind of hazard
     * @param location where the hazard is
     * @return the new hazard report
     */
    public HazardReport reportHazard(HazardCategory type, Location location) {
        requireLogin();
        HazardReport report = new HazardReport(type, location, currentUser.getUserName());
        report.report();
        hazardReports.add(report);
        return report;
    }

    /**
     * Lets an admin verify a hazard report.
     *
     * @param hazardId the hazard report's id
     */
    public void verifyHazard(UUID hazardId) {
        requireAdmin();
        HazardReport hazard = null;
        for (HazardReport h : hazardReports) {
            if (h.getId().equals(hazardId)) {
                hazard = h;
                break;
            }
        }
        if (hazard == null) {
            throw new IllegalArgumentException("No hazard with id " + hazardId);
        }
        hazard.verify();
    }

    /**
     * Returns every hazard an admin has verified.
     *
     * @return the verified hazard reports
     */
    public ArrayList<HazardReport> listVerifiedHazards() {
        ArrayList<HazardReport> verified = new ArrayList<>();
        for (HazardReport h : hazardReports) {
            if (h.isVerified()) {
                verified.add(h);
            }
        }
        return verified;
    }

    /**
     * Lets an admin update where a hurricane is and where it is heading.
     *
     * @param eventId   the hurricane event's id
     * @param location  the hurricane's current location
     * @param direction the direction it is moving
     * @param path      its forecast path
     */
    public void updateHurricaneTrack(UUID eventId, Location location, String direction, String path) {
        requireAdmin();
        HurricaneEvent event = eventManagement.getHurricaneEvent(eventId);
        if (event == null) {
            throw new IllegalArgumentException("No event with id " + eventId);
        }
        event.updateTrack(location, direction, path);
    }

    /** Lets an admin approve the selected request. */
    public void approveRequest() {
        requireAdmin().approve(requireRequest());
    }

    /** Lets an admin deny the selected request. */
    public void denyRequest() {
        requireAdmin().deny(requireRequest());
    }

    /** Lets an admin archive the selected request. */
    public void archiveRequest() {
        requireAdmin().archiveRequest(requireRequest());
    }

    /**
     * Lets an admin change another user's role.
     *
     * @param userId the user's id
     * @param role   the new role
     */
    public void overrideUserRole(UUID userId, UserRole role) {
        Admin admin = requireAdmin();
        User target = userManagement.getUser(userId);
        if (target == null) {
            throw new IllegalArgumentException("No user with id " + userId);
        }
        admin.overrideRole(target, role);
    }

    /**
     * Lets an admin ban a user.
     *
     * @param userId the user's id
     */
    public void banUser(UUID userId) {
        Admin admin = requireAdmin();
        User target = userManagement.getUser(userId);
        if (target == null) {
            throw new IllegalArgumentException("No user with id " + userId);
        }
        admin.banUser(target);
    }

    /** Notifies every volunteer and professional about the selected request. */
    public void notifyResponders() {
        userManagement.notifyResponders(requireRequest());
    }

    /**
     * Lets an admin broadcast an emergency alert.
     *
     * @param message the alert text
     */
    public void publishEmergencyAlert(String message) {
        requireAdmin().broadcastAlert(message);
    }

    public void completeRequest() {
        requireLogin();
        if (!currentUser.getRoles().contains(UserRole.SHELTER_MANAGER)) {
            throw new IllegalStateException(
                    "Your volunteer role does not have permission to complete relief requests.");

        }
        requireRequest().complete();
    }
    public void exportTripInfo(String filePath){
        requireLogin();
        TripExport.exportTrip(requireRequest(), currentUser,
                requireShelter(), filePath);

    }
     public void checkInFamily(ArrayList<Person> persons, ArrayList<Pet> pets) {
        requireShelter().checkIn(persons, pets);
    }

    /**
     * Returns open requests ranked for the signed-in responder.
     *
     * @return open requests, best match first
     */
    public ArrayList<ReliefRequest> listRecommendedRequests() {
        requireResponder();
        return reliefManagement.getRecommendedRequests(currentUser);
    }

    /**
     * Explains why a request was recommended to the signed-in responder.
     *
     * @param request the request to explain
     * @return a short reason, or an empty string if nothing matched
     */
    public String explainRecommendation(ReliefRequest request) {
        requireLogin();
        return reliefManagement.getRecommendationReason(request, currentUser);
    }
}
