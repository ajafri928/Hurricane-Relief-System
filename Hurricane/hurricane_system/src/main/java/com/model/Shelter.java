package com.model;

import java.util.ArrayList;
import java.util.UUID;

/**
 * A shelter where people (and their pets) can stay during a hurricane.
 * Tracks capacity, occupants, pets, amenities, and supplies.
 */
public class Shelter {
    private UUID id;
    private String name;
    private Location location;
    private int capacity;
    private int occupancy;
    private boolean petFriendly;
    private boolean accessible;
    private boolean medicalStaff;
    private boolean vetStaff;
    private String operationalStatus;
    private ArrayList<String> amenities;
    private ArrayList<ShelterResource> shelterResources;
    private ArrayList<Person> occupants;
    private ArrayList<Pet> petsOnSite;

    /**
     * Makes a new, empty shelter.
     *
     * @param id       the shelter's id
     * @param name     the shelter's name
     * @param location where the shelter is
     * @param capacity how many people it can hold
     */
    public Shelter(UUID id, String name, Location location, int capacity) { // Q: should operationalStatus be required?
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.amenities = new ArrayList<String>();
        this.shelterResources = new ArrayList<ShelterResource>();
        this.occupants = new ArrayList<Person>();
        this.petsOnSite = new ArrayList<Pet>();
    }

    /**
     * Rebuilds a saved shelter (used by DataLoader).
     *
     * @param id                the shelter's id
     * @param name              the shelter's name
     * @param location          where the shelter is
     * @param capacity          how many people it can hold
     * @param occupancy         how many people are there now
     * @param petFriendly       whether pets are allowed
     * @param accessible        whether it is wheelchair accessible
     * @param medicalStaff      whether medical staff are on site
     * @param vetStaff          whether a vet is on site
     * @param operationalStatus the status, like "OPEN"
     */
    public Shelter(UUID id, String name, Location location, int capacity, int occupancy,
            boolean petFriendly, boolean accessible, boolean medicalStaff,
            boolean vetStaff, String operationalStatus) {

        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.occupancy = occupancy;
        this.petFriendly = petFriendly;
        this.accessible = accessible;
        this.medicalStaff = medicalStaff;
        this.vetStaff = vetStaff;
        this.operationalStatus = operationalStatus;
        this.amenities = new ArrayList<String>();
        this.shelterResources = new ArrayList<ShelterResource>();
        this.occupants = new ArrayList<Person>();
        this.petsOnSite = new ArrayList<Pet>();
    }

    /**
     * Checks one person in by QR code and raises occupancy by one.
     *
     * @param qrCode the person's QR code
     */
    public void checkIn(String qrCode) {
        occupancy++;
    }

    /**
     * Checks one person out and lowers occupancy by one.
     *
     * @param citienId the person's id
     */
    public void checkOut(String citienId) {
        occupancy--;
    }

    /**
     * Checks a group of people and their pets into this shelter.
     * Adds each person to the occupant list, raises human occupancy
     * by the number of people, and records the pets separately.
     * Pets do not count toward human occupancy.
     *
     * @param persons the people checking in
     * @param pets    the pets coming with them (can be empty)
     * @throws IllegalStateException if there isn't room, or pets are brought
     *                               to a shelter that isn't pet friendly
     */
    public void checkIn(ArrayList<Person> persons, ArrayList<Pet> pets) {
        if (persons.size() > getAvailableSpace()) {
            throw new IllegalStateException(name + " doesn't have room for " + persons.size() + " people");
        }
        if (!pets.isEmpty() && !petFriendly) {
            throw new IllegalStateException(name + " is not pet friendly");
        }
        occupants.addAll(persons);
        occupancy += persons.size();
        petsOnSite.addAll(pets);
    }

    /**
     * Checks a group of people and their pets out of this shelter.
     *
     * @param persons the people leaving
     * @param pets    the pets leaving with them (can be empty)
     */
    public void checkOut(ArrayList<Person> persons, ArrayList<Pet> pets) {
        for (Person p : persons) {
            if (occupants.remove(p)) {
                occupancy--;
            }
        }
        petsOnSite.removeAll(pets);
    }

    /**
     * Adds an amenity, like "Showers".
     *
     * @param amenity the amenity to add
     */
    public void addAmenity(String amenity) {
        amenities.add(amenity);
    }

