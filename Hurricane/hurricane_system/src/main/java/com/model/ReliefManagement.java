package com.model;

import java.util.ArrayList;
import java.util.UUID;

/** Singleton that stores and manages relief requests. */
public class ReliefManagement {
    
    private static ReliefManagement reliefManagement;
    private ArrayList<ReliefRequest> requests;

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
}