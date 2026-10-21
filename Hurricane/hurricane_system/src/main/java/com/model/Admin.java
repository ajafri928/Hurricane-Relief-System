package com.model;

public class Admin {
    public String accessLevel;
    public String firstName;
    public String lastName;

    public Admin(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    public Admin(String firstName, String lastName, String accessLevel){
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessLevel = accessLevel;
    } 

    public void approve (ReliefRequest request){
        request.updateStatus(ReliefRequestStatus.APPROVED);
    }

    public void deny (ReliefRequest request){
        request.updateStatus(ReliefRequestStatus.DENIED);
    }

    public void overrideRole (User user, UserRole role){
        //user.

    }

    public void broadcastAlert(String message){

    }

    public void archiveRequest(ReliefRequest request){

    }

    public void changeShelterStatus(Shelter shelter, String status){

    }

    public int calculatePriorityScore(ReliefRequest request){
        return 0;
    }

    public void drawDisasterZone(GeographicRegion region){

    }

    public void banUser (User user){

    }

}
