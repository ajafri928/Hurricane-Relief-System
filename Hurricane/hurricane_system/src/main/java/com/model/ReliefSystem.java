package com.model;

import java.util.ArrayList;
import java.util.UUID;


public class ReliefSystem {

    private static ReliefSystem facade;

    private UserManagement userManagement;
    private ReliefManagement reliefManagement;
    private ShelterManagement shelterManagement;
    private RegionManagement regionManagement;
    private HazardManagement hazardManagement;
    private HurricaneEventManagement eventManagement;

    private User currentUser;
    private ReliefRequest currentRequest;
    private Shelter currentShelter;

    private ReliefSystem() {
        userManagement = UserManagement.getInstance();
        reliefManagement = ReliefManagement.getInstance();
        shelterManagement = ShelterManagement.getInstance();
        regionManagement = RegionManagement.getInstance();
        hazardManagement = HazardManagement.getInstance();
        eventManagement = HurricaneEventManagement.getInstance();
    }

    public static ReliefSystem getInstance() {
        if (facade == null) {
            facade = new ReliefSystem();
        }
        return facade;
    }


    private void requireLogin() {
        if (currentUser == null) {
            throw new IllegalStateException("No user is signed in");
        }
    }

    private Admin requireAdmin() {
        requireLogin();
        if (!(currentUser instanceof Admin)) {
            throw new IllegalStateException("This action requires an admin");
        }
        return (Admin) currentUser;
    }

    private void requireResponder() {
        requireLogin();
        if (!currentUser.isResponder()) {
            throw new IllegalStateException("Only volunteers and professionals can do this");
        }
    }

    private ReliefRequest requireRequest() {
        if (currentRequest == null) {
            throw new IllegalStateException("No relief request is selected");
        }
        return currentRequest;
    }

    private Shelter requireShelter() {
        if (currentShelter == null) {
            throw new IllegalStateException("No shelter is selected");
        }
        return currentShelter;
    }


    public User registerUser(String userName, String firstName, String lastName,
                             String password, Location location) {
        if (userManagement.getUserByUserName(userName) != null) {
            throw new IllegalArgumentException("Username already taken: " + userName);
        }
        User user = new User(userName, firstName, lastName, password, location);
        userManagement.addUser(user);
        return user;
    }

    public boolean signIn(String userName, String password) {
        User user = userManagement.getUserByUserName(userName);
        if (user != null && user.signIn(userName, password)) {
            currentUser = user;
            return true;
        }
        return false;
    }

