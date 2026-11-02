package com.model;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.Test;

import static org.junit.Assert.*;

public class ReliefManagementTest {

    private ReliefRequest makeRequest(UUID id, ReliefRequestStatus status,
            String assistance, UrgencyTier urgency) {

        return new ReliefRequest(
                id, "2026-11-01T10:00:00", null, assistance,
                "Test request", false, 5, urgency, status);
    }

    @Test
    public void getInstanceReturnsSameManager() {
        ReliefManagement first = ReliefManagement.getInstance();
        ReliefManagement second = ReliefManagement.getInstance();

        assertSame(first, second);
    }

    @Test
    public void addFindAndRemoveRequestWorks() {
        ReliefManagement manager = ReliefManagement.getInstance();
        UUID id = UUID.randomUUID();

        ReliefRequest request = makeRequest(
                id, ReliefRequestStatus.SUBMITTED,
                "Food and water", UrgencyTier.STANDARD);

        try {
            manager.addReliefRequest(request);

            assertSame(request, manager.getReliefRequest(id));

            manager.removeReliefRequest(id);

            assertNull(manager.getReliefRequest(id));
        } finally {
            manager.removeReliefRequest(id);
        }
    }

    @Test
    public void getReliefRequestReturnsNullForUnknownId() {
        ReliefManagement manager = ReliefManagement.getInstance();

        assertNull(manager.getReliefRequest(UUID.randomUUID()));
    }

    @Test
    public void getRequestsByStatusReturnsOnlyMatchingRequests() {
        ReliefManagement manager = ReliefManagement.getInstance();

        ReliefRequest submitted = makeRequest(
                UUID.randomUUID(), ReliefRequestStatus.SUBMITTED,
                "Food and water", UrgencyTier.STANDARD);

        ReliefRequest complete = makeRequest(
                UUID.randomUUID(), ReliefRequestStatus.COMPLETE,
                "Shelter placement", UrgencyTier.STANDARD);

        try {
            manager.addReliefRequest(submitted);
            manager.addReliefRequest(complete);

            ArrayList<ReliefRequest> submittedRequests =
                    manager.getRequestsByStatus(ReliefRequestStatus.SUBMITTED);

            assertTrue(submittedRequests.contains(submitted));
            assertFalse(submittedRequests.contains(complete));

            for (ReliefRequest request : submittedRequests) {
                assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
            }
        } finally {
            manager.removeReliefRequest(submitted.getId());
            manager.removeReliefRequest(complete.getId());
        }
    }

    @Test
    public void recommendationReasonExplainsSpecialtyAndUrgency() {
        ReliefManagement manager = ReliefManagement.getInstance();

        User volunteer = new User(
                "rosa", "Rosa", "Delgado", "password", null);
        volunteer.setSpecialty(VolunteerRole.TRANSPORTATION);

        ReliefRequest request = makeRequest(
                UUID.randomUUID(), ReliefRequestStatus.SUBMITTED,
                "Transportation needed for evacuation",
                UrgencyTier.URGENT);

        String reason = manager.getRecommendationReason(request, volunteer);

        assertTrue(reason.contains("transportation specialty"));
        assertTrue(reason.contains("marked urgent"));
    }

    @Test
    public void recommendedRequestsOnlyContainSubmittedRequests() {
        ReliefManagement manager = ReliefManagement.getInstance();

        User volunteer = new User(
                "rosa", "Rosa", "Delgado", "password", null);
        volunteer.setSpecialty(VolunteerRole.TRANSPORTATION);

        ReliefRequest closedRequest = makeRequest(
                UUID.randomUUID(), ReliefRequestStatus.COMPLETE,
                "Transportation needed for evacuation",
                UrgencyTier.LIFE_THREATENING);

        try {
            manager.addReliefRequest(closedRequest);

            ArrayList<ReliefRequest> recommended =
                    manager.getRecommendedRequests(volunteer);

            assertFalse(recommended.contains(closedRequest));

            for (ReliefRequest request : recommended) {
                assertEquals(ReliefRequestStatus.SUBMITTED, request.getStatus());
            }
        } finally {
            manager.removeReliefRequest(closedRequest.getId());
        }
    }
    @Test
    public void recommendationReasonIsEmptyWhenNothingMatches() {
        ReliefManagement manager = ReliefManagement.getInstance();

        User user = new User("generalUser", "General", "Volunteer",
                "password", null);
        user.setSpecialty(VolunteerRole.GENERAL);

        ReliefRequest request = makeRequest(
                UUID.randomUUID(),
                ReliefRequestStatus.SUBMITTED,
                "Food and water supplies",
                UrgencyTier.STANDARD);

        assertEquals("", manager.getRecommendationReason(request, user));
    }
    @Test
    public void recommendationReasonMentionsLifeThreateningUrgency() {
        ReliefManagement manager = ReliefManagement.getInstance();

        User user = new User("generalUser", "General", "Volunteer",
                "password", null);
        user.setSpecialty(VolunteerRole.GENERAL);

        ReliefRequest request = makeRequest(
                UUID.randomUUID(),
                ReliefRequestStatus.SUBMITTED,
                "Food and water supplies",
                UrgencyTier.LIFE_THREATENING);

        String reason = manager.getRecommendationReason(request, user);

        assertTrue(reason.contains("life-threatening"));
    }
    @Test
    public void removeUnknownIdDoesNotRemoveAnExistingRequest() {
        ReliefManagement manager = ReliefManagement.getInstance();

        ReliefRequest request = makeRequest(
                UUID.randomUUID(),
                ReliefRequestStatus.SUBMITTED,
                "Supplies",
                UrgencyTier.STANDARD);

        manager.addReliefRequest(request);

        try {
            manager.removeReliefRequest(UUID.randomUUID());

            assertSame(request, manager.getReliefRequest(request.getId()));
        } finally {
            manager.removeReliefRequest(request.getId());
        }
    }
    @Test
    public void specialtyMatchRanksAheadOfUrgencyWithoutSpecialtyMatch() {
        ReliefManagement manager = ReliefManagement.getInstance();

        User rosa = new User("rosa", "Rosa", "Delgado", "password", null);
        rosa.setSpecialty(VolunteerRole.TRANSPORTATION);

        ReliefRequest transportationRequest = makeRequest(
                UUID.randomUUID(),
                ReliefRequestStatus.SUBMITTED,
                "Transportation to an evacuation shelter",
                UrgencyTier.STANDARD);

        ReliefRequest emergencyFoodRequest = makeRequest(
                UUID.randomUUID(),
                ReliefRequestStatus.SUBMITTED,
                "Food supplies",
                UrgencyTier.LIFE_THREATENING);

        manager.addReliefRequest(transportationRequest);
        manager.addReliefRequest(emergencyFoodRequest);

        try {
            ArrayList<ReliefRequest> recommended =
                    manager.getRecommendedRequests(rosa);

            assertTrue(recommended.indexOf(transportationRequest)
                    < recommended.indexOf(emergencyFoodRequest));
        } finally {
            manager.removeReliefRequest(transportationRequest.getId());
            manager.removeReliefRequest(emergencyFoodRequest.getId());
        }
    }
}