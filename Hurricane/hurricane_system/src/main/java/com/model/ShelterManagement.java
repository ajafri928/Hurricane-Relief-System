package com.model;

import java.util.ArrayList;
import java.util.UUID;
import com.location.Address;
import com.location.MapTools;
import java.io.IOException;

/**
 * Keeps track of every shelter and finds shelters for people who need one.
 * Uses the Singleton pattern, so there is only ever one ShelterManagement.
 */
public class ShelterManagement {
    private static ShelterManagement shelterManagement;
    private ArrayList<Shelter> shelters;
    private MapTools mapTools = MapTools.getInstance();

    /**
     * Private so the only instance comes from getInstance(). Loads shelters from
     * the JSON file.
     */
    private ShelterManagement() {
        shelters = DataLoader.getShelters();
    }

    /**
     * Returns the one shared ShelterManagement (Singleton).
     *
     * @return the ShelterManagement instance
     */
    public static ShelterManagement getInstance() {
        if (shelterManagement == null) {
            shelterManagement = new ShelterManagement();
        }
        return shelterManagement;
    }

    /** @return every shelter in the system */
    public ArrayList<Shelter> getShelters() {
        return shelters;
    }

    /**
     * Finds the closest shelter that has room and meets the group's needs.
     * If the map service can't be reached, returns the first shelter that
     * qualifies.
     *
     * @param location         where the group is
     * @param people           how many people need space
     * @param needsPetFriendly true if the group has pets
     * @param needsMedical     true if the group needs medical staff
     * @return the best shelter, or null if none qualify
     */
    public Shelter findNearestAvailable(Location location, int people, boolean needsPetFriendly, boolean needsMedical) {

        ArrayList<Shelter> qualifying = new ArrayList<Shelter>();

        for (Shelter shelter : getAvailableShelters(people)) {
            if (needsPetFriendly && !shelter.isPetFriendly())
                continue;
            if (needsMedical && !shelter.hasMedicalStaff())
                continue;
            qualifying.add(shelter);
        }
        if (qualifying.isEmpty()) {
            return null;
        }
        if (qualifying.size() == 1) {
            return qualifying.get(0);
        }
        Address[] addresses = new Address[qualifying.size()];
        for (int i = 0; i < qualifying.size(); i++) {
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

    /**
     * Lists every shelter with enough open spots for a group.
     *
     * @param people how many people need space
     * @return the shelters with room
     */
    public ArrayList<Shelter> getAvailableShelters(int people) {
        ArrayList<Shelter> available = new ArrayList<>();

        for (Shelter s : shelters) {
            int openSpots = (s.getCapacity() - s.getOccupancy());

            if (openSpots >= people) {
                available.add(s);
            }

        }
        return available;
    }

    /**
     * Adds a shelter to the system.
     *
     * @param shelter the shelter to add
     */
    public void addShelter(Shelter shelter) {
        shelters.add(shelter);
    }

    /**
     * Removes a shelter from the system.
     *
     * @param id the shelter's id
     */
    public void removeShelter(UUID id) {
        shelters.removeIf(shelter -> shelter.getId().equals(id));
    }

    /** Saves every shelter to the JSON file. */
    public void save() {
        DataWriter.saveShelters(shelters);
    }
}