    public void signOut() {
        currentUser = null;
        currentRequest = null;
        currentShelter = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addEmergencyContact(String name, String phone, String relationship) {
        requireLogin();
        currentUser.addEmergencyContact(name, phone, relationship);
    }

    public void markUserSafe(SafetyCategory category) {
        requireLogin();
        currentUser.markSafe(category);
    }

    public void shareUserLocation() {
        requireLogin();
        currentUser.shareLocation();
    }


    public ReliefRequest submitReliefRequest(Location location, String assistance, int people) {
        requireLogin();
        ReliefRequest request = new ReliefRequest(location, assistance, people);
        request.submit();
        reliefManagement.addReliefRequest(request);
        currentRequest = request;
        return request;
    }

    public ReliefRequest selectRequest(UUID requestId) {
        ReliefRequest request = reliefManagement.getReliefRequest(requestId);
        if (request == null) {
            throw new IllegalArgumentException("No request with id " + requestId);
        }
        currentRequest = request;
        return request;
    }

    public ReliefRequestStatus getReliefStatus() {
        return requireRequest().getStatus();
    }

    public void updateRequestStatus(ReliefRequestStatus status) {
        requireRequest().updateStatus(status);
    }

    public void claimRequest() {
        requireResponder();
        requireRequest().accept(currentUser);
    }

    public void respondToRequest() {
        requireResponder();
        requireRequest().updateStatus(ReliefRequestStatus.HELP_EN_ROUTE);
    }

    public void escalateRequest() {
        requireRequest().escalate();
    }

    public void cancelRequest() {
        requireRequest().cancel();
    }

    public void completeRequest() {
        requireRequest().complete();
    }

    public void addRequestComment(String text) {
        requireLogin();
        requireRequest().addComment(new RequestComment(currentUser, text));
    }

    public ArrayList<RequestComment> getRequestComments() {
        return requireRequest().getComments();
    }

    public ArrayList<ReliefRequest> listOpenRequests() {
        return reliefManagement.getRequestsByStatus(ReliefRequestStatus.SUBMITTED);
    }

    public ArrayList<ReliefRequest> listRequestsByRegion(UUID regionId) {
        return reliefManagement.getRequestsByRegion(regionId);
    }


    public Shelter findSafeShelter(Location location, int people) {
        return shelterManagement.findNearestAvailable(location, people);
    }

    public Shelter selectShelter(UUID shelterId) {
        Shelter shelter = shelterManagement.getShelter(shelterId);
        if (shelter == null) {
            throw new IllegalArgumentException("No shelter with id " + shelterId);
        }
        currentShelter = shelter;
        return shelter;
    }

    public void assignShelterToRequest(UUID shelterId) {
        ReliefRequest request = requireRequest();
        request.setDestinationShelter(selectShelter(shelterId));
    }

    public void checkInCitizen(String qrCode) {
        requireShelter().checkIn(qrCode);
    }

    public void checkOutCitizen(UUID citizenId) {
        requireShelter().checkOut(citizenId.toString());
    }

    public void updateShelterInventory(ShelterResource resource) {
        requireShelter().updateInventory(resource);
    }

    public ArrayList<ShelterResource> getLowStockResources() {
        return requireShelter().getLowStockResources();
    }

    public void changeShelterStatus(String status) {
        requireAdmin().changeShelterStatus(requireShelter(), status);
    }


    public HazardReport reportHazard(HazardCategory type, Location location) {
        requireLogin();
        HazardReport report = new HazardReport(type, location, currentUser.getUserName());
        report.report();
        hazardManagement.addHazard(report);
        return report;
    }

    public void verifyHazard(UUID hazardId) {
        requireAdmin();
        HazardReport hazard = hazardManagement.getHazard(hazardId);
        if (hazard == null) {
            throw new IllegalArgumentException("No hazard with id " + hazardId);
        }
        hazard.verify();
    }

    public ArrayList<HazardReport> listVerifiedHazards() {
        return hazardManagement.getVerifiedHazards();
    }


    public GeographicRegion createRegion(String name, String boundary) {
        requireAdmin();
        GeographicRegion region = new GeographicRegion(name, boundary);
        regionManagement.addRegion(region);
        return region;
    }

    public void setEvacuationStatus(UUID regionId, ZoneType zoneType) {
        requireAdmin();
        GeographicRegion region = regionManagement.getRegion(regionId);
        if (region == null) {
            throw new IllegalArgumentException("No region with id " + regionId);
        }
        region.setZoneType(zoneType);
    }

    public void updateHurricaneTrack(String eventName) {
        requireAdmin();
        HurricaneEvent event = eventManagement.getEvent(eventName);
        if (event == null) {
            throw new IllegalArgumentException("No event named " + eventName);
        }
        event.updateTrack();
    }

    public HurricaneEvent getActiveEvent() {
        return eventManagement.getActiveEvent();
    }


    public void approveRequest() {
        requireAdmin().approve(requireRequest());
    }

    public void denyRequest() {
        requireAdmin().deny(requireRequest());
    }

    public void archiveRequest() {
        requireAdmin().archiveRequest(requireRequest());
    }

    public void overrideUserRole(UUID userId, UserRole role) {
        Admin admin = requireAdmin();
        User target = userManagement.getUser(userId);
        if (target == null) {
            throw new IllegalArgumentException("No user with id " + userId);
        }
        admin.overrideRole(target, role);
    }

    public void banUser(UUID userId) {
        Admin admin = requireAdmin();
        User target = userManagement.getUser(userId);
        if (target == null) {
            throw new IllegalArgumentException("No user with id " + userId);
        }
        admin.banUser(target);
    }

    public void notifyResponders() {
        userManagement.notifyResponders(requireRequest());
    }

    public void publishEmergencyAlert(String message) {
        requireAdmin().broadcastAlert(message);
    }
}
