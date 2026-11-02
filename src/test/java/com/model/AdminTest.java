package com.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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
}