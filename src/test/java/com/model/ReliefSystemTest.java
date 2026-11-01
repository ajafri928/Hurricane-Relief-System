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

    // =====================================================================
    // Accounts: registerUser, signIn, signOut, getCurrentUser
    // =====================================================================

    @Test
    public void signInWithCorrectPasswordSucceeds() {
        assertTrue(system.signIn("rosadelgado", "rosa2026"));
        assertEquals("rosadelgado", system.getCurrentUser().getUserName());
    }

    @Test
    public void signInWithWrongPasswordFails() {
        assertFalse(system.signIn("rosadelgado", "wrong-password"));
    }

    @Test
    public void signInWithUnknownUserFails() {
        assertFalse(system.signIn("nobody" + System.nanoTime(), "pw123"));
    }

    @Test
    public void signOutClearsUserAndSelection() {
        ReliefRequest request = makeRequest();
        signIn("johndoe", "password123");
        system.selectRequest(request.getId());

        system.signOut();

        assertNull(system.getCurrentUser());
        assertIllegalState("A request was still selected after signing out",
                () -> system.getReliefStatus());
    }

    @Test
    public void registerRejectsBlankPassword() {
        assertNull("An account was created with a blank password",
                system.registerUser("test" + System.nanoTime(), "Test", "User", "", testLocation()));
    }

    @Test
    public void registerRejectsBlankUsername() {
        assertNull("An account was created with a blank username",
                system.registerUser("", "Test", "User", "pw123", testLocation()));
    }

    @Test
    public void registeredUserCanSignIn() {
        String name = "test" + System.nanoTime();
        system.registerUser(name, "Test", "User", "pw123", testLocation());
        assertTrue(system.signIn(name, "pw123"));
    }

    // =====================================================================
    // Profile: addEmergencyContact, markUserSafe, shareUserLocation
    // =====================================================================

    @Test
    public void addEmergencyContactRequiresSignIn() {
        assertIllegalState("An emergency contact was added with nobody signed in",
                () -> system.addEmergencyContact("Pat", "555-0199", "Friend"));
    }

    @Test
    public void addEmergencyContactAddsContact() {
        signIn("johndoe", "password123");
        int before = system.getCurrentUser().getEmergencyContacts().size();
        system.addEmergencyContact("Pat", "555-0199", "Friend");
        assertEquals(before + 1, system.getCurrentUser().getEmergencyContacts().size());
    }

    @Test
    public void markUserSafeRequiresSignIn() {
        assertIllegalState("A safety status was set with nobody signed in",
                () -> system.markUserSafe(SafetyCategory.HIGH_RISK));
    }

    @Test
    public void markUserSafeSetsSafety() {
        signIn("johndoe", "password123");
        system.markUserSafe(SafetyCategory.HIGH_RISK);
        assertEquals(SafetyCategory.HIGH_RISK, system.getCurrentUser().getSafety());
    }

    @Test
    public void shareUserLocationRequiresSignIn() {
        assertIllegalState("A location was shared with nobody signed in",
                () -> system.shareUserLocation());
    }

    // =====================================================================
    // Requests: submit, select, status, claim, respond, escalate, comments
    // =====================================================================

    @Test
    public void submitStoresPeoplePetsAndContact() {
        signIn("johndoe", "password123");
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Adult", 40));
        people.add(new Person("Test", "Child", 9));
        ArrayList<Pet> pets = new ArrayList<>();
        pets.add(new Pet("Rex", "Dog"));

        ReliefRequest request = system.submitReliefRequest(testLocation(),
                "Transportation - test ride", people, pets, "555-0100");

        assertEquals(2, request.getPeopleCount());
        assertEquals(1, request.getPets().size());
        assertEquals("555-0100", request.getContactPhone());
        assertEquals(ReliefRequestStatus.SUBMITTED, system.getReliefStatus());
        assertEquals("johndoe", request.getSubmittedBy().getUserName());
    }

    @Test
    public void submitWithMedicalNeedIsUrgent() {
        ArrayList<Person> people = new ArrayList<>();
        Person child = new Person("Test", "Child", 9);
        child.setNeedsMedicalAssistance(true);
        people.add(child);

        ReliefRequest request = makeRequest(people, new ArrayList<>());

        assertEquals(UrgencyTier.URGENT, request.getUrgency());
        assertTrue(request.hasSpecialNeeds());
    }

    @Test
    public void submitWithoutMedicalNeedIsStandard() {
        ReliefRequest request = makeRequest();
        assertEquals(UrgencyTier.STANDARD, request.getUrgency());
        assertFalse(request.hasSpecialNeeds());
    }

    @Test
    public void submitRequiresSignIn() {
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Person", 30));

        assertIllegalState("A request was submitted with nobody signed in",
                () -> system.submitReliefRequest(testLocation(), "Transportation - test",
                        people, new ArrayList<>(), "555-0100"));
    }

    @Test
    public void selectRequestWithUnknownIdThrows() {
        assertIllegalArgument("An unknown request id was accepted",
                () -> system.selectRequest(UUID.randomUUID()));
    }

    @Test
    public void getReliefStatusWithNoSelectionThrows() {
        assertIllegalState("A status was returned with no request selected",
                () -> system.getReliefStatus());
    }

    @Test
    public void claimRequestAssignsVolunteer() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());

        system.claimRequest();

        assertEquals(ReliefRequestStatus.ASSIGNED, system.getReliefStatus());
        assertEquals("rosadelgado", request.getResponder().getUserName());
    }

    @Test
    public void claimRequestRequiresResponder() {
        ReliefRequest request = makeRequest();
        signIn("systemadmin", "admin2026");
        system.selectRequest(request.getId());

        assertIllegalState("A non-responder was able to claim a request",
                () -> system.claimRequest());
    }

    @Test
    public void respondToRequestMarksHelpEnRoute() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();

        system.respondToRequest();

        assertEquals(ReliefRequestStatus.HELP_EN_ROUTE, system.getReliefStatus());
    }

    @Test
    public void escalateRequestEscalates() {
        ReliefRequest request = makeRequest();
        signIn("johndoe", "password123");
        system.selectRequest(request.getId());

        system.escalateRequest();

        assertEquals(ReliefRequestStatus.ESCALATED_TO_911, system.getReliefStatus());
    }

    @Test
    public void addRequestCommentAddsComment() {
        ReliefRequest request = makeRequest();
        signIn("johndoe", "password123");
        system.selectRequest(request.getId());
        int before = system.getRequestComments().size();

        system.addRequestComment("Water is rising");

        ArrayList<RequestComment> comments = system.getRequestComments();
        assertEquals(before + 1, comments.size());
        assertEquals("Water is rising", comments.get(comments.size() - 1).getText());
    }

    @Test
    public void addRequestCommentRequiresSignIn() {
        ReliefRequest request = makeRequest();
        system.selectRequest(request.getId());

        assertIllegalState("A comment was added with nobody signed in",
                () -> system.addRequestComment("Anonymous note"));
    }

    @Test
    public void listOpenRequestsIncludesNewRequest() {
        ReliefRequest request = makeRequest();
        assertTrue(system.listOpenRequests().contains(request));
    }

    @Test
    public void listOpenRequestsExcludesClaimedRequest() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        system.claimRequest();

        assertFalse(system.listOpenRequests().contains(request));
    }

    @Test
    public void listRecommendedRequestsRequiresResponder() {
        signIn("systemadmin", "admin2026");
        assertIllegalState("A non-responder got recommended requests",
                () -> system.listRecommendedRequests());
    }

    @Test
    public void explainRecommendationRequiresSignIn() {
        ReliefRequest request = makeRequest();
        assertIllegalState("A recommendation was explained with nobody signed in",
                () -> system.explainRecommendation(request));
    }

    // =====================================================================
    // Shelters: find, select, assign, check in, inventory, export
    // =====================================================================

    @Test
    public void findSafeShelterFindsPetFriendlyMedicalShelter() {
        Shelter shelter = system.findSafeShelter(testLocation(), 3, true, true);
        assertNotNull("No shelter was found", shelter);
        assertTrue(shelter.isPetFriendly());
        assertTrue(shelter.hasMedicalStaff());
        assertTrue(shelter.getAvailableSpace() >= 3);
    }

    @Test
    public void selectShelterWithUnknownIdThrows() {
        assertIllegalArgument("An unknown shelter id was accepted",
                () -> system.selectShelter(UUID.randomUUID()));
    }

    @Test
    public void assignShelterToRequestSetsDestination() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());
        Shelter shelter = firstShelter();

        system.assignShelterToRequest(shelter.getId());

        assertSame(shelter, request.getDestinationShelter());
    }

    @Test
    public void checkInFamilyAddsOccupantsAndPets() {
        signIn("felixmanager", "felix2026");
        Shelter shelter = firstShelter();
        system.selectShelter(shelter.getId());
        int occupancyBefore = shelter.getOccupancy();
        int occupantsBefore = shelter.getOccupants().size();
        int petsBefore = shelter.getPetsOnSite().size();
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Adult", 40));
        people.add(new Person("Test", "Child", 9));
        ArrayList<Pet> pets = new ArrayList<>();
        pets.add(new Pet("Rex", "Dog"));

        system.checkInFamily(people, pets);

        assertEquals(occupancyBefore + 2, shelter.getOccupancy());
        assertEquals(occupantsBefore + 2, shelter.getOccupants().size());
        assertEquals(petsBefore + 1, shelter.getPetsOnSite().size());
    }

    @Test
    public void checkInFamilyWithNoShelterSelectedThrows() {
        signIn("felixmanager", "felix2026");
        ArrayList<Person> people = new ArrayList<>();
        people.add(new Person("Test", "Guest", 30));

        assertIllegalState("A family was checked in with no shelter selected",
                () -> system.checkInFamily(people, new ArrayList<>()));
    }

    @Test
    public void updateShelterInventoryReplacesSameType() {
        Shelter shelter = firstShelter();
        system.selectShelter(shelter.getId());
        ArrayList<ShelterResource> resources = shelter.getShelterResources();
        assertFalse("The first shelter has no resources to test with", resources.isEmpty());
        int before = resources.size();

        system.updateShelterInventory(resources.get(0));

        assertEquals(before, shelter.getShelterResources().size());
    }

    @Test
    public void getLowStockResourcesRequiresSelectedShelter() {
        assertIllegalState("Low-stock resources were returned with no shelter selected",
                () -> system.getLowStockResources());
    }

    @Test
    public void exportTripInfoRequiresSelectedShelter() {
        ReliefRequest request = makeRequest();
        signIn("rosadelgado", "rosa2026");
        system.selectRequest(request.getId());

        assertIllegalState("A trip sheet was exported with no shelter selected",
                () -> system.exportTripInfo("test-trip.txt"));
    }

    

}
