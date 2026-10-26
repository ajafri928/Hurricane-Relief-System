package com.model;

/** The stages a relief request can be in. */
public enum ReliefRequestStatus {
    SUBMITTED,
    ASSIGNED,
    HELP_EN_ROUTE,
    COMPLETE,
    ESCALATED_TO_911,
    APPROVED,
    DENIED
}