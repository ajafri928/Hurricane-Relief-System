package com.model;

/**
 * An administrator who can approve, deny, and manage relief requests,
 * shelters, and users. Admin is its own class, not a subclass of User.
 */
public class Admin{
    public String accessLevel;
    public String firstName;
    public String lastName;

    /**
     * Makes an admin with the default access level.
     *
     * @param firstName the admin's first name
     * @param lastName  the admin's last name
     */
    public Admin(String firstName, String lastName){
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    /**
     * Makes an admin with a specific access level.
     *
     * @param firstName   the admin's first name
     * @param lastName    the admin's last name
     * @param accessLevel the admin's access level
     */
    public Admin(String firstName, String lastName, String accessLevel){
        this.firstName = firstName;
        this.lastName = lastName;
        this.accessLevel = accessLevel;
    } 

    /**
     * Approves a relief request.
     *
     * @param request the request to approve
     */
    public void approve (ReliefRequest request){
        request.updateStatus(ReliefRequestStatus.APPROVED);
    }

    /**
     * Denies a relief request.
     *
     * @param request the request to deny
     */
    public void deny (ReliefRequest request){
        request.updateStatus(ReliefRequestStatus.DENIED);
    }

    /**
     * Changes another user's role. (Not implemented yet.)
     *
     * @param user the user to change
     * @param role the new role
     */
    public void overrideRole (User user, UserRole role){
        //user.

    }

    /**
     * Sends an emergency alert to everyone. (Not implemented yet.)
     *
     * @param message the alert text
     */
    public void broadcastAlert(String message){

    }

    /**
     * Archives a finished relief request. (Not implemented yet.)
     *
     * @param request the request to archive
     */
    public void archiveRequest(ReliefRequest request){

    }

    /**
     * Changes a shelter's operational status. (Not implemented yet.)
     *
     * @param shelter the shelter to change
     * @param status  the new status, like "OPEN" or "AT_CAPACITY"
     */
    public void changeShelterStatus(Shelter shelter, String status){

    }

    /**
     * Scores how urgent a relief request is. (Not implemented yet.)
     *
     * @param request the request to score
     * @return the priority score (currently always 0)
     */
    public int calculatePriorityScore(ReliefRequest request){
        return 0;
    }

    /**
     * Bans a user from the system. (Not implemented yet.)
     *
     * @param user the user to ban
     */
    public void banUser (User user){

    }

}