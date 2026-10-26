package com.model;

public class PersistenceCheck {

    public static void main(String[] args) {
        System.out.println("\n===== PART 7: After a restart =====\n");
        System.out.println("This is a new run. Everything below was loaded from the JSON files.\n");

        ReliefSystem system = ReliefSystem.getInstance();

        if (!system.signIn("felixmanager", "felix2026")) {
            System.out.println("Felix could not sign in.");
            return;
        }
        System.out.println(system.getCurrentUser().getFirstName()
                + " signed in, so the accounts were saved.");

        ReliefRequest carter = findCarterRequest();
        if (carter == null) {
            System.out.println("\nNo Carter request found. Run ReliefSystemUI first.");
            return;
        }

        System.out.println("\nCarter request:");
        System.out.println("  Status:       " + carter.getStatus());

        User responder = carter.getResponder();
        System.out.println("  Handled by:   " + (responder == null ? "nobody"
                : responder.getFirstName() + " " + responder.getLastName()));

        User submitter = carter.getSubmittedBy();
        System.out.println("  Submitted by: " + (submitter == null ? "not recorded"
                : submitter.getFirstName() + " " + submitter.getLastName()));
        
        printFamily(carter);
        printOccupiedShelters();
    }

    private static ReliefRequest findCarterRequest() {
        for (ReliefRequest r : ReliefManagement.getInstance().getReliefRequests()) {
            for (Person p : r.getRequestedPersons()) {
                if ("Carter".equals(p.getLastName())) {
                    return r;
                }
            }
        }
        return null;
    }
    
    private static void printFamily(ReliefRequest carter) {
        System.out.println("  Contact:      " + carter.getContactPhone());
        for (Person p : carter.getRequestedPersons()) {
            System.out.println("    - " + p.getFullName() + ", age " + p.getAge());
        }
        for (Pet pet : carter.getPets()) {
            System.out.println("    - " + pet.getName() + " (" + pet.getSpecies() + ")");
        }
    }
    private static void printOccupiedShelters() {
        for (Shelter s : ShelterManagement.getInstance().getShelters()) {
            if (!s.getOccupants().isEmpty() || !s.getPetsOnSite().isEmpty()) {
                System.out.println("\n" + s.getName() + ":");
                System.out.println("  Occupancy: " + s.getOccupancy()
                        + " of " + s.getCapacity());
                for (Person p : s.getOccupants()) {
                    System.out.println("    - " + p.getFullName());
                }
                for (Pet pet : s.getPetsOnSite()) {
                    System.out.println("    - " + pet.getName()
                            + " (" + pet.getSpecies() + ")");
                }
            }
        }
    }
}
