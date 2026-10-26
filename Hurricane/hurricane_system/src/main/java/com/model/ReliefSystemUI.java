package com.model;

import java.util.ArrayList;

/**
 * Walks through the Hurricane Scarlett scenario end to end: the Carter
 * family requests help, Rosa Delgado is matched to the request and finds
 * them a shelter, her volunteer role is refused permission to close the
 * request, and Felix the shelter manager checks them in and completes it.
 *
 * @author Sahil
 */
public class ReliefSystemUI {

    private ReliefSystem system;

    private ReliefRequest carterRequest;
    private Shelter chosenShelter;

    public ReliefSystemUI() {
        system = ReliefSystem.getInstance();
    }

    /** Runs every part of the scenario in order. */
    public void run() {
        createTheRequest();
        matchRosaToTheRequest();
        findAShelterAndAssign();
        exportTheTrip();
        demonstrateRolePermissions();
        checkTheFamilyIn();
    }

    /**
     * Part 1: David Carter creates an account and submits a relief request
     * for his family and their dog.
     */
    public void createTheRequest() {
        System.out.println("\n===== PART 1: The Carter family requests help =====\n");

        Location home = new Location("155 Jackson Street", "", "Charleston",
        "SC", "29403", 32.8018, -79.9444);

        User david = system.registerUser("dcarter", "David", "Carter",
                "carter2026", home);

        if (david == null) {
            System.out.println("Could not create the account.");
            return;
        }
        System.out.println("David Carter created an account.");

        if (!system.signIn("dcarter", "carter2026")) {
            System.out.println("Could not sign in.");
            return;
        }
        System.out.println("David Carter is signed in.");

        system.markUserSafe(SafetyCategory.HIGH_RISK);
        System.out.println("Marked himself HIGH_RISK: no power, rising water.");

        ArrayList<Person> family = new ArrayList<>();
        family.add(new Person("David", "Carter", 41));
        family.add(new Person("Erin", "Carter", 39));

        Person sophie = new Person("Sophie", "Carter", 9);
        sophie.setNeedsMedicalAssistance(true);
        family.add(sophie);

        ArrayList<Pet> pets = new ArrayList<>();
        pets.add(new Pet("Buddy", "Dog"));

        carterRequest = system.submitReliefRequest(home,
                "Transportation - medical evacuation",
                family, pets, "843-555-0164");

        if (carterRequest == null) {
            System.out.println("The relief request was not submitted.");
            return;
        }

        System.out.println("\nRelief request submitted.");
        System.out.println("  People:  " + carterRequest.getPeopleCount());
        for (Person p : carterRequest.getRequestedPersons()) {
            System.out.println("    - " + p.getFullName() + ", age " + p.getAge()
                    + (p.needsMedicalAssistance() ? "  (needs medical assistance)" : ""));
        }
        for (Pet pet : carterRequest.getPets()) {
            System.out.println("    - " + pet.getName() + " (" + pet.getSpecies() + ")");
        }
        System.out.println("  Contact: " + carterRequest.getContactPhone());
        System.out.println("  Status:  " + system.getReliefStatus());

        system.addRequestComment(
                "Floodwater is around the house, we cannot drive out.");
        System.out.println("  Comment added.");

        system.signOut();
        System.out.println("\nDavid signed out. Request saved.");
    }

    /**
     * Part 2: Rosa signs in, sees the open requests, and the Carter request
     * is shown first with the reason it was recommended.
     */
    public void matchRosaToTheRequest() {
        System.out.println("\n===== PART 2: Rosa is matched to the request =====\n");

        if (!system.signIn("rosadelgado", "rosa2026")) {
            System.out.println("Rosa could not sign in.");
            return;
        }
        System.out.println("Rosa Delgado is signed in.");
        System.out.println("Specialty: " + system.getCurrentUser().getSpecialty());

        ArrayList<ReliefRequest> open = system.listRecommendedRequests();
        System.out.println("\nOpen relief requests (best match first): " + open.size() + "\n");
        
        for (ReliefRequest r : open) {
            String marker = r.getId().equals(carterRequest.getId())
                    ? "  <-- RECOMMENDED" : "";
            System.out.println("  " + r.getAssistance()
                    + " for " + r.getPeopleCount() + " people" + marker);
        }
        
        if (!open.isEmpty()) {
            System.out.println("\nWhy the top request is recommended:");
            System.out.println("  " + system.explainRecommendation(open.get(0)));
        }

        system.selectRequest(carterRequest.getId());
        System.out.println("\nRosa opened the request:");
        System.out.println("  People needing transport: "
                + carterRequest.getPeopleCount());
        for (Person p : carterRequest.getRequestedPersons()) {
            System.out.println("    - " + p.getFullName() + ", age " + p.getAge()
                    + (p.needsMedicalAssistance() ? "  (needs medical assistance)" : ""));
        }
        for (Pet pet : carterRequest.getPets()) {
            System.out.println("    - " + pet.getName()
                    + " (" + pet.getSpecies() + ")");
        }
    }

