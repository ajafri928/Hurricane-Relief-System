package com.model;

/**
 * The types of hazards a user can report.
 */
public enum HazardCategory {
    /** Water covering roads or buildings. */
    FLOODING,
    /** Damage from strong winds, like downed trees or roofs. */
    WIND_DAMAGE,
    /** An area that has lost power. */
    POWER_OUTAGE,
    /** A road that can't be driven on. */
    BLOCKED_ROAD,
    /** Someone who needs medical help right away. */
    MEDICAL_EMERGENCY
}