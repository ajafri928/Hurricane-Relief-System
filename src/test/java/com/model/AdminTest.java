package com.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.UUID;

public class AdminTest {

    @Test
    public void approveChangesRequestStatusToApproved() {
        Admin admin = new Admin("Avery", "Brooks");
        ReliefRequest request = new ReliefRequest(null, "Food and water");

        admin.approve(request);

        assertEquals(ReliefRequestStatus.APPROVED, request.getStatus());
    }

    @Test
    public void denyChangesRequestStatusToDenied() {
        Admin admin = new Admin("Avery", "Brooks");
        ReliefRequest request = new ReliefRequest(null, "Food and water");

        admin.deny(request);

        assertEquals(ReliefRequestStatus.DENIED, request.getStatus());
    }
    @Test
    public void approveReplacesAnExistingStatus() {
        Admin admin = new Admin("Ava", "Admin");

        ReliefRequest request = new ReliefRequest(
                UUID.randomUUID(),
                "2026-10-25T08:15:00",
                null,
                "Medical supplies",
                "Needs supplies",
                false,
                5,
                UrgencyTier.URGENT,
                ReliefRequestStatus.ESCALATED_TO_911);

        admin.approve(request);

        assertEquals(ReliefRequestStatus.APPROVED, request.getStatus());
    }
    @Test
    public void denyReplacesAnExistingStatus() {
        Admin admin = new Admin("Ava", "Admin");

        ReliefRequest request = new ReliefRequest(
                UUID.randomUUID(),
                "2026-10-25T08:15:00",
                null,
                "Shelter",
                "Needs a shelter",
                false,
                4,
                UrgencyTier.STANDARD,
                ReliefRequestStatus.APPROVED);

        admin.deny(request);

        assertEquals(ReliefRequestStatus.DENIED, request.getStatus());
    }
}