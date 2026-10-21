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

    private Address toAddress(Location loc) {
        return new Address(loc.getAddressLine1(), loc.getCity(),
                           loc.getState(), loc.getZipCode());
    }

    public Shelter findNearestAvailable(Location location, int numPeople)
            throws IOException, InterruptedException {

        ArrayList<Shelter> available = new ArrayList<>();
        for (Shelter s : shelters) {
            if (s.getAvailableSpace() >= numPeople) {
                available.add(s);
            }
        }
        if (available.isEmpty()) {
            return null;
        }

        Address[] addresses = new Address[available.size()];
        for (int i = 0; i < available.size(); i++) {
            addresses[i] = toAddress(available.get(i).getLocation());
        }

        Address closest = mapTools.findClosestAddress(toAddress(location), addresses);

        for (int i = 0; i < addresses.length; i++) {
            if (addresses[i] == closest) {
                return available.get(i);
            }
        }
        return null;
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

    public void removeShelter(String id){
        shelters.removeIf(shelter -> shelter.getId().equals(id));
    }
    public void save(){
        DataWriter.saveShelters(shelters);
    }
}
