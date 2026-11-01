package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class DataLoaderTest {

    @Before
    public void setUp() {
        TestDataFiles.backUpAll();
        TestDataFiles.write(DataConstants.USER_FILE_NAME, TestDataFiles.TWO_USERS_JSON);
    }

    @After
    public void tearDown() {
        TestDataFiles.restoreAll();
    }

    private User firstUser() {
        return DataLoader.getUsers().get(0);
    }

    @Test
    public void getUsersLoadsEveryUserInTheFile() {
        assertEquals(2, DataLoader.getUsers().size());
    }

    @Test
    public void getUsersReadsTheId() {
        assertEquals(UUID.fromString(TestDataFiles.JOHN_ID), firstUser().getId());
    }

    @Test
    public void getUsersReadsTheUsername() {
        assertEquals("johndoe", firstUser().getUserName());
    }

    @Test
    public void getUsersReadsThePassword() {
        assertEquals("password123", firstUser().getPassword());
    }

    @Test
    public void getUsersReadsFirstAndLastName() {
        assertEquals("John Doe", firstUser().getFirstName() + " " + firstUser().getLastName());
    }

    @Test
    public void getUsersReadsAllRoles() {
        assertEquals(Arrays.asList(UserRole.VICTIM, UserRole.VOLUNTEER), firstUser().getRoles());
    }

    @Test
    public void getUsersReadsSafetyStatus() {
        assertEquals(SafetyCategory.SAFE, firstUser().getSafety());
    }

    @Test
    public void getUsersReadsVolunteerSpecialty() {
        assertEquals(VolunteerRole.EMS, DataLoader.getUsers().get(1).getSpecialty());
    }

    @Test
    public void getUsersReadsContactPhone() {
        assertEquals("803-555-0101", firstUser().getContactPhone());
    }

    @Test
    public void getUsersReadsLocationAddress() {
        assertEquals("123 Main St, Apt 4B, Columbia, SC 29201", firstUser().getLocation().formatAddress());
    }

    @Test
    public void getUsersReadsLocationCoordinates() {
        assertEquals(33.9988, firstUser().getLocation().getLatitude(), 0.0001);
    }

    @Test
    public void getUsersReadsEmergencyContacts() {
        EmergencyContact contact = firstUser().getEmergencyContacts().get(0);
        assertEquals("Jane Doe (Spouse) - 555-0199", contact.toString());
    }

    @Test
    public void getUsersReadsLowercaseRoleNames() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\",\"roles\":[\"volunteer\"]}]");
        assertEquals(Arrays.asList(UserRole.VOLUNTEER), firstUser().getRoles());
    }

    @Test
    public void getUsersSkipsUnknownRoleNames() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\",\"roles\":[\"WIZARD\",\"VICTIM\"]}]");
        assertEquals(Arrays.asList(UserRole.VICTIM), firstUser().getRoles());
    }

    @Test
    public void getUsersGivesNullSafetyForUnknownSafetyValue() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\",\"safety\":\"FINE\"}]");
        assertNull(firstUser().getSafety());
    }

    @Test
    public void getUsersWithMissingRolesGivesEmptyRoleList() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\"}]");
        assertTrue(firstUser().getRoles().isEmpty());
    }

    @Test
    public void getUsersWithMissingLocationGivesNullLocation() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\"}]");
        assertNull(firstUser().getLocation());
    }

    @Test
    public void getUsersAcceptsASingleUserObjectInsteadOfAnArray() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "{\"id\":\"" + TestDataFiles.JOHN_ID + "\",\"userName\":\"johndoe\"}");
        assertEquals("johndoe", firstUser().getUserName());
    }

    @Test
    public void getUsersReturnsEmptyListWhenFileIsMissing() {
        TestDataFiles.delete(DataConstants.USER_FILE_NAME);
        assertTrue(DataLoader.getUsers().isEmpty());
    }

    @Test
    public void getUsersReturnsEmptyListWhenFileIsNotValidJson() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME, "{ this is not json");
        assertTrue(DataLoader.getUsers().isEmpty());
    }

    @Test
    public void getUsersReturnsEmptyListWhenFileHoldsJustAString() {
        TestDataFiles.write(DataConstants.USER_FILE_NAME, "\"hello\"");
        assertTrue(DataLoader.getUsers().isEmpty());
    }

    @Test
    public void getUsersStillLoadsLaterUsersWhenOneRecordIsBroken() {
        // 2nd user's "roles" is a string instead of an array. One bad record
        // should not wipe out every user after it.
        TestDataFiles.write(DataConstants.USER_FILE_NAME, "["
                + "{\"id\":\"" + UUID.randomUUID() + "\",\"userName\":\"first\"},"
                + "{\"id\":\"" + UUID.randomUUID() + "\",\"userName\":\"broken\",\"roles\":\"VICTIM\"},"
                + "{\"id\":\"" + UUID.randomUUID() + "\",\"userName\":\"third\"}"
                + "]");
        ArrayList<User> users = DataLoader.getUsers();
        assertEquals("third", users.get(users.size() - 1).getUserName());
    }

    @Test
    public void getUsersGivesAUserWithAnInvalidIdANewId() {
        // Persons, pets, and hurricane events get a new id when theirs is bad.
        // A user with a null id makes UserManagement.getUser(id) crash.
        TestDataFiles.write(DataConstants.USER_FILE_NAME,
                "[{\"id\":\"not-a-uuid\",\"userName\":\"johndoe\"}]");
        assertNotNull(firstUser().getId());
    }

    private static final String SHELTER_ID = "a1a6c92c-4dd3-4aaf-bb06-1800a1d00101";

    private static final String ONE_SHELTER_JSON = """
        [{
          "id": "a1a6c92c-4dd3-4aaf-bb06-1800a1d00101",
          "name": "St. Julian Devine Family Shelter",
          "capacity": 180, "occupancy": 150,
          "petFriendly": true, "accessible": "true", "medicalStaff": false, "vetStaff": true,
          "operationalStatus": "OPEN",
          "location": { "street1": "1 Cooper Street", "street2": "", "city": "Charleston",
                        "state": "SC", "zipCode": "29403", "latitude": 32.8006, "longitude": -79.9332 },
          "amenities": ["Cots", "Charging Stations"],
          "shelterResources": [
            { "type": "Water Bottles", "unit": "cases", "quantity": 950,
              "availableUntil": "2026-11-15T18:00", "lowStockThreshold": 200 },
            { "type": "Blankets", "unit": "each", "quantity": 40,
              "availableUntil": "2026-11-20T12:00:00Z", "lowStockThreshold": 50 },
            { "type": "MREs", "unit": "boxes", "quantity": 10,
              "availableUntil": "next tuesday", "lowStockThreshold": 5 }
          ],
          "occupants": [
            { "id": "a4997eed-e9f4-4197-a695-a2de28e6ffb7", "firstName": "David", "lastName": "Carter",
              "age": 41, "needsMedicalAssistance": false },
            { "firstName": "Erin", "lastName": "Carter", "age": 9, "needsMedicalAssistance": true }
          ],
          "petsOnSite": [
            { "id": "66dfd42e-5251-452d-872b-1e13e8c18e8e", "name": "Buddy", "species": "Dog" }
          ]
        }]
        """;

    private Shelter loadOneShelter() {
        TestDataFiles.write(DataConstants.SHELTER_FILE_NAME, ONE_SHELTER_JSON);
        return DataLoader.getShelters().get(0);
    }

    @Test
    public void getSheltersLoadsEveryShelterInTheFile() {
        TestDataFiles.write(DataConstants.SHELTER_FILE_NAME, ONE_SHELTER_JSON);
        assertEquals(1, DataLoader.getShelters().size());
    }

    @Test
    public void getSheltersReadsIdAndName() {
        Shelter shelter = loadOneShelter();
        assertEquals(UUID.fromString(SHELTER_ID) + " St. Julian Devine Family Shelter",
                shelter.getId() + " " + shelter.getName());
    }

    @Test
    public void getSheltersReadsCapacity() {
        assertEquals(180, loadOneShelter().getCapacity());
    }

    @Test
    public void getSheltersReadsOccupancy() {
        assertEquals(150, loadOneShelter().getOccupancy());
    }

    @Test
    public void getSheltersReadsTrueBoolean() {
        assertTrue(loadOneShelter().isPetFriendly());
    }

    @Test
    public void getSheltersReadsFalseBoolean() {
        assertFalse(loadOneShelter().hasMedicalStaff());
    }

    @Test
    public void getSheltersReadsBooleanWrittenAsString() {
        assertTrue(loadOneShelter().isAccessible());
    }

    @Test
    public void getSheltersReadsOperationalStatus() {
        assertEquals("OPEN", loadOneShelter().getOperationalStatus());
    }

    @Test
    public void getSheltersReadsLocationCity() {
        assertEquals("Charleston", loadOneShelter().getLocation().getCity());
    }

    @Test
    public void getSheltersReadsAmenities() {
        assertEquals(Arrays.asList("Cots", "Charging Stations"), loadOneShelter().getAmenities());
    }

    @Test
    public void getSheltersReadsResourceQuantity() {
        assertEquals(950, loadOneShelter().getShelterResources().get(0).getQuantity());
    }

    @Test
    public void getSheltersReadsResourceDate() {
        assertEquals(LocalDateTime.of(2026, 11, 15, 18, 0),
                loadOneShelter().getShelterResources().get(0).getAvailableUntil());
    }

    @Test
    public void getSheltersReadsResourceDateEndingInZ() {
        assertEquals(LocalDateTime.of(2026, 11, 20, 12, 0),
                loadOneShelter().getShelterResources().get(1).getAvailableUntil());
    }

    @Test
    public void getSheltersGivesNullForInvalidResourceDate() {
        assertNull(loadOneShelter().getShelterResources().get(2).getAvailableUntil());
    }

    @Test
    public void getSheltersReadsOccupants() {
        assertEquals("David Carter", loadOneShelter().getOccupants().get(0).getFullName());
    }

    @Test
    public void getSheltersGivesOccupantWithoutIdANewId() {
        assertNotNull(loadOneShelter().getOccupants().get(1).getId());
    }

    @Test
    public void getSheltersReadsOccupantNeedsMedicalAssistance() {
        assertTrue(loadOneShelter().getOccupants().get(1).needsMedicalAssistance());
    }

    @Test
    public void getSheltersReadsPetsOnSite() {
        assertEquals("Buddy", loadOneShelter().getPetsOnSite().get(0).getName());
    }

    @Test
    public void getSheltersWithNoListsGivesEmptyLists() {
        TestDataFiles.write(DataConstants.SHELTER_FILE_NAME,
                "[{\"id\":\"" + SHELTER_ID + "\",\"name\":\"Bare Shelter\",\"capacity\":10}]");
        Shelter shelter = DataLoader.getShelters().get(0);
        assertEquals(0, shelter.getAmenities().size() + shelter.getShelterResources().size()
                + shelter.getOccupants().size() + shelter.getPetsOnSite().size());
    }

    @Test
    public void getSheltersWithMissingNumbersUsesZero() {
        TestDataFiles.write(DataConstants.SHELTER_FILE_NAME,
                "[{\"id\":\"" + SHELTER_ID + "\",\"name\":\"Bare Shelter\"}]");
        assertEquals(0, DataLoader.getShelters().get(0).getCapacity());
    }

    @Test
    public void getSheltersReturnsEmptyListWhenFileIsMissing() {
        TestDataFiles.delete(DataConstants.SHELTER_FILE_NAME);
        assertTrue(DataLoader.getShelters().isEmpty());
    }

    private static final String REQUEST_ID = "b7d1c4e8-2a95-4f63-8c70-5e9a1b6d3f24";

    private static final String ONE_REQUEST_JSON = """
        [{
          "id": "b7d1c4e8-2a95-4f63-8c70-5e9a1b6d3f24",
          "requestDate": "2026-10-25T08:40:00",
          "assistance": "Transportation - Accessible Ride",
          "description": "Resident using a wheelchair needs a ride.",
          "userId": "123e4567-e89b-12d3-a456-426614174000",
          "assignedTo": "223e4567-e89b-12d3-a456-426614174001",
          "specialNeeds": true, "priorityScore": 9,
          "urgency": "URGENT", "status": "ASSIGNED",
          "contactPhone": "843-555-0202",
          "location": { "street1": "265 Fishburne Street", "city": "Charleston", "state": "SC",
                        "zipCode": "29403", "latitude": 32.7904, "longitude": -79.9594 },
          "requestedPersons": [
            { "id": "5a9d2e7c-1b84-4f63-9c50-8e3a6d1b7f24", "firstName": "Walter", "lastName": "Hayes",
              "age": 76, "needsMedicalAssistance": true }
          ],
          "pets": [ { "name": "Whiskers", "species": "Cat" } ],
          "comments": [
            { "commenterId": "223e4567-e89b-12d3-a456-426614174001", "text": "On my way",
              "timestamp": "2026-10-25T09:00:00" },
            { "commenterId": "123e4567-e89b-12d3-a456-426614174000", "text": "Thank you" }
          ]
        }]
        """;

    private ReliefRequest loadOneRequest() {
        TestDataFiles.write(DataConstants.RELIEF_REQUEST_FILE_NAME, ONE_REQUEST_JSON);
        return DataLoader.getReliefRequests().get(0);
    }

    @Test
    public void getReliefRequestsLoadsEveryRequestInTheFile() {
        TestDataFiles.write(DataConstants.RELIEF_REQUEST_FILE_NAME, ONE_REQUEST_JSON);
        assertEquals(1, DataLoader.getReliefRequests().size());
    }

    @Test
    public void getReliefRequestsReadsTheId() {
        assertEquals(UUID.fromString(REQUEST_ID), loadOneRequest().getId());
    }

    @Test
    public void getReliefRequestsReadsAssistanceType() {
        assertEquals("Transportation - Accessible Ride", loadOneRequest().getAssistance());
    }

    @Test
    public void getReliefRequestsReadsUrgency() {
        assertEquals(UrgencyTier.URGENT, loadOneRequest().getUrgency());
    }

    @Test
    public void getReliefRequestsReadsStatus() {
        assertEquals(ReliefRequestStatus.ASSIGNED, loadOneRequest().getStatus());
    }

    @Test
    public void getReliefRequestsReadsPriorityScore() {
        assertEquals(9, loadOneRequest().getPriorityScore());
    }

    @Test
    public void getReliefRequestsReadsSpecialNeeds() {
        assertTrue(loadOneRequest().hasSpecialNeeds());
    }

    @Test
    public void getReliefRequestsReadsContactPhone() {
        assertEquals("843-555-0202", loadOneRequest().getContactPhone());
    }

    @Test
    public void getReliefRequestsLinksTheSubmittingUser() {
        assertEquals("johndoe", loadOneRequest().getSubmittedBy().getUserName());
    }

    @Test
    public void getReliefRequestsLinksTheAssignedResponder() {
        assertEquals("mrivera", loadOneRequest().getResponder().getUserName());
    }

    @Test
    public void getReliefRequestsUsesTheSameUserObjectForSubmitterAndCommenter() {
        ReliefRequest request = loadOneRequest();
        assertSame(request.getSubmittedBy(), request.getComments().get(1).getAuthor());
    }

    @Test
    public void getReliefRequestsReadsRequestedPersons() {
        assertEquals("Walter Hayes", loadOneRequest().getRequestedPersons().get(0).getFullName());
    }

    @Test
    public void getReliefRequestsReadsPets() {
        assertEquals("Cat", loadOneRequest().getPets().get(0).getSpecies());
    }

    @Test
    public void getReliefRequestsReadsCommentText() {
        assertEquals("On my way", loadOneRequest().getComments().get(0).getText());
    }

    @Test
    public void getReliefRequestsReadsCommentTimestamp() {
        assertEquals(LocalDateTime.of(2026, 10, 25, 9, 0),
                loadOneRequest().getComments().get(0).getTimeStamp());
    }

    @Test
    public void getReliefRequestsGivesCommentWithoutTimestampATimestamp() {
        assertNotNull(loadOneRequest().getComments().get(1).getTimeStamp());
    }

    @Test
    public void getReliefRequestsGivesNullSubmitterForUnknownUserId() {
        TestDataFiles.write(DataConstants.RELIEF_REQUEST_FILE_NAME,
                "[{\"id\":\"" + REQUEST_ID + "\",\"userId\":\"" + UUID.randomUUID() + "\"}]");
        assertNull(DataLoader.getReliefRequests().get(0).getSubmittedBy());
    }

    @Test
    public void getReliefRequestsWithNoPeopleGivesEmptyList() {
        TestDataFiles.write(DataConstants.RELIEF_REQUEST_FILE_NAME,
                "[{\"id\":\"" + REQUEST_ID + "\"}]");
        assertTrue(DataLoader.getReliefRequests().get(0).getRequestedPersons().isEmpty());
    }

    @Test
    public void getReliefRequestsReturnsEmptyListWhenFileIsMissing() {
        TestDataFiles.delete(DataConstants.RELIEF_REQUEST_FILE_NAME);
        assertTrue(DataLoader.getReliefRequests().isEmpty());
    }

    private static final String EVENT_ID = "777e4567-e89b-12d3-a456-426614174777";

    @Test
    public void getHurricaneEventsReadsNameCategoryAndStatus() {
        TestDataFiles.write(DataConstants.EVENT_FILE_NAME,
                "[{\"id\":\"" + EVENT_ID + "\",\"name\":\"Hurricane Helena\",\"category\":4,\"status\":\"ACTIVE\"}]");
        HurricaneEvent event = DataLoader.getHurricaneEvents().get(0);
        assertEquals("Hurricane Helena 4 ACTIVE",
                event.getName() + " " + event.getCategory() + " " + event.getStatus());
    }

    @Test
    public void getHurricaneEventsReadsTheId() {
        TestDataFiles.write(DataConstants.EVENT_FILE_NAME,
                "[{\"id\":\"" + EVENT_ID + "\",\"name\":\"Hurricane Helena\"}]");
        assertEquals(UUID.fromString(EVENT_ID), DataLoader.getHurricaneEvents().get(0).getId());
    }

    @Test
    public void getHurricaneEventsGivesEventWithoutIdANewId() {
        TestDataFiles.write(DataConstants.EVENT_FILE_NAME, "[{\"name\":\"Hurricane Ian\"}]");
        assertNotNull(DataLoader.getHurricaneEvents().get(0).getId());
    }

    @Test
    public void getHurricaneEventsReturnsEmptyListWhenFileIsMissing() {
        TestDataFiles.delete(DataConstants.EVENT_FILE_NAME);
        assertTrue(DataLoader.getHurricaneEvents().isEmpty());
    }
}