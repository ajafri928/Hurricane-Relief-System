package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataWriterTest {

    private Location columbia;
    private User john;
    private User maria;

    @Before
    public void setUp() {
        TestDataFiles.backUpAll();
        columbia = new Location("123 Main St", "Apt 4B", "Columbia", "SC", "29201", 33.9988, -81.0453);

        john = new User(UUID.fromString(TestDataFiles.JOHN_ID), "johndoe", "John", "Doe",
                "password123", columbia, SafetyCategory.SAFE,
                new ArrayList<>(Arrays.asList(UserRole.VICTIM, UserRole.VOLUNTEER)));
        john.addEmergencyContact("Jane Doe", "555-0199", "Spouse");
        john.setContactPhone("803-555-0101");

        maria = new User(UUID.fromString(TestDataFiles.MARIA_ID), "mrivera", "Maria", "Rivera",
                "volunteer456", columbia, SafetyCategory.CAUTION,
                new ArrayList<>(Arrays.asList(UserRole.VOLUNTEER)));
        maria.setSpecialty(VolunteerRole.EMS);
    }

    @After
    public void tearDown() {
        TestDataFiles.restoreAll();
    }

    @Test
    public void getUserJSONWritesIdAsString() {
        assertEquals(TestDataFiles.JOHN_ID, DataWriter.getUserJSON(john).get(DataConstants.USER_ID));
    }

    @Test
    public void getUserJSONWritesUsername() {
        assertEquals("johndoe", DataWriter.getUserJSON(john).get(DataConstants.USER_USERNAME));
    }

    @Test
    public void getUserJSONWritesPassword() {
        assertEquals("password123", DataWriter.getUserJSON(john).get(DataConstants.USER_PASSWORD));
    }

    @Test
    public void getUserJSONWritesRolesAsStrings() {
        JSONArray roles = (JSONArray) DataWriter.getUserJSON(john).get(DataConstants.USER_ROLES);
        assertEquals(Arrays.asList("VICTIM", "VOLUNTEER"), roles);
    }

    @Test
    public void getUserJSONWritesSafetyAsString() {
        assertEquals("SAFE", DataWriter.getUserJSON(john).get(DataConstants.USER_SAFETY));
    }

    @Test
    public void getUserJSONWritesNullWhenSafetyIsMissing() {
        john.setSafety(null);
        assertNull(DataWriter.getUserJSON(john).get(DataConstants.USER_SAFETY));
    }

    @Test
    public void getUserJSONWritesSpecialtyAsString() {
        assertEquals("EMS", DataWriter.getUserJSON(maria).get(DataConstants.USER_SPECIALTY));
    }

    @Test
    public void getUserJSONWritesNullWhenSpecialtyIsMissing() {
        assertNull(DataWriter.getUserJSON(john).get(DataConstants.USER_SPECIALTY));
    }

    @Test
    public void getUserJSONWritesEmergencyContactPhoneUnderPhoneNumberKey() {
        JSONArray contacts = (JSONArray) DataWriter.getUserJSON(john).get(DataConstants.USER_EMERGENCY_CONTACTS);
        assertEquals("555-0199", ((JSONObject) contacts.get(0)).get("phoneNumber"));
    }

    @Test
    public void getUserJSONWritesLocationCity() {
        JSONObject location = (JSONObject) DataWriter.getUserJSON(john).get(DataConstants.USER_LOCATION);
        assertEquals("Columbia", location.get(DataConstants.LOCATION_CITY));
    }

    @Test
    public void getUserJSONWritesLocationLatitude() {
        JSONObject location = (JSONObject) DataWriter.getUserJSON(john).get(DataConstants.USER_LOCATION);
        assertEquals(33.9988, (Double) location.get(DataConstants.LOCATION_LATITUDE), 0.0001);
    }

    @Test
    public void getUserJSONWritesNullLocationForUserWithNoLocation() {
        // User allows a null location (and DataLoader creates them), so saving
        // one should not throw NullPointerException.
        User noLocation = new User("nolocation", "No", "Location", "password1", null);
        assertNull(DataWriter.getUserJSON(noLocation).get(DataConstants.USER_LOCATION));
    }

    @Test
    public void saveUsersWritesEveryUser() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        assertEquals(2, DataLoader.getUsers().size());
    }

    @Test
    public void saveUsersThenLoadKeepsUsername() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john)));
        assertEquals("johndoe", DataLoader.getUsers().get(0).getUserName());
    }

    @Test
    public void saveUsersThenLoadKeepsId() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john)));
        assertEquals(john.getId(), DataLoader.getUsers().get(0).getId());
    }

    @Test
    public void saveUsersThenLoadKeepsRoles() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john)));
        assertEquals(john.getRoles(), DataLoader.getUsers().get(0).getRoles());
    }

    @Test
    public void saveUsersThenLoadKeepsEmergencyContact() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john)));
        assertEquals("Jane Doe (Spouse) - 555-0199",
                DataLoader.getUsers().get(0).getEmergencyContacts().get(0).toString());
    }

    @Test
    public void saveUsersThenLoadKeepsSpecialty() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(maria)));
        assertEquals(VolunteerRole.EMS, DataLoader.getUsers().get(0).getSpecialty());
    }

    @Test
    public void saveUsersThenLoadKeepsAddressLine2() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john)));
        assertEquals("Apt 4B", DataLoader.getUsers().get(0).getLocation().getAddressLine2());
    }

    @Test
    public void saveUsersWithEmptyListWritesEmptyArray() {
        DataWriter.saveUsers(new ArrayList<>());
        assertEquals("[]", TestDataFiles.read(DataConstants.USER_FILE_NAME).trim());
    }

    private Shelter makeShelter() {
        Shelter shelter = new Shelter(UUID.fromString("a1a6c92c-4dd3-4aaf-bb06-1800a1d00101"),
                "St. Julian Devine Family Shelter", columbia, 180, 150,
                true, true, false, true, "OPEN");
        shelter.addAmenity("Cots");
        shelter.addResource(new ShelterResource("Water Bottles", 950,
                LocalDateTime.of(2026, 11, 15, 18, 0), 200, "cases"));
        shelter.getOccupants().add(new Person("David", "Carter", 41));
        shelter.getPetsOnSite().add(new Pet("Buddy", "Dog"));
        return shelter;
    }

    @Test
    public void getShelterJSONWritesName() {
        assertEquals("St. Julian Devine Family Shelter",
                DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_NAME));
    }

    @Test
    public void getShelterJSONWritesCapacity() {
        assertEquals(180, DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_CAPACITY));
    }

    @Test
    public void getShelterJSONWritesAmenities() {
        assertEquals(Arrays.asList("Cots"),
                DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_AMENITIES));
    }

    @Test
    public void getShelterJSONWritesResourceDateAsText() {
        JSONArray resources = (JSONArray) DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_RESOURCES);
        assertEquals("2026-11-15T18:00", ((JSONObject) resources.get(0)).get(DataConstants.RESOURCE_AVAILABLE_UNTIL));
    }

    @Test
    public void getShelterJSONWritesNullForResourceWithNoDate() {
        Shelter shelter = makeShelter();
        shelter.getShelterResources().get(0).setAvailableUntil(null);
        JSONArray resources = (JSONArray) DataWriter.getShelterJSON(shelter).get(DataConstants.SHELTER_RESOURCES);
        assertNull(((JSONObject) resources.get(0)).get(DataConstants.RESOURCE_AVAILABLE_UNTIL));
    }

    @Test
    public void getShelterJSONWritesOccupants() {
        JSONArray occupants = (JSONArray) DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_OCCUPANTS);
        assertEquals("David", ((JSONObject) occupants.get(0)).get(DataConstants.PERSON_FIRST_NAME));
    }

    @Test
    public void getShelterJSONWritesPets() {
        JSONArray pets = (JSONArray) DataWriter.getShelterJSON(makeShelter()).get(DataConstants.SHELTER_PETS_ON_SITE);
        assertEquals("Buddy", ((JSONObject) pets.get(0)).get(DataConstants.PET_NAME));
    }

    @Test
    public void saveSheltersThenLoadKeepsResourceQuantity() {
        DataWriter.saveShelters(new ArrayList<>(Arrays.asList(makeShelter())));
        assertEquals(950, DataLoader.getShelters().get(0).getShelterResources().get(0).getQuantity());
    }

    @Test
    public void saveSheltersThenLoadKeepsOccupantId() {
        Shelter shelter = makeShelter();
        DataWriter.saveShelters(new ArrayList<>(Arrays.asList(shelter)));
        assertEquals(shelter.getOccupants().get(0).getId(),
                DataLoader.getShelters().get(0).getOccupants().get(0).getId());
    }

    @Test
    public void saveSheltersThenLoadKeepsVetStaffFlag() {
        DataWriter.saveShelters(new ArrayList<>(Arrays.asList(makeShelter())));
        assertTrue(DataLoader.getShelters().get(0).hasVetStaff());
    }

    @Test
    public void getPersonJSONWritesAge() {
        assertEquals(76, DataWriter.getPersonJSON(new Person("Walter", "Hayes", 76)).get(DataConstants.PERSON_AGE));
    }

    @Test
    public void getPersonJSONWritesNeedsMedicalAssistance() {
        Person walter = new Person(UUID.randomUUID(), "Walter", "Hayes", 76, true);
        assertEquals(true, DataWriter.getPersonJSON(walter).get(DataConstants.PERSON_NEEDS_MEDICAL));
    }

    @Test
    public void getPetJSONWritesSpecies() {
        assertEquals("Cat", DataWriter.getPetJSON(new Pet("Whiskers", "Cat")).get(DataConstants.PET_SPECIES));
    }

    private ReliefRequest makeRequest() {
        ReliefRequest request = new ReliefRequest(
                UUID.fromString("b7d1c4e8-2a95-4f63-8c70-5e9a1b6d3f24"), "2026-10-25T08:40:00",
                columbia, "Transportation", "Needs an accessible ride", true, 9,
                UrgencyTier.URGENT, ReliefRequestStatus.SUBMITTED, john);
        request.addPerson(new Person("Walter", "Hayes", 76));
        request.addPet(new Pet("Whiskers", "Cat"));
        request.setResponder(maria);
        request.setContactPhone("843-555-0202");
        request.addComment(new RequestComment(maria, "On my way", LocalDateTime.of(2026, 10, 25, 9, 0)));
        return request;
    }

    @Test
    public void getReliefRequestJSONWritesSubmitterId() {
        assertEquals(TestDataFiles.JOHN_ID,
                DataWriter.getReliefRequestJSON(makeRequest()).get(DataConstants.RELIEF_REQUEST_USER_ID));
    }

    @Test
    public void getReliefRequestJSONWritesNullWhenNoSubmitter() {
        ReliefRequest request = makeRequest();
        request.setSubmittedBy(null);
        assertNull(DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_USER_ID));
    }

    @Test
    public void getReliefRequestJSONWritesResponderId() {
        assertEquals(TestDataFiles.MARIA_ID,
                DataWriter.getReliefRequestJSON(makeRequest()).get(DataConstants.RELIEF_REQUEST_ASSIGNED_TO));
    }

    @Test
    public void getReliefRequestJSONWritesNullWhenNoResponder() {
        ReliefRequest request = makeRequest();
        request.setResponder(null);
        assertNull(DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_ASSIGNED_TO));
    }

    @Test
    public void getReliefRequestJSONWritesUrgencyAsString() {
        assertEquals("URGENT", DataWriter.getReliefRequestJSON(makeRequest()).get(DataConstants.RELIEF_REQUEST_URGENCY));
    }

    @Test
    public void getReliefRequestJSONWritesNullWhenUrgencyMissing() {
        ReliefRequest request = makeRequest();
        request.setUrgency(null);
        assertNull(DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_URGENCY));
    }

    @Test
    public void getReliefRequestJSONWritesNullWhenStatusMissing() {
        ReliefRequest request = new ReliefRequest(UUID.randomUUID(), "2026-10-25T08:40:00",
                columbia, "Water", "", false, 1, UrgencyTier.STANDARD, null, john);
        assertNull(DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_STATUS));
    }

    @Test
    public void getReliefRequestJSONWritesCommentAuthorId() {
        JSONArray comments = (JSONArray) DataWriter.getReliefRequestJSON(makeRequest()).get(DataConstants.RELIEF_REQUEST_COMMENTS);
        assertEquals(TestDataFiles.MARIA_ID, ((JSONObject) comments.get(0)).get(DataConstants.COMMENT_AUTHOR_ID));
    }

    @Test
    public void getReliefRequestJSONWritesNullAuthorForAnonymousComment() {
        ReliefRequest request = makeRequest();
        request.addComment(new RequestComment(null, "Anonymous tip", LocalDateTime.of(2026, 10, 25, 10, 0)));
        JSONArray comments = (JSONArray) DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_COMMENTS);
        assertNull(((JSONObject) comments.get(1)).get(DataConstants.COMMENT_AUTHOR_ID));
    }

    @Test
    public void getReliefRequestJSONHandlesCommentWithNoTimestamp() {
        // RequestComment allows a null timestamp and DataLoader handles a missing
        // one, so DataWriter should write null instead of throwing NullPointerException.
        ReliefRequest request = makeRequest();
        request.addComment(new RequestComment(maria, "No time given", null));
        JSONArray comments = (JSONArray) DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_COMMENTS);
        assertNull(((JSONObject) comments.get(1)).get(DataConstants.COMMENT_TIMESTAMP));
    }

    @Test
    public void getReliefRequestJSONWritesNullLocationForRequestWithNoLocation() {
        // A GPS failure can leave a request with no location; saving it should not crash.
        ReliefRequest request = new ReliefRequest(null, "Water");
        assertNull(DataWriter.getReliefRequestJSON(request).get(DataConstants.RELIEF_REQUEST_LOCATION));
    }

    @Test
    public void saveReliefRequestsThenLoadLinksSubmitterBack() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        assertEquals(john.getId(), DataLoader.getReliefRequests().get(0).getSubmittedBy().getId());
    }

    @Test
    public void saveReliefRequestsThenLoadKeepsComment() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        assertEquals("On my way", DataLoader.getReliefRequests().get(0).getComments().get(0).getText());
    }

    @Test
    public void saveReliefRequestsThenLoadKeepsRequestedPerson() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        assertEquals("Walter Hayes", DataLoader.getReliefRequests().get(0).getRequestedPersons().get(0).getFullName());
    }

    @Test
    public void saveReliefRequestsThenLoadKeepsCreatedDate() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        assertEquals("2026-10-25T08:40:00", DataLoader.getReliefRequests().get(0).getCreatedAt());
    }

    @Test
    public void saveReliefRequestsWithEmptyListWritesEmptyArray() {
        DataWriter.saveReliefRequests(new ArrayList<>());
        assertEquals("[]", TestDataFiles.read(DataConstants.RELIEF_REQUEST_FILE_NAME).trim());
    }

    @Test
    public void saveReliefRequestsDoesNotChangeUsersFile() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        String before = TestDataFiles.read(DataConstants.USER_FILE_NAME);
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        assertEquals(before, TestDataFiles.read(DataConstants.USER_FILE_NAME));
    }

    @Test
    public void savedRequestPointsAtTheSameUserObjectAsItsComment() {
        DataWriter.saveUsers(new ArrayList<>(Arrays.asList(john, maria)));
        DataWriter.saveReliefRequests(new ArrayList<>(Arrays.asList(makeRequest())));
        ReliefRequest loaded = DataLoader.getReliefRequests().get(0);
        assertSame(loaded.getResponder(), loaded.getComments().get(0).getAuthor());
    }
}