    /**
     * Adds a supply to the shelter's inventory.
     *
     * @param resource the resource to add
     */
    public void addResource(ShelterResource resource) {
        shelterResources.add(resource);
    }

    /**
     * Lists supplies at or below their low-stock threshold.
     *
     * @return the resources that are running low
     */
    public ArrayList<ShelterResource> getLowStockResources() {
        ArrayList<ShelterResource> low = new ArrayList<>();
        for (ShelterResource r : shelterResources) {
            if (r.checkStockLevel()) {
                low.add(r);
            }
        }
        return low;
    }
    /*
     * public void updateInventory(ShelterResource resource){ //Q: Confused on how
     * this will work/if it should be moved to another class?
     * resource = resource;
     * }
     */

    /** @return how many more people the shelter can hold */
    public int getAvailableSpace() {
        return capacity - occupancy;
    }

    /**
     * @return how many more people the shelter can hold (same as getAvailableSpace)
     */
    public int getAvailableSpaces() {
        return capacity - occupancy;
    }

    // Getters
    /** @return the shelter's id */
    public UUID getId() {
        return id;
    }

    /** @return the shelter's name */
    public String getName() {
        return name;
    }

    /** @return where the shelter is */
    public Location getLocation() {
        return location;
    }

    /** @return how many people the shelter can hold */
    public int getCapacity() {
        return capacity;
    }

    /** @return how many people are there now */
    public int getOccupancy() {
        return occupancy;
    }

    /** @return true if pets are allowed */
    public boolean isPetFriendly() {
        return petFriendly;
    }

    /** @return true if the shelter is wheelchair accessible */
    public boolean isAccessible() {
        return accessible;
    }

    /** @return true if medical staff are on site */
    public boolean hasMedicalStaff() {
        return medicalStaff;
    }

    /** @return true if a vet is on site */
    public boolean hasVetStaff() {
        return vetStaff;
    }

    /** @return the status, like "OPEN" */
    public String getOperationalStatus() {
        return operationalStatus;
    }

    /** @return the shelter's amenities */
    public ArrayList<String> getAmenities() {
        return amenities;
    }

    /** @return the shelter's supplies */
    public ArrayList<ShelterResource> getShelterResources() {
        return shelterResources;
    }

    /** @return the people checked in */
    public ArrayList<Person> getOccupants() {
        return occupants;
    }

    /** @return the pets checked in */
    public ArrayList<Pet> getPetsOnSite() {
        return petsOnSite;
    }

    // Setters
    /** @param name the new name */
    public void setName(String name) {
        this.name = name;
    }

    /** @param location the new location */
    public void setLocation(Location location) {
        this.location = location;
    }

    /** @param capacity the new capacity */
    public void setCapacity(int capacity) {
        /*
         * if (capacity < occupancy) { //Q: not sure if this part is needed
         * throw new
         * IllegalArgumentException("Capacity can't be below current occupancy");
         * }
         */
        this.capacity = capacity;
    }

    /** @param petFriendly whether pets are allowed */
    public void setPetFriendly(boolean petFriendly) {
        this.petFriendly = petFriendly;
    }

    /** @param accessible whether it is wheelchair accessible */
    public void setAccessible(boolean accessible) {
        this.accessible = accessible;
    }

    /** @param medicalStaff whether medical staff are on site */
    public void setMedicalStaff(boolean medicalStaff) {
        this.medicalStaff = medicalStaff;
    }

    /** @param vetStaff whether a vet is on site */
    public void setVetStaff(boolean vetStaff) {
        this.vetStaff = vetStaff;
    }

    /** @param operationalStatus the new status */
    public void setOperationalStatus(String operationalStatus) {
        this.operationalStatus = operationalStatus;
    }

    @Override
    /** @return a readable summary of the shelter */
    public String toString() {
        return "Shelter {" +
                "\n  ID: " + id +
                "\n  Name: '" + name + '\'' +
                "\n  Capacity: " + capacity +
                "\n  Occupancy: " + occupancy +
                "\n  Pet Friendly: " + petFriendly +
                "\n  Accessible: " + accessible +
                "\n  Medical Staff: " + medicalStaff +
                "\n  Vet Staff: " + vetStaff +
                "\n  Operational Status: '" + operationalStatus + '\'' +
                "\n  Location: " + (location != null ? location.toString() : "N/A") +
                "\n  Amenities: " + amenities +
                "\n  Resources: " + shelterResources +
                "\n}";
    }

}