    /**
     * Part 3: Rosa searches the shelters for the closest one that takes dogs,
     * has medical staff and has room, then claims the request.
     */
    public void findAShelterAndAssign() {
        System.out.println("\n===== PART 3: Finding a shelter =====\n");

        System.out.println("Searching shelters that accept dogs, have medical");
        System.out.println("staff, and have room for "
                + carterRequest.getPeopleCount() + " people...\n");

        chosenShelter = system.findSafeShelter(
                carterRequest.getLocation(),
                carterRequest.getPeopleCount(),
                true,
                true);

        if (chosenShelter == null) {
            System.out.println("No shelter matched all three requirements.");
            return;
        }

        System.out.println("Closest matching shelter: " + chosenShelter.getName());
        System.out.println("  " + chosenShelter.getLocation().formatAddress());
        System.out.println("  Pet friendly:  " + chosenShelter.isPetFriendly());
        System.out.println("  Medical staff: " + chosenShelter.hasMedicalStaff());
        System.out.println("  Space left:    " + chosenShelter.getAvailableSpace());

        system.assignShelterToRequest(chosenShelter.getId());
        system.claimRequest();

        System.out.println("\nRosa claimed the request.");
        System.out.println("  Status is now " + system.getReliefStatus() + ".");
        System.out.println("  Other volunteers can see Rosa is handling it.");
    }

    /** Part 4: Rosa exports a printable trip sheet. */
    public void exportTheTrip() {
        System.out.println("\n===== PART 4: Exporting the trip sheet =====\n");

        if (chosenShelter == null) {
            System.out.println("No shelter was selected, nothing to export.");
            return;
        }

        system.exportTripInfo("carter-trip.txt");
    }

    /**
     * Part 5: Rosa tries to close the request and is refused, because
     * completing a request is not something a volunteer may do.
     */
    public void demonstrateRolePermissions() {
        System.out.println("\n===== PART 5: Role permissions =====\n");

        System.out.println("Rosa delivered the family and tries to mark the");
        System.out.println("request Complete...\n");

        try {
            system.completeRequest();
            System.out.println("Request completed.");
        } catch (IllegalStateException e) {
            System.out.println("  Refused: " + e.getMessage());
            System.out.println("  The request stays assigned to Rosa.");
            System.out.println("  Status is still " + system.getReliefStatus() + ".");
        }

        system.signOut();
        System.out.println("\nRosa signed out.");
    }

    /**
     * Part 6: Felix the shelter manager checks the family and their dog in,
     * then closes the request, which his role does allow.
     */
    public void checkTheFamilyIn() {
        System.out.println("\n===== PART 6: Felix checks the family in =====\n");

        if (!system.signIn("felixmanager", "felix2026")) {
            System.out.println("Felix could not sign in.");
            return;
        }
        System.out.println("Felix Martinez is signed in as shelter manager.");

        system.selectShelter(chosenShelter.getId());

        System.out.println("\nShelter details:");
        System.out.println("  Name:      " + chosenShelter.getName());
        System.out.println("  Location:  " + chosenShelter.getLocation().formatAddress());
        System.out.println("  Capacity:  " + chosenShelter.getCapacity());
        System.out.println("  Occupancy: " + chosenShelter.getOccupancy());
        System.out.println("  Available: " + chosenShelter.getAvailableSpace());
        System.out.println("  Occupants: " + chosenShelter.getOccupants().size());

        system.checkInFamily(carterRequest.getRequestedPersons(),
                carterRequest.getPets());

        System.out.println("\nChecked in the Carter family and Buddy.");
        System.out.println("  Occupancy is now " + chosenShelter.getOccupancy());
        System.out.println("  Occupant list:");
        for (Person p : chosenShelter.getOccupants()) {
            System.out.println("    - " + p.getFullName());
        }
        System.out.println("  Pets on site:");
        for (Pet pet : chosenShelter.getPetsOnSite()) {
            System.out.println("    - " + pet.getName()
                    + " (" + pet.getSpecies() + ")");
        }

        system.selectRequest(carterRequest.getId());
        system.completeRequest();

        System.out.println("\nFelix marked the request "
                + system.getReliefStatus() + ".");
        System.out.println("His shelter manager role allows this, Rosa's did not.");

        system.signOut();
        System.out.println("\nFelix signed out. Everything saved to JSON.");
    }

    public static void main(String[] args) {
        new ReliefSystemUI().run();
    }
}
