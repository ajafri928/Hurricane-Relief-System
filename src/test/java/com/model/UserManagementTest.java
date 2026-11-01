package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserManagementTest {

    private UserManagement userManagement;
    private Location columbia;
    private final PrintStream originalOut = System.out;

    @Before
    public void setUp() {
        TestDataFiles.backUpAll();
        TestDataFiles.write(DataConstants.USER_FILE_NAME, TestDataFiles.TWO_USERS_JSON);
        TestDataFiles.resetUserManagement();
        userManagement = UserManagement.getInstance();
        columbia = new Location("1400 Greene St", "Columbia", "SC", "29208");
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        TestDataFiles.resetUserManagement();
        TestDataFiles.restoreAll();
    }

    @Test
    public void getInstanceAlwaysReturnsTheSameObject() {
        assertSame(userManagement, UserManagement.getInstance());
    }

    @Test
    public void getInstanceLoadsUsersFromTheFile() {
        assertEquals(2, userManagement.getUsers().size());
    }

    @Test
    public void getUserByIdFindsMatchingUser() {
        User found = userManagement.getUser(UUID.fromString(TestDataFiles.JOHN_ID));
        assertEquals("johndoe", found.getUserName());
    }

    @Test
    public void getUserByIdReturnsNullForUnknownId() {
        assertNull(userManagement.getUser(UUID.randomUUID()));
    }

    @Test
    public void getUserByIdReturnsNullForNullId() {
        assertNull(userManagement.getUser((UUID) null));
    }

    @Test
    public void getUserByUserNameFindsExactMatch() {
        assertEquals("Maria", userManagement.getUserByUserName("mrivera").getFirstName());
    }

    @Test
    public void getUserByUserNameIgnoresCase() {
        assertNotNull(userManagement.getUserByUserName("MRIVERA"));
    }

    @Test
    public void getUserByUserNameTrimsSpaces() {
        assertNotNull(userManagement.getUserByUserName("  mrivera  "));
    }

    @Test
    public void getUserByUserNameReturnsNullForUnknownName() {
        assertNull(userManagement.getUserByUserName("nobody"));
    }

    @Test
    public void getUserByUserNameReturnsNullForNull() {
        assertNull(userManagement.getUserByUserName(null));
    }

    @Test
    public void loginWithCorrectCredentialsReturnsTheUser() {
        User found = userManagement.getUser("johndoe", "password123");
        assertEquals(UUID.fromString(TestDataFiles.JOHN_ID), found.getId());
    }

    @Test
    public void loginWithWrongPasswordReturnsNull() {
        assertNull(userManagement.getUser("johndoe", "wrongpass"));
    }

    @Test
    public void loginWithUnknownUsernameReturnsNull() {
        assertNull(userManagement.getUser("nobody", "password123"));
    }

    @Test
    public void loginWithNullPasswordReturnsNull() {
        assertNull(userManagement.getUser("johndoe", null));
    }

    @Test
    public void loginWithAnotherUsersPasswordReturnsNull() {
        assertNull(userManagement.getUser("johndoe", "volunteer456"));
    }

    @Test
    public void addUserWithValidInfoReturnsTrue() {
        assertTrue(userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", columbia));
    }

    @Test
    public void addUserAddsTheUserToTheList() {
        userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", columbia);
        assertEquals(3, userManagement.getUsers().size());
    }

    @Test
    public void addedUserCanLogIn() {
        userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", columbia);
        assertNotNull(userManagement.getUser("abaanj", "gamecocks1"));
    }

    @Test
    public void addUserTrimsSpacesFromUsername() {
        userManagement.addUser("  abaanj  ", "Abaan", "Jafri", "gamecocks1", columbia);
        assertEquals("abaanj", userManagement.getUsers().get(2).getUserName());
    }

    @Test
    public void addUserWithDuplicateUsernameReturnsFalse() {
        assertFalse(userManagement.addUser("johndoe", "Other", "Person", "pass9999", columbia));
    }

    @Test
    public void addUserWithDuplicateUsernameInDifferentCaseReturnsFalse() {
        assertFalse(userManagement.addUser("JohnDoe", "Other", "Person", "pass9999", columbia));
    }

    @Test
    public void addUserWithDuplicateUsernameDoesNotGrowTheList() {
        userManagement.addUser("johndoe", "Other", "Person", "pass9999", columbia);
        assertEquals(2, userManagement.getUsers().size());
    }

    @Test
    public void addUserWithNullUsernameReturnsFalse() {
        assertFalse(userManagement.addUser(null, "Abaan", "Jafri", "gamecocks1", columbia));
    }

    @Test
    public void addUserWithBlankUsernameReturnsFalse() {
        assertFalse(userManagement.addUser("   ", "Abaan", "Jafri", "gamecocks1", columbia));
    }

    @Test
    public void addUserWithNullPasswordReturnsFalse() {
        assertFalse(userManagement.addUser("abaanj", "Abaan", "Jafri", null, columbia));
    }

    @Test
    public void addUserWithBlankPasswordReturnsFalse() {
        assertFalse(userManagement.addUser("abaanj", "Abaan", "Jafri", "  ", columbia));
    }

    @Test
    public void addUserWithBlankFirstNameReturnsFalse() {
        // Responders need a name to know who they're helping.
        assertFalse(userManagement.addUser("abaanj", "", "Jafri", "gamecocks1", columbia));
    }

    @Test
    public void addUserWithNullLocationReturnsFalse() {
        // Requirements: location sharing is mandatory during account creation.
        assertFalse(userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", null));
    }

    @Test
    public void removeUserDeletesThatUser() {
        userManagement.removeUser(UUID.fromString(TestDataFiles.JOHN_ID));
        assertNull(userManagement.getUserByUserName("johndoe"));
    }

    @Test
    public void removeUserLeavesOtherUsersAlone() {
        userManagement.removeUser(UUID.fromString(TestDataFiles.JOHN_ID));
        assertNotNull(userManagement.getUserByUserName("mrivera"));
    }

    @Test
    public void removeUserWithUnknownIdRemovesNobody() {
        userManagement.removeUser(UUID.randomUUID());
        assertEquals(2, userManagement.getUsers().size());
    }

    @Test
    public void removeUserWithNullIdRemovesNobody() {
        userManagement.removeUser(null);
        assertEquals(2, userManagement.getUsers().size());
    }

    @Test
    public void editUserChangesThatUsersFirstName() {
        userManagement.editUser(UUID.fromString(TestDataFiles.JOHN_ID), "Johnny", "Doe", columbia);
        assertEquals("Johnny", userManagement.getUserByUserName("johndoe").getFirstName());
    }

    @Test
    public void editUserChangesThatUsersLocation() {
        userManagement.editUser(UUID.fromString(TestDataFiles.JOHN_ID), "John", "Doe", columbia);
        assertSame(columbia, userManagement.getUserByUserName("johndoe").getLocation());
    }

    @Test
    public void editUserWithUnknownIdChangesNobody() {
        userManagement.editUser(UUID.randomUUID(), "Changed", "Name", columbia);
        assertEquals("John", userManagement.getUserByUserName("johndoe").getFirstName());
    }

    private String captureNotifications(ReliefRequest request) {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.setOut(new PrintStream(output));
        userManagement.notifyResponders(request);
        System.setOut(originalOut);
        return output.toString();
    }

    @Test
    public void notifyRespondersNotifiesVolunteers() {
        ReliefRequest request = new ReliefRequest(columbia, "Water");
        assertTrue(captureNotifications(request).contains("Notifying mrivera"));
    }

    @Test
    public void notifyRespondersSkipsUsersWhoAreNotResponders() {
        userManagement.getUserByUserName("johndoe").setRoles(java.util.Arrays.asList(UserRole.VICTIM));
        ReliefRequest request = new ReliefRequest(columbia, "Water");
        assertFalse(captureNotifications(request).contains("johndoe"));
    }

    @Test
    public void notifyRespondersIncludesTheRequestId() {
        ReliefRequest request = new ReliefRequest(columbia, "Water");
        assertTrue(captureNotifications(request).contains(request.getId().toString()));
    }

    @Test
    public void notifyRespondersWithNullRequestDoesNotCrash() {
        // Should ignore a null request instead of throwing NullPointerException.
        captureNotifications(null);
    }

    @Test
    public void saveUsersWritesNewUserToTheFile() {
        userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", columbia);
        userManagement.saveUsers();
        assertTrue(TestDataFiles.read(DataConstants.USER_FILE_NAME).contains("\"abaanj\""));
    }

    @Test
    public void savedUserIsStillThereAfterReloading() {
        userManagement.addUser("abaanj", "Abaan", "Jafri", "gamecocks1", columbia);
        userManagement.saveUsers();
        TestDataFiles.resetUserManagement();
        assertNotNull(UserManagement.getInstance().getUser("abaanj", "gamecocks1"));
    }
}