package com.model;

import java.util.ArrayList;

/**
 * Walks through two user scenarios to demonstrate the full system:
 * a victim requesting help, and a volunteer responding to it.
 *
 * @author Sahil
 */
public class ReliefSystemUI {

    private ReliefSystem system;

    public ReliefSystemUI() {
        system = ReliefSystem.getInstance();
    }

    public void run() {
        scenario1();
        scenario2();
    }

    /**
     * Scenario 1: A victim signs in, submits a relief request,
     * finds the nearest shelter, and is assigned to it.
     */
    public void scenario1() {
        System.out.println("\n========== SCENARIO 1: Victim requests help ==========\n");

        if (!system.signIn("johndoe", "password123")) {
            System.out.println("Sorry, we couldn't log in.");
            return;
        }
        System.out.println("John Doe is now logged in.");

        Location johnsLocation = system.getCurrentUser().getLocation();
        System.out.println("Location on file: " + johnsLocation.formatAddress());

        system.markUserSafe(SafetyCategory.HIGH_RISK);
        System.out.println("Safety status set to HIGH_RISK.");

        ArrayList<Person> persons = new ArrayList<>();
        persons.add(new Person("John", "Doe", 40));
        persons.add(new Person("Jane", "Doe", 38));
        persons.add(new Person("Emma", "Doe", 6));
        persons.add(new Person("Liam", "Doe", 4));

        submitReliefRequest(Location location, String assistance,
            ArrayList<Person> persons, ArrayList<Pet> pets, String contactPhone);

        if (request == null) {
            System.out.println("The relief request was not submitted.");
            return;
        }
        System.out.println("Relief request submitted.");
        System.out.println("  Request ID: " + request.getId());
        System.out.println("  Status:     " + system.getReliefStatus());

        system.addRequestComment("We have two small children and no power.");
        System.out.println("Comment added to the request.");

        Shelter shelter = system.findSafeShelter(johnsLocation, request.getPeopleCount(), false, false);

        if (shelter == null) {
            System.out.println("No shelter with room was found.");
        } else {
            System.out.println("Nearest available shelter: " + shelter.getName());
            System.out.println("  Address:   " + shelter.getLocation().formatAddress());
            System.out.println("  Occupancy: " + shelter.getOccupancy()
                    + " / " + shelter.getCapacity());

            system.assignShelterToRequest(shelter.getId());
            System.out.println("Shelter assigned to the request.");
        }

        system.signOut();
        System.out.println("John Doe signed out. Data saved.");
    }

    /**
     * Scenario 2: A volunteer signs in, browses open requests,
     * claims one, and marks themselves en route.
     */
    public void scenario2() {
        System.out.println("\n========== SCENARIO 2: Volunteer responds ==========\n");

        if (!system.signIn("mrivera", "volunteer456")) {
            System.out.println("Sorry, we couldn't log in.");
            return;
        }
        System.out.println("Maria Rivera is now logged in.");

        ArrayList<ReliefRequest> openRequests = system.listOpenRequests();

        if (openRequests == null || openRequests.isEmpty()) {
            System.out.println("There are no open requests right now.");
            system.signOut();
            return;
        }

        System.out.println("Open requests: " + openRequests.size());
        for (ReliefRequest open : openRequests) {
            System.out.println("  - " + open.getAssistance()
                    + " for " + open.getPeopleCount() + " people"
                    + " (" + open.getStatus() + ")");
        }

        ReliefRequest chosen = openRequests.get(0);
        system.selectRequest(chosen.getId());
        System.out.println("Selected request: " + chosen.getId());

        system.claimRequest();
        System.out.println("Request claimed. Status is now " + system.getReliefStatus() + ".");

        system.addRequestComment("On my way with supplies, ETA 30 minutes.");
        System.out.println("Comment added for the family.");

        system.respondToRequest();
        System.out.println("Status updated to " + system.getReliefStatus() + ".");

        system.signOut();
        System.out.println("Maria Rivera signed out. Data saved.");
    }

    public static void main(String[] args) {
        new ReliefSystemUI().run();
    }
}