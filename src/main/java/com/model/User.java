package com.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


/**
 * Represents a user account in the hurricane relief system.
 * Stores profile information, roles, safety status, emergency contacts,
 * and volunteer specialty.
 */
public class User {

     /**
     * Creates a new user with a unique ID and empty role and emergency contact lists.
     *
     * @param userName the user's username
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @param location the user's location
     */
    private UUID id;
    private String userName;
    private String firstName;
    private String lastName;
    private String password;
    private Location location;
    protected SafetyCategory safety;
    protected List<UserRole> roles;
    private List<EmergencyContact> emergencyContacts;
    private VolunteerRole specialty;
    private String contactPhone;


    /**
     * Rebuilds a saved user loaded from the JSON file.
     * Creates Emergency Contact List.
     *
     * @param id the user's saved ID
     * @param userName the user's username
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @param location the user's location
     * @param safety the user's safety status
     * @param roles the user's roles, or null for an empty role list
     */
    public User(String userName, String firstName, String lastName, String password, Location location) {
        this.id = UUID.randomUUID();
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.location = location;
        this.roles = new ArrayList<>();
        this.emergencyContacts = new ArrayList<>();
    }

     /**
     * Rebuilds a saved user loaded from the JSON file.
     * Initializes an empty emergency contact list.
     *
     * @param id the user's saved ID
     * @param userName the user's username
     * @param firstName the user's first name
     * @param lastName the user's last name
     * @param password the user's password
     * @param location the user's location
     * @param safety the user's safety status
     * @param roles the user's roles, or null for an empty role list
     */
    public User(UUID id, String userName, String firstName, String lastName, String password,
            Location location, SafetyCategory safety, ArrayList<UserRole> roles) {
        this.id = id;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.location = location;
        this.safety = safety;
        this.roles = (roles == null) ? new ArrayList<>() : roles;
        this.emergencyContacts = new ArrayList<>();
    }

    /**
     * Checks whether the supplied credentials match this user's credentials.
     * Trims the supplied username and compares it without regard to case.
     * The password comparison is case-sensitive.
     *
     * @param userName the username to check
     * @param password the password to check
     * @return true if both credentials match; false if either is null or does not match
     */
    public boolean signIn(String userName, String password) {
        if (userName == null || password == null) {
            return false;
        }
        return this.userName.equalsIgnoreCase(userName.trim()) && this.password.equals(password);
    }

    public void resetPassword() {
        // Stub
    }

    /**
     * Updates this user's name and location.
     *
     * @param firstName the new first name
     * @param lastName the new last name
     * @param location the new location
     */
    public void editProfile(String firstName, String lastName, Location location) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
    }
    /**
     * Updates this user's safety status.
     *
     * @param category the new safety status
     */
    public void markSafe(SafetyCategory category) {
        this.safety = category;
    }
    /**
     * 
     * These are multiple placeholders, we are not sure if implementation is needed. But, have not done anything as of yet.
     */
    public void shareLocation() {
        // Stub
    }

    public void isGuest() {
        // Stub
    }

    public void Languages() {
        // Stub
    }
    
     /**
     * Returns the account type. Subclasses may override this method.
     *
     * @return "USER" for a standard user account
     */
    public String getType() {
        return "USER";
    }

    /**
     * Checks whether this user has a volunteer or professional role.
     *
     * @return true if this user is a volunteer or professional; false otherwise
     */
    public boolean isResponder() {
        return roles.contains(UserRole.VOLUNTEER) || roles.contains(UserRole.PROFESSIONAL);
    }


    /**
     * Creates and adds an emergency contact for this user.
     *
     * @param name the contact's name
     * @param phone the contact's phone number
     * @param relationship the contact's relationship to the user
     */
    public void addEmergencyContact(String name, String phone, String relationship) {
        emergencyContacts.add(new EmergencyContact(name, phone, relationship));
    }

      /**
     * Returns all that is needed
     *
     * @return the user's ID
     * @return the username
     * @return the first name
     * @return the last name
     * @return the password
     * @return the location
     * @return the safety status
     * @return the role list
     * @return the emergency contact list
     * 
     */
    public UUID getId() { return id; }
    public String getUserName() { return userName; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPassword() { return password; }
    public Location getLocation() { return location; }
    public SafetyCategory getSafety() { return safety; }
    public List<UserRole> getRoles() { return roles; }
    public List<EmergencyContact> getEmergencyContacts() { return emergencyContacts; }


    /**
     * Updates this user's username, removing leading and trailing whitespace.
     *
     * @param userName the new username
     * @throws IllegalArgumentException if the username is null or blank
     */
    public void setUserName(String userName) {
        if (userName == null || userName.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
        this.userName = userName.trim();
    }
    
    /**
     * Updates this user's first name.
     *
     * @param firstName the new first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Updates this user's last name.
     *
     * @param lastName the new last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
     /**
     * Updates this user's password.
     *
     * @param password the new password
     * @throws IllegalArgumentException if the password is null or empty
     */
    public void setPassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        this.password = password;
    }
    /**
     * Updates this user's location.
     *
     * @param location the new location
     */
    public void setLocation(Location location) {
        this.location = location;
    }
    /**
     * Updates this user's safety status.
     *
     * @param safety the new safety status
     */
    public void setSafety(SafetyCategory safety) {
        this.safety = safety;
    }
    /**
     * Replaces this user's roles with a copy of the supplied list.
     *
     * @param roles the new roles, or null to clear all roles
     */
    public void setRoles(List<UserRole> roles) {
        this.roles = (roles == null) ? new ArrayList<>() : new ArrayList<>(roles);
    }
    /**
     * Replaces this user's emergency contacts with a copy of the supplied list.
     *
     * @param emergencyContacts the new contacts, or null to clear all contacts
     */
    public void setEmergencyContacts(List<EmergencyContact> emergencyContacts) {
        this.emergencyContacts = (emergencyContacts == null)
                ? new ArrayList<>()
                : new ArrayList<>(emergencyContacts);
    }

    /**
     * Updates this user's contact phone number.
     *
     * @param contactPhone the new phone number
     */

    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    @Override
    public String toString() {
        return "User {" +
                "\n  ID: " + id +
                "\n  Username: '" + userName + '\'' +
                "\n  Name: '" + firstName + " " + lastName + '\'' +
                "\n  Safety: " + safety +
                "\n  Roles: " + roles +
                "\n  Location: " + (location != null ? location.toString() : "N/A") +
                "\n}";
    }

    public VolunteerRole getSpecialty() {
        return specialty;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setSpecialty(VolunteerRole specialty) {
        this.specialty = specialty;
    }
}
