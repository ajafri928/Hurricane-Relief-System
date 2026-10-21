package com.model;

public class EmergencyContact {
    private String name;
    private String phone;
    private String relationship;

    public EmergencyContact(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public EmergencyContact(String name, String phone, String relationship) {
        this.name = name;
        this.phone = phone;
        this.relationship = relationship;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getRelationship() { return relationship; }

    @Override
    public String toString() {
        return name + " (" + relationship + ") - " + phone;
    }
}
