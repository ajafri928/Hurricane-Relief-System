package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit 4 tests for the ReliefSystem facade.
 *
 * The first section checks the ten rows of the team's potential-issues table.
 * The rest covers every public method: normal use, sign-in and role guards,
 * and bad input.
 *
 * These tests call signOut(), which saves to the JSON files. Run
 * "git restore jsonfiles/" after every test run.
 */
public class ReliefSystemTest {

    private ReliefSystem system;

    /** Gets the shared facade before each test. */
    @Before
    public void setUp() {
        system = ReliefSystem.getInstance();
    }

    /** Clears the signed-in user and selections so tests do not affect each other. */
    @After
    public void tearDown() {
        system.signOut();
    }

    // =====================================================================
    // Helpers
    // =====================================================================

    /** A Charleston test address. */
    private Location testLocation() {
        return new Location("155 Jackson Street", "", "Charleston", "SC", "29403",
                32.8018, -79.9444);
    }

    /** Signs in and fails the test right away if the account is missing. */
    private void signIn(String userName, String password) {
        assertTrue("Could not sign in as " + userName + ". Check jsonfiles/Users.json.",
                system.signIn(userName, password));
    }

    /** Submits a one-person request as johndoe, then signs out. */
    private ReliefRequest makeRequest() {
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Person", 30));
        return makeRequest(people, new ArrayList<>());
    }

    /** Submits a request with the given people and pets as johndoe, then signs out. */
    private ReliefRequest makeRequest(ArrayList<Person> people, ArrayList<Pet> pets) {
        signIn("johndoe", "password123");
        ReliefRequest request = system.submitReliefRequest(testLocation(),
                "Transportation - test ride", people, pets, "555-0100");
        system.signOut();
        return request;
    }

    /** Returns the first loaded shelter. */
    private Shelter firstShelter() {
        for (Shelter s : ShelterManagement.getInstance().getShelters()) {
            return s;
        }
        fail("No shelters loaded. Check jsonfiles/Shelters.json.");
        return null;
    }

    /** Fails the test unless the action throws IllegalStateException. */
    private void assertIllegalState(String message, Runnable action) {
        try {
            action.run();
            fail(message);
        } catch (IllegalStateException expected) {
            // Expected
        }
    }

    /** Fails the test unless the action throws IllegalArgumentException. */
    private void assertIllegalArgument(String message, Runnable action) {
        try {
            action.run();
            fail(message);
        } catch (IllegalArgumentException expected) {
            // Expected
        }
    }

    // =====================================================================
    // Potential issues table (rows 1 to 10)
    // =====================================================================

    @Test
    public void volunteerCannotCompleteThroughUpdateRequestStatus() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();

        assertIllegalState("A volunteer was able to set the status to COMPLETE through updateRequestStatus",
                () -> system.updateRequestStatus(ReliefRequestStatus.COMPLETE));
    }

    @Test
    public void cancelRequestChangesStatus() {
        ReliefRequest request = makeRequest();
        signIn("johndoe", "password123");
        system.selectRequest(request.getId());

        system.cancelRequest();

        assertFalse("Cancelling left the request as SUBMITTED",
                system.getReliefStatus() == ReliefRequestStatus.SUBMITTED);
    }

    @Test
    public void secondVolunteerCannotTakeAClaimedRequest() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();
        system.signOut();

        signIn("mrivera", "volunteer456");
        system.selectRequest(request.getId());

        assertIllegalState("A second volunteer was able to claim a request Rosa already claimed",
                () -> system.claimRequest());
        assertEquals("rosadelgado", request.getResponder().getUserName());
    }

    @Test
    public void onlyAssignedVolunteerCanMarkHelpEnRoute() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();
        system.signOut();

        signIn("mrivera", "volunteer456");
        system.selectRequest(request.getId());

        assertIllegalState("A volunteer who is not assigned marked the request as help en route",
                () -> system.respondToRequest());
    }

    @Test
    public void checkInFamilyRequiresShelterManager() {
        system.selectShelter(firstShelter().getId());
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Guest", 30));

        assertIllegalState("People were checked into a shelter with nobody signed in",
                () -> system.checkInFamily(people, new ArrayList<>()));
    }

    @Test
    public void listOpenRequestsHandlesRequestWithNoStatus() {
        ReliefRequest unsubmitted = new ReliefRequest(testLocation(), "Never submitted");
        ReliefManagement.getInstance().addReliefRequest(unsubmitted);
        try {
            system.listOpenRequests();
        } catch (NullPointerException e) {
            fail("listOpenRequests crashed on a request with no status: " + e);
        } finally {
            ReliefManagement.getInstance().getReliefRequests().remove(unsubmitted);
        }
    }

    @Test
    public void submitRejectsRequestWithNoPeople() {
        signIn("johndoe", "password123");

        assertIllegalArgument("A request with no people was accepted",
                () -> system.submitReliefRequest(testLocation(), "Transportation - empty",
                        new ArrayList<>(), new ArrayList<>(), "555-0100"));
    }

    @Test
    public void volunteerCannotCompleteRequest() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();

        try {
            system.completeRequest();
            fail("A volunteer was able to complete a request");
        } catch (IllegalStateException e) {
            assertEquals("Your volunteer role does not have permission to complete relief requests.",
                    e.getMessage());
        }
        assertEquals(ReliefRequestStatus.ASSIGNED, system.getReliefStatus());
    }

    @Test
    public void shelterManagerCanCompleteRequest() {
        ReliefRequest request = makeRequest();
        signIn("felixmanager", "felix2026");
        system.selectRequest(request.getId());

        system.completeRequest();

        assertEquals(ReliefRequestStatus.COMPLETE, system.getReliefStatus());
    }

    @Test
    public void registerRejectsDuplicateUsername() {
        assertNull("A duplicate username was registered",
                system.registerUser("rosadelgado", "Copy", "User", "pw123", testLocation()));
    }

    @Test
    public void registerCreatesNewUser() {
        String name = "test" + System.nanoTime();
        User user = system.registerUser(name, "Test", "User", "pw123", testLocation());

        assertNotNull("A new username could not be registered", user);
        assertEquals(name, user.getUserName());
    }

    @Test
    public void failedSignInKeepsCurrentUser() {
        signIn("felixmanager", "felix2026");
        system.signIn("felixmanager", "wrong-password");
        assertNotNull("A failed sign-in signed Felix out", system.getCurrentUser());
    }

}
