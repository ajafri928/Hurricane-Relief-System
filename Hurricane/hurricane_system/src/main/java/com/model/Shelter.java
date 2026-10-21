package com.model;

import java.util.ArrayList;
import java.util.UUID;

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

    public Shelter(UUID id, String name, Location location, int capacity) { //Q: should operationalStatus be required?
        this.id = id;
        this.name = name;
        this.location = location;
        this.capacity = capacity;
        this.amenities = new ArrayList<String>();
        this.shelterResources = new ArrayList<ShelterResource>();
    }

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
}

    public void checkIn(String qrCode){
        occupancy++;
    }

    public void checkOut(String citienId){
        occupancy--;
    }
    public void addAmenity(String amenity) {
        amenities.add(amenity);
    }
    public void addResource(ShelterResource resource) {
        shelterResources.add(resource);
    }

    public ArrayList<ShelterResource> getLowStockResources(){
        ArrayList<ShelterResource> low = new ArrayList<>();
        for (ShelterResource r : shelterResources){
            if (r.checkStockLevel()){
                low.add(r);
            }
        }
        return low;
    }
    /*public void updateInventory(ShelterResource resource){ //Q: Confused on how this will work/if it should be moved to another class?
        resource = resource;
    }
    */

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public Location getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getOccupancy() { return occupancy; }
    public boolean isPetFriendly() { return petFriendly; }
    public boolean isAccessible() { return accessible; }
    public boolean hasMedicalStaff() { return medicalStaff; }
    public boolean hasVetStaff() { return vetStaff; }
    public String getOperationalStatus() { return operationalStatus; }
    public ArrayList<String> getAmenities() { return amenities; }
    public ArrayList<ShelterResource> getShelterResources() { return shelterResources; }
    // Setters
    public void setName(String name) { this.name = name; }
    public void setLocation(Location location) { this.location = location; }
    
    public void setCapacity(int capacity) {
        /*if (capacity < occupancy) { //Q: not sure if this part is needed
            throw new IllegalArgumentException("Capacity can't be below current occupancy");
        } */ 
        this.capacity = capacity;
    }
    
    public void setPetFriendly(boolean petFriendly) { this.petFriendly = petFriendly; }
    public void setAccessible(boolean accessible) { this.accessible = accessible; }
    public void setMedicalStaff(boolean medicalStaff) { this.medicalStaff = medicalStaff; }
    public void setVetStaff(boolean vetStaff) { this.vetStaff = vetStaff; }
    public void setOperationalStatus(String operationalStatus) { this.operationalStatus = operationalStatus; }

    @Override
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

