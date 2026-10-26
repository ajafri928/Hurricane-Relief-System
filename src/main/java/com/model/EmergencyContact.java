package com.model;

/**
 * An emergency contact saved on a user's profile.
 */
public class EmergencyContact {
    private String name;
    private String phone;
    private String relationship;

    /**
     * Makes a contact without a relationship.
     *
     * @param name the contact's name
     * @param phone the contact's phone number
     */
    public EmergencyContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    /**
     * Makes a contact with a relationship.
     *
     * @param name the contact's name
     * @param phone the contact's phone number
     * @param relationship how the contact is related to the user (like "Mother")
     */
    public EmergencyContact(String name, String phone, String relationship) {
        this.name = name;
        this.phone = phone;
        this.relationship = relationship;
    }

    /** @return the contact's name */
    public String getName() { return name; }

    /** @return the contact's phone number */
    public String getPhone() { return phone; }

    /** @return how the contact is related to the user */
    public String getRelationship() { return relationship; }

    /** @return the contact as "name (relationship) - phone" */
    @Override
    public String toString() {
        return name + " (" + relationship + ") - " + phone;
    }
}