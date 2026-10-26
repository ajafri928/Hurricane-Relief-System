package com.model;

/**
 * Runs the create account and login scenario from start to finish.
 * Only talks to the facade, the same way the real UI will.
 * @author Abaan Jafri
 */
public class LoginSignUpDriver {
    private ReliefSystem facade;

    public LoginSignUpDriver() {
        facade = ReliefSystem.getInstance();
    }

    // Runs each step of the scenario in order.
    public void run() {
        signUp();
        signUpDuplicate();
        signUpBlank();
        loginWrongPassword();
        loginUnknownUser();
        loginSuccess();
        signOut();
    }

    // Step 1: a new user creates an account.
    private void signUp() {
        System.out.println("--- Step 1: Create account ---");
        Location home = new Location("1523 Greene St", "Columbia", "SC", "29208");
        User user = facade.registerUser("asmith", "Amy", "Smith", "storm2026", home);

        if (user == null) {
            System.out.println("Sorry, that account could not be created.");
            return;
        }
        System.out.println("Account created for " + user.getFirstName() + " " + user.getLastName()
                + " (username: " + user.getUserName() + ")");
    }

    // Step 2: someone tries to take a username that already exists.
    private void signUpDuplicate() {
        System.out.println("\n--- Step 2: Create account with a taken username ---");
        Location home = new Location("800 Main St", "Columbia", "SC", "29201");
        User user = facade.registerUser("ASmith", "Adam", "Smith", "password1", home);

        if (user == null) {
            System.out.println("Username \"ASmith\" is already taken.");
        } else {
            System.out.println("ERROR: a duplicate account was created.");
        }
    }

    // Step 3: someone leaves the password empty.
    private void signUpBlank() {
        System.out.println("\n--- Step 3: Create account with no password ---");
        Location home = new Location("800 Main St", "Columbia", "SC", "29201");
        User user = facade.registerUser("bjones", "Bob", "Jones", "", home);

        if (user == null) {
            System.out.println("Username and password are required.");
        } else {
            System.out.println("ERROR: an account with no password was created.");
        }
    }

    // Step 4: the user types the wrong password.
    private void loginWrongPassword() {
        System.out.println("\n--- Step 4: Login with the wrong password ---");
        if (facade.signIn("asmith", "wrongpassword")) {
            System.out.println("ERROR: logged in with the wrong password.");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Step 5: someone logs in with a username that does not exist.
    private void loginUnknownUser() {
        System.out.println("\n--- Step 5: Login with an unknown username ---");
        if (facade.signIn("nobody", "storm2026")) {
            System.out.println("ERROR: logged in as a user that does not exist.");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    // Step 6: the user logs in correctly.
    private void loginSuccess() {
        System.out.println("\n--- Step 6: Login ---");
        if (!facade.signIn("asmith", "storm2026")) {
            System.out.println("ERROR: could not log in with the right password.");
            return;
        }
        User user = facade.getCurrentUser();
        System.out.println("Welcome back, " + user.getFirstName() + " " + user.getLastName() + "!");
    }

    // Step 7: the user logs out.
    private void signOut() {
        System.out.println("\n--- Step 7: Sign out ---");
        facade.signOut();
        if (facade.getCurrentUser() == null) {
            System.out.println("You have been signed out.");
        } else {
            System.out.println("ERROR: still signed in.");
        }
    }

    public static void main(String[] args) {
        LoginSignUpDriver driver = new LoginSignUpDriver();
        driver.run();
    }
}
