package com.model;

import java.util.ArrayList;
import java.util.UUID;
import java.util.HashMap;
import com.location.MapTools;
import com.location.Coordinates;
import com.location.DrivingRoute;

/** Singleton that stores and manages relief requests. */
public class ReliefManagement {
    
    private static ReliefManagement reliefManagement;
    private ArrayList<ReliefRequest> requests;
    private final HashMap<UUID, Double> distanceCache = new HashMap<>();


    private ReliefManagement() {
        requests = DataLoader.getReliefRequests();
    }

    /** @return the single instance */
    public static ReliefManagement getInstance(){
        if (reliefManagement == null) {
            reliefManagement = new ReliefManagement();
        }
        return reliefManagement;
    }

    /** @return all relief requests */
    public ArrayList<ReliefRequest> getReliefRequests(){
        return requests;
    }

    /** @param id request id @return the matching request, or null */
    public ReliefRequest getReliefRequest(UUID id){
        for (ReliefRequest r : requests){
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    /** @param status status to filter by @return requests with that status */
    public ArrayList<ReliefRequest> getRequestsByStatus(ReliefRequestStatus status){
        ArrayList<ReliefRequest> statusRequests = new ArrayList<>();
        for (ReliefRequest r : requests){
            if (r.getStatus() == status){
                statusRequests.add(r);
            }
        }
        return statusRequests;
    }

    /** @param request request to add */
    public void addReliefRequest(ReliefRequest request){
        requests.add(request);
    }

    /** @param id id of request to remove */
    public void removeReliefRequest(UUID id){
        requests.removeIf(r -> r.getId().equals(id));
    }

    /** Saves all requests to the JSON file. */
    public void save(){
        DataWriter.saveReliefRequests(requests);
    }

    /**
     * Returns open requests ranked for this responder, best match first.
     *
     * @param user the responder browsing requests
     * @return open requests sorted by descending recommendation score
     */
    public ArrayList<ReliefRequest> getRecommendedRequests(User user) {
        ArrayList<ReliefRequest> open =
                getRequestsByStatus(ReliefRequestStatus.SUBMITTED);
        distanceCache.clear();

        HashMap<UUID, Double> scores = new HashMap<>();
        for (ReliefRequest r : open) {
            scores.put(r.getId(), scoreRequest(r, user));
        }
        open.sort((a, b) -> Double.compare(
                scores.get(b.getId()), scores.get(a.getId())));
        return open;
    }

    /**
     * Explains in plain words why a request was recommended to a responder.
     *
     * @param request the request being explained
     * @param user    the responder it was recommended to
     * @return a short reason, or an empty string if nothing matched
     */
    public String getRecommendationReason(ReliefRequest request, User user) {
        ArrayList<String> reasons = new ArrayList<>();
        if (matchesSpecialty(request, user)) {
            reasons.add("matches your "
                    + user.getSpecialty().toString().toLowerCase().replace('_', ' ')
                    + " specialty");
        }
        double miles = milesTo(user, request);
        if (miles >= 0 && miles <= 15) {
            reasons.add(String.format("%.1f miles from you", miles));
        }
        if (request.getUrgency() == UrgencyTier.LIFE_THREATENING) {
            reasons.add("life-threatening");
        } else if (request.getUrgency() == UrgencyTier.URGENT) {
            reasons.add("marked urgent");
        }
        if (reasons.isEmpty()) {
            return "";
        }
        String reason = reasons.get(0);
        for (int i = 1; i < reasons.size(); i++) {
            reason += (i == reasons.size() - 1) ? " and " : ", ";
            reason += reasons.get(i);
        }
        return "Recommended: " + reason + ".";
    }

    /** Scores how well a request suits a responder. Higher is better. */
    private double scoreRequest(ReliefRequest request, User user) {
        double score = 0;
        if (matchesSpecialty(request, user)) {
            score += 50;
        }
        double miles = milesTo(user, request);
        if (miles >= 0 && miles <= 15) {
            score += 30 * (1 - miles / 15);   // 30 points at 0 miles, fading to 0 at 15
        }
        if (request.getUrgency() == UrgencyTier.LIFE_THREATENING) {
            score += 30;
        } else if (request.getUrgency() == UrgencyTier.URGENT) {
            score += 15;
        }
        return score;
    }

    /** True when the request's assistance text matches the responder's specialty. */
    private boolean matchesSpecialty(ReliefRequest request, User user) {
        if (user.getSpecialty() == null || request.getAssistance() == null) {
            return false;
        }
        String need = request.getAssistance().toLowerCase();
        switch (user.getSpecialty()) {
            case TRANSPORTATION:
                return need.contains("transport") || need.contains("evacuat")
                        || need.contains("ride");
            case EMS:
            case CPR_CERTIFIED:
                return need.contains("medical") || need.contains("injur");
            default:
                return false;
        }
    }

    /** Driving miles from the responder to the request, looked up once and cached. */
    private double milesTo(User user, ReliefRequest request) {
        Double cached = distanceCache.get(request.getId());
        if (cached != null) {
            return cached;
        }
        double miles = drivingMiles(user.getLocation(), request.getLocation());
        if (miles < 0) {
            miles = milesBetween(user.getLocation(), request.getLocation());
        }
        distanceCache.put(request.getId(), miles);
        return miles;
    }

    /** Road distance in miles along MapTools' route, or -1 if it can't be found. */
    private double drivingMiles(Location from, Location to) {
        if (from == null || to == null) {
            return -1;
        }
        try {
            DrivingRoute route = MapTools.getInstance()
                    .getDrivingRoute(from.toAddress(), to.toAddress());
            ArrayList<Coordinates> points = route.getRoutePoints();
            if (points == null || points.size() < 2) {
                return -1;
            }
            double miles = 0;
            for (int i = 1; i < points.size(); i++) {
                miles += haversine(
                        points.get(i - 1).getLatitude(), points.get(i - 1).getLongitude(),
                        points.get(i).getLatitude(), points.get(i).getLongitude());
            }
            return miles;
        } catch (Exception e) {
            System.err.println("Driving distance unavailable, using straight-line: "
                    + e.getMessage());
            return -1;
        }
    }

    /**
     * Straight-line distance in miles between two locations.
     * Returns -1 when either location has no coordinates.
     */
    private double milesBetween(Location a, Location b) {
        if (a == null || b == null) {
            return -1;
        }
        if (a.getLatitude() == 0 || b.getLatitude() == 0) {
            return -1;
        }
        return haversine(a.getLatitude(), a.getLongitude(),
                b.getLatitude(), b.getLongitude());
    }

    /** Haversine distance in miles between two latitude/longitude points. */
    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        double earthRadiusMiles = 3958.8;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double h = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadiusMiles * 2 * Math.atan2(Math.sqrt(h), Math.sqrt(1 - h));
    }
        
}
