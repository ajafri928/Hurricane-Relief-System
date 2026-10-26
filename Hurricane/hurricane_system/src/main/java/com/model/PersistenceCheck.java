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
    }
}
