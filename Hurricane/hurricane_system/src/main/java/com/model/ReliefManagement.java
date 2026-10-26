package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class ReliefManagement {
    
    private static ReliefManagement reliefManagement;
    private ArrayList<ReliefRequest> requests;

    private ReliefManagement() {
        requests = DataLoader.getReliefRequests();
    }

    public static ReliefManagement getInstance(){
        if (reliefManagement == null) {
            reliefManagement = new ReliefManagement();
        }
        return reliefManagement;
    }

    public ArrayList<ReliefRequest> getReliefRequests(){
        return requests;
    }

    public ReliefRequest getReliefRequest(UUID id){
        for (ReliefRequest r : requests){
            if (r.getId().equals(id)) {
                return r;
            }
        }
        return null;
    }

    public ArrayList<ReliefRequest> getRequestsByStatus(ReliefRequestStatus status){
        ArrayList<ReliefRequest> statusRequests = new ArrayList<>();
            for (ReliefRequest r : requests){
                if (r.getStatus().equals(status)){
                    statusRequests.add(r);
                }
            }
        return statusRequests;
    }

    public void addReliefRequest(ReliefRequest request){
        requests.add(request);
    }

    public void removeReliefRequest(UUID id){
        for (ReliefRequest r: requests){
            if (r.getId().equals(id)){
                requests.remove(r);
            }
        }
    }

    //Q: How would this change anything?
    /*public ReliefRequest editReliefRequest(UUID id){
        for (ReliefRequest r: requests){
            if (r.getId().equals(id)){
                r = 
            }
        }
    } */

    public void save(){
        DataWriter.saveReliefRequests(requests);
    }
}