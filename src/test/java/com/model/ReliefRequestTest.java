package com.model;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReliefRequestTest {

    @Test
    public void newRequestConstructorCreatesUsableRequest() {
        ReliefRequest request = new ReliefRequest(null, "Food and water");

        assertNotNull(request.getId());
        assertNotNull(request.getCreatedAt());
        assertEquals("Food and water", request.getAssistance());
        assertEquals(0, request.getPeopleCount());
        assertTrue(request.getPets().isEmpty());
        assertTrue(request.getComments().isEmpty());
    }

    @Test
    public void savedRequestConstructorKeepsProvidedValues() {
        UUID id = UUID.randomUUID();

        ReliefRequest request = new ReliefRequest(
                id, "2026-11-01T10:00:00", null, "Medical supplies",
                "Needs insulin", true, 9, UrgencyTier.URGENT,
                ReliefRequestStatus.SUBMITTED);

        assertEquals(id, request.getId());
        assertEquals("2026-11-01T10:00:00", request.getCreatedAt());
        assertEquals("Medical supplies", request.getAssistance());
        assertEquals("Needs insulin", request.getDescription());
        assertTrue(request.hasSpecialNeeds());
        assertEquals(9, request.getPriorityScore());
        assertEquals(UrgencyTier.URGENT, request.getUrgency());
        assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void savedRequestWithUserConstructorKeepsSubmitter() {
        User submitter = new User("sam", "Sam", "Taylor", "password", null);

        ReliefRequest request = new ReliefRequest(
                UUID.randomUUID(), "2026-11-01T10:00:00", null,
                "Transportation", "Needs a ride", false, 5,
                UrgencyTier.STANDARD, ReliefRequestStatus.SUBMITTED,
                submitter);

        assertSame(submitter, request.getSubmittedBy());
    }

    @Test
    public void submitChangesStatusToSubmitted() {
        ReliefRequest request = new ReliefRequest(null, "Food");

        request.submit();

        assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void updateStatusChangesToProvidedStatus() {
        ReliefRequest request = new ReliefRequest(null, "Food");

        request.updateStatus(ReliefRequestStatus.DENIED);

        assertEquals(ReliefRequestStatus.DENIED, request.getStatus());
    }

    @Test
    public void escalateChangesStatusToEscalatedTo911() {
        ReliefRequest request = new ReliefRequest(null, "Medical help");

        request.escalate();

        assertEquals(ReliefRequestStatus.ESCALATED_TO_911, request.getStatus());
    }

    @Test
    public void acceptThenDeclineStoresAndClearsResponder() {
        ReliefRequest request = new ReliefRequest(null, "Transportation");
        User responder = new User("rosa", "Rosa", "Delgado", "password", null);

        request.accept(responder);

        assertSame(responder, request.getResponder());
        assertEquals(ReliefRequestStatus.ASSIGNED, request.getStatus());

        request.decline();

        assertNull(request.getResponder());
        assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void completeChangesStatusToComplete() {
        ReliefRequest request = new ReliefRequest(null, "Shelter");

        request.complete();

        assertEquals(ReliefRequestStatus.COMPLETE, request.getStatus());
    }

    @Test
    public void addCommentAddsCommentAndReturnsCopyOfList() {
        ReliefRequest request = new ReliefRequest(null, "Food");
        User author = new User("rosa", "Rosa", "Delgado", "password", null);
        RequestComment comment = new RequestComment(author, "Volunteer has been notified.");

        request.addComment(comment);

        ArrayList<RequestComment> returnedComments = request.getComments();

        assertEquals(1, returnedComments.size());
        assertSame(comment, returnedComments.get(0));

        returnedComments.clear();

        assertEquals(1, request.getComments().size());
    }

    @Test
    public void addPersonAndPetUpdatesRequestListsAndPeopleCount() {
        ReliefRequest request = new ReliefRequest(null, "Evacuation");
        Person person = new Person("Jamie", "Lee", 30);
        Pet pet = new Pet("Bailey", "Dog");

        request.addPerson(person);
        request.addPet(pet);

        assertEquals(1, request.getPeopleCount());
        assertTrue(request.getRequestedPersons().contains(person));
        assertTrue(request.getPets().contains(pet));
    }

    @Test
    public void specialNeedsAndToStringContainCorrectInformation() {
        UUID id = UUID.randomUUID();

        ReliefRequest request = new ReliefRequest(
                id, "2026-11-01T10:00:00", null, "Medical supplies",
                "Needs insulin", true, 9, UrgencyTier.URGENT,
                ReliefRequestStatus.SUBMITTED);

        String summary = request.toString();

        assertTrue(request.hasSpecialNeeds());
        assertTrue(summary.contains(id.toString()));
        assertTrue(summary.contains("Medical supplies"));
        assertTrue(summary.contains("URGENT"));
        assertTrue(summary.contains("SUBMITTED"));
    }
        @Test
    public void newRequestConstructorKeepsProvidedLocation() {
        Location location = new Location(
                "100 Test Street", "Charleston", "SC", "29401");

        ReliefRequest request = new ReliefRequest(location, "Food");

        assertSame(location, request.getLocation());
        assertEquals("Food", request.getAssistance());
        assertNotNull(request.getId());
        assertNotNull(request.getCreatedAt());
    }

    @Test
    public void savedRequestConstructorStartsWithEmptyLists() {
        ReliefRequest request = new ReliefRequest(
                UUID.randomUUID(), "2026-11-01T10:00:00", null,
                "Supplies", "Test description", false, 4,
                UrgencyTier.STANDARD, ReliefRequestStatus.SUBMITTED);

        assertTrue(request.getRequestedPersons().isEmpty());
        assertTrue(request.getPets().isEmpty());
        assertTrue(request.getComments().isEmpty());
        assertEquals(0, request.getPeopleCount());
    }

    @Test
    public void savedRequestWithUserConstructorKeepsAllImportantValues() {
        UUID id = UUID.randomUUID();
        User submitter = new User("jordan", "Jordan", "Smith", "password", null);

        ReliefRequest request = new ReliefRequest(
                id, "2026-11-01T11:00:00", null,
                "Transportation", "Needs evacuation", true, 10,
                UrgencyTier.LIFE_THREATENING,
                ReliefRequestStatus.SUBMITTED, submitter);

        assertEquals(id, request.getId());
        assertEquals("Transportation", request.getAssistance());
        assertTrue(request.hasSpecialNeeds());
        assertEquals(10, request.getPriorityScore());
        assertEquals(UrgencyTier.LIFE_THREATENING, request.getUrgency());
        assertSame(submitter, request.getSubmittedBy());
    }

    @Test
    public void submitCanResubmitDeniedRequest() {
        ReliefRequest request = new ReliefRequest(null, "Food");

        request.updateStatus(ReliefRequestStatus.DENIED);
        request.submit();

        assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void updateStatusWorksForMoreThanOneStatus() {
        ReliefRequest request = new ReliefRequest(null, "Food");

        request.updateStatus(ReliefRequestStatus.HELP_EN_ROUTE);
        assertEquals(ReliefRequestStatus.HELP_EN_ROUTE, request.getStatus());

        request.updateStatus(ReliefRequestStatus.COMPLETE);
        assertEquals(ReliefRequestStatus.COMPLETE, request.getStatus());
    }

    @Test
    public void declineWithoutResponderStillReturnsRequestToSubmitted() {
        ReliefRequest request = new ReliefRequest(null, "Food");

        request.decline();

        assertNull(request.getResponder());
        assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
    }

    @Test
    public void addingMultipleCommentsKeepsTheirOrder() {
        ReliefRequest request = new ReliefRequest(null, "Food");
        RequestComment first = new RequestComment(null, "First update");
        RequestComment second = new RequestComment(null, "Second update");

        request.addComment(first);
        request.addComment(second);

        ArrayList<RequestComment> comments = request.getComments();

        assertEquals(2, comments.size());
        assertSame(first, comments.get(0));
        assertSame(second, comments.get(1));
    }

    @Test
    public void addingMultiplePeopleUpdatesPeopleCount() {
        ReliefRequest request = new ReliefRequest(null, "Evacuation");

        request.addPerson(new Person("Jamie", "Lee", 30));
        request.addPerson(new Person("Morgan", "Lee", 8));

        assertEquals(2, request.getPeopleCount());
        assertEquals("Jamie", request.getRequestedPersons().get(0).getFirstName());
        assertEquals("Morgan", request.getRequestedPersons().get(1).getFirstName());
    }

    @Test
    public void savedRequestCanHaveNoSpecialNeeds() {
        ReliefRequest request = new ReliefRequest(
                UUID.randomUUID(), "2026-11-01T10:00:00", null,
                "Food", "Test description", false, 3,
                UrgencyTier.STANDARD, ReliefRequestStatus.SUBMITTED);

        assertFalse(request.hasSpecialNeeds());
    }
}