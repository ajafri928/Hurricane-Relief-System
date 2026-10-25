package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class HazardManagement {
    private static HazardManagement hazardManagement;
    private ArrayList<HazardReport> hazards;

    private HazardManagement() {
        hazards = new ArrayList<>();
    }

    public static HazardManagement getInstance() {
        if (hazardManagement == null) {
            hazardManagement = new HazardManagement();
        }
        return hazardManagement;
    }

    public ArrayList<HazardReport> getHazards() {
        return hazards;
    }

    // Returns null if no hazard has this id.
    public HazardReport getHazard(UUID id) {
        for (HazardReport hazard : hazards) {
            if (hazard.getId().equals(id)) {
                return hazard;
            }
        }
        return null;
    }

    public ArrayList<HazardReport> getVerifiedHazards() {
        ArrayList<HazardReport> verified = new ArrayList<>();
        for (HazardReport hazard : hazards) {
            if (hazard.isVerified()) {
                verified.add(hazard);
            }
        }
        return verified;
    }

    public void addHazard(HazardReport hazard) {
        hazards.add(hazard);
    }

    public void removeHazard(UUID id) {
        hazards.removeIf(hazard -> hazard.getId().equals(id));
    }
}
