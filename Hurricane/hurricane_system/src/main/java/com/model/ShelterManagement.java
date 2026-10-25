package com.model;

import java.util.ArrayList;
import java.util.UUID;
import com.location.Address;
import com.location.MapTools;
import java.io.IOException;



public class ShelterManagement {
    private static ShelterManagement shelterManagement;
    private ArrayList<Shelter> shelters;
    private MapTools mapTools = MapTools.getInstance();

    private ShelterManagement(){
        shelters = DataLoader.getShelters();
    }

    public static ShelterManagement getInstance(){
        if (shelterManagement == null) {
            shelterManagement = new ShelterManagement();
        }
        return shelterManagement;
    }

    public ArrayList<Shelter> getShelters(){
        return shelters;
    }


    public Shelter findNearestAvailable(Location location, int people, boolean needsPetFriendly, boolean needsMedical){

    
             ArrayList<Shelter> qualifying = new ArrayList<Shelter>();
        
            for (Shelter shelter : getAvailableShelters(people)) {
                if (needsPetFriendly && !shelter.isPetFriendly()) continue;
                if (needsMedical && !shelter.hasMedicalStaff()) continue;
                qualifying.add(shelter);
            }
            if (qualifying.isEmpty()) {
                return null;
            }
            if (qualifying.size() == 1) {
                return qualifying.get(0);
            }
        Address[] addresses = new Address[qualifying.size()];
        for (int i =0; i <qualifying.size(); i++){
            addresses[i] = qualifying.get(i).getLocation().toAddress();
        }

        try {
             Address closest = MapTools.getInstance()
                .findClosestAddress(location.toAddress(), addresses);

        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i] == closest) {
                return qualifying.get(i);
            }
        }

        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i].toString().equals(closest.toString())) {
                return qualifying.get(i);
            }
        }
    } catch (Exception e) {
        System.err.println("Could not reach the map service: " + e.getMessage());
    }

    return qualifying.get(0);
}
        
    public ArrayList<Shelter> getAvailableShelters(int people){
        ArrayList<Shelter> available = new ArrayList<>();

        for (Shelter s: shelters){
            int openSpots = (s.getCapacity() - s.getOccupancy());

            if (openSpots >= people) {
                available.add(s);
            }

        }
        return available;
    }
    
    public void addShelter(Shelter shelter){
        shelters.add(shelter);
    }

    public void removeShelter(UUID id){
        shelters.removeIf(shelter -> shelter.getId().equals(id));
    }
    public void save(){
        DataWriter.saveShelters(shelters);
    }
}
