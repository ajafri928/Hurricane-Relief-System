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
}