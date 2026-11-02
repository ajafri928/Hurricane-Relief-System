package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class UserTest {

    private Location columbia;
    private User user;

    @Before
    public void setUp() {
        columbia = new Location("123 Main St", "Columbia", "SC", "29201");
        user = new User("johndoe", "John", "Doe", "password123", columbia);
    }

    @Test
    public void newUserGetsANonNullId() {
        assertTrue(user.getId() != null);
    }

    @Test
    public void twoNewUsersGetDifferentIds() {
        User other = new User("mrivera", "Maria", "Rivera", "volunteer456", columbia);
        assertNotEquals(user.getId(), other.getId());
    }

    @Test
    public void newUserStartsWithNoRoles() {
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void newUserStartsWithNoEmergencyContacts() {
        assertTrue(user.getEmergencyContacts().isEmpty());
    }

    @Test
    public void loadConstructorKeepsTheSavedId() {
        UUID savedId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        User loaded = new User(savedId, "johndoe", "John", "Doe", "password123",
                columbia, SafetyCategory.SAFE, new ArrayList<>());
        assertEquals(savedId, loaded.getId());
    }

    @Test
    public void loadConstructorWithNullRolesGivesEmptyRoleList() {
        User loaded = new User(UUID.randomUUID(), "johndoe", "John", "Doe", "password123",
                columbia, SafetyCategory.SAFE, null);
        assertTrue(loaded.getRoles().isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsBlankUsername() {
        // setUserName() rejects blank usernames, so the constructor should too.
        new User("   ", "John", "Doe", "password123", columbia);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorRejectsNullPassword() {
        // setPassword() rejects null passwords, so the constructor should too.
        new User("johndoe", "John", "Doe", null, columbia);
    }

    @Test
    public void signInWithCorrectUsernameAndPasswordReturnsTrue() {
        assertTrue(user.signIn("johndoe", "password123"));
    }

    @Test
    public void signInWithWrongPasswordReturnsFalse() {
        assertFalse(user.signIn("johndoe", "wrongpass"));
    }

    @Test
    public void signInWithWrongUsernameReturnsFalse() {
        assertFalse(user.signIn("janedoe", "password123"));
    }

    @Test
    public void signInIgnoresUsernameCase() {
        assertTrue(user.signIn("JohnDoe", "password123"));
    }

    @Test
    public void signInTrimsSpacesAroundUsername() {
        assertTrue(user.signIn("  johndoe  ", "password123"));
    }

    @Test
    public void signInPasswordIsCaseSensitive() {
        assertFalse(user.signIn("johndoe", "PASSWORD123"));
    }

    @Test
    public void signInWithNullUsernameReturnsFalse() {
        assertFalse(user.signIn(null, "password123"));
    }

    @Test
    public void signInWithNullPasswordReturnsFalse() {
        assertFalse(user.signIn("johndoe", null));
    }

    @Test
    public void signInWithEmptyPasswordReturnsFalse() {
        assertFalse(user.signIn("johndoe", ""));
    }

    @Test
    public void editProfileChangesFirstName() {
        user.editProfile("Johnny", "Doe", columbia);
        assertEquals("Johnny", user.getFirstName());
    }

    @Test
    public void editProfileChangesLastName() {
        user.editProfile("John", "Smith", columbia);
        assertEquals("Smith", user.getLastName());
    }

    @Test
    public void editProfileChangesLocation() {
        Location charleston = new Location("1 Cooper Street", "Charleston", "SC", "29403");
        user.editProfile("John", "Doe", charleston);
        assertSame(charleston, user.getLocation());
    }

    @Test(expected = IllegalArgumentException.class)
    public void editProfileRejectsBlankFirstName() {
        // A profile edit should not be able to wipe out the user's name.
        user.editProfile("", "Doe", columbia);
    }

    @Test
    public void markSafeSetsSafetyStatus() {
        user.markSafe(SafetyCategory.SAFE);
        assertEquals(SafetyCategory.SAFE, user.getSafety());
    }

    @Test
    public void markSafeCanChangeSafeUserToInDanger() {
        user.markSafe(SafetyCategory.SAFE);
        user.markSafe(SafetyCategory.IN_IMMEDIATE_DANGER);
        assertEquals(SafetyCategory.IN_IMMEDIATE_DANGER, user.getSafety());
    }

    @Test
    public void markSafeWithNullKeepsPreviousStatus() {
        // Passing null should not silently erase a user's safety status.
        user.markSafe(SafetyCategory.HIGH_RISK);
        user.markSafe(null);
        assertEquals(SafetyCategory.HIGH_RISK, user.getSafety());
    }

    @Test
    public void volunteerIsAResponder() {
        user.setRoles(Arrays.asList(UserRole.VOLUNTEER));
        assertTrue(user.isResponder());
    }

    @Test
    public void professionalIsAResponder() {
        user.setRoles(Arrays.asList(UserRole.PROFESSIONAL));
        assertTrue(user.isResponder());
    }

    @Test
    public void victimOnlyIsNotAResponder() {
        user.setRoles(Arrays.asList(UserRole.VICTIM));
        assertFalse(user.isResponder());
    }

    @Test
    public void userWithNoRolesIsNotAResponder() {
        assertFalse(user.isResponder());
    }

    @Test
    public void victimWhoIsAlsoVolunteerIsAResponder() {
        user.setRoles(Arrays.asList(UserRole.VICTIM, UserRole.VOLUNTEER));
        assertTrue(user.isResponder());
    }

    @Test
    public void addEmergencyContactAddsOneContact() {
        user.addEmergencyContact("Jane Doe", "555-0199", "Spouse");
        assertEquals(1, user.getEmergencyContacts().size());
    }

    @Test
    public void addEmergencyContactStoresTheGivenDetails() {
        user.addEmergencyContact("Jane Doe", "555-0199", "Spouse");
        EmergencyContact contact = user.getEmergencyContacts().get(0);
        assertEquals("Jane Doe (Spouse) - 555-0199", contact.toString());
    }

    @Test
    public void addingTwoEmergencyContactsKeepsBoth() {
        user.addEmergencyContact("Jane Doe", "555-0199", "Spouse");
        user.addEmergencyContact("Carlos Rivera", "555-0142", "Brother");
        assertEquals(2, user.getEmergencyContacts().size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addEmergencyContactRejectsBlankPhoneNumber() {
        // An emergency contact you can't call is useless.
        user.addEmergencyContact("Jane Doe", "", "Spouse");
    }

    @Test
    public void setUserNameTrimsSpaces() {
        user.setUserName("  newname  ");
        assertEquals("newname", user.getUserName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserNameRejectsNull() {
        user.setUserName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setUserNameRejectsBlank() {
        user.setUserName("   ");
    }

    @Test
    public void setPasswordChangesPassword() {
        user.setPassword("newPass789");
        assertTrue(user.signIn("johndoe", "newPass789"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPasswordRejectsNull() {
        user.setPassword(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPasswordRejectsEmpty() {
        user.setPassword("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void setPasswordRejectsSpacesOnly() {
        // setUserName rejects "   " and UserManagement.addUser rejects a blank
        // password, so setPassword should be consistent.
        user.setPassword("   ");
    }

    @Test
    public void setRolesWithNullClearsRoles() {
        user.setRoles(Arrays.asList(UserRole.VOLUNTEER));
        user.setRoles(null);
        assertTrue(user.getRoles().isEmpty());
    }

    @Test
    public void setRolesKeepsItsOwnCopyOfTheList() {
        List<UserRole> roles = new ArrayList<>(Arrays.asList(UserRole.VICTIM));
        user.setRoles(roles);
        roles.add(UserRole.ADMIN);
        assertFalse(user.getRoles().contains(UserRole.ADMIN));
    }

    @Test
    public void setEmergencyContactsWithNullClearsContacts() {
        user.addEmergencyContact("Jane Doe", "555-0199", "Spouse");
        user.setEmergencyContacts(null);
        assertTrue(user.getEmergencyContacts().isEmpty());
    }

    @Test
    public void setEmergencyContactsKeepsItsOwnCopyOfTheList() {
        List<EmergencyContact> contacts = new ArrayList<>();
        contacts.add(new EmergencyContact("Jane Doe", "555-0199", "Spouse"));
        user.setEmergencyContacts(contacts);
        contacts.add(new EmergencyContact("Carlos Rivera", "555-0142", "Brother"));
        assertEquals(1, user.getEmergencyContacts().size());
    }

    @Test
    public void getTypeReturnsUser() {
        assertEquals("USER", user.getType());
    }

    @Test
    public void toStringIncludesUsername() {
        assertTrue(user.toString().contains("johndoe"));
    }

    @Test
    public void toStringShowsNAWhenLocationIsMissing() {
        User noLocation = new User("johndoe", "John", "Doe", "password123", null);
        assertTrue(noLocation.toString().contains("Location: N/A"));
    }

    @Test
    public void toStringDoesNotIncludePassword() {
        assertFalse(user.toString().contains("password123"));
    }

    @Test
    public void stubMethodsDoNotChangeTheUser() {
        user.resetPassword();
        user.shareLocation();
        user.isGuest();
        user.Languages();
        assertTrue(user.signIn("johndoe", "password123"));
    }

    @Test
    public void newUserHasNoSafetyStatusYet() {
        assertNull(user.getSafety());
    }
}