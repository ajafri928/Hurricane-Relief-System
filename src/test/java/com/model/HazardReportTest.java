package com.model;

import java.time.LocalDateTime;

import org.junit.Test;

import com.model.HazardCategory;
import com.model.HazardReport;

import static org.junit.Assert.*;

public class HazardReportTest {

    @Test
    public void basicConstructorCreatesUnverifiedHazardReport() {
        HazardReport report = new HazardReport(
                HazardCategory.FLOODING, null, "Resident");

        assertNotNull(report.getId());
        assertEquals(HazardCategory.FLOODING, report.getType());
        assertNull(report.getLocation());
        assertEquals("Resident", report.getSource());
        assertFalse(report.isVerified());
        assertNotNull(report.getTimeStamp());
    }

    @Test
    public void fullConstructorKeepsProvidedValues() {
        LocalDateTime timeStamp = LocalDateTime.of(2026, 11, 1, 10, 30);

        HazardReport report = new HazardReport(
                HazardCategory.POWER_OUTAGE, null, true,
                "Emergency Operations Center", timeStamp);

        assertNotNull(report.getId());
        assertEquals(HazardCategory.POWER_OUTAGE, report.getType());
        assertNull(report.getLocation());
        assertEquals("Emergency Operations Center", report.getSource());
        assertTrue(report.isVerified());
        assertEquals(timeStamp, report.getTimeStamp());
    }

    @Test
    public void verifyChangesReportToVerified() {
        HazardReport report = new HazardReport(
                HazardCategory.BLOCKED_ROAD, null, "Resident");

        assertFalse(report.isVerified());

        report.verify();

        assertTrue(report.isVerified());
    }
    @Test
    public void basicConstructorCreatesIndependentUnverifiedReports() {
        HazardReport first = new HazardReport(
                HazardCategory.FLOODING, null, "Resident");

        HazardReport second = new HazardReport(
                HazardCategory.FLOODING, null, "Resident");

        assertNotNull(first.getId());
        assertNotNull(second.getId());
        assertNotEquals(first.getId(), second.getId());
        assertFalse(first.isVerified());
        assertFalse(second.isVerified());
    }
    @Test
    public void verifyCanBeCalledMoreThanOnce() {
        HazardReport report = new HazardReport(
                HazardCategory.BLOCKED_ROAD, null, "Resident");

        report.verify();
        report.verify();

        assertTrue(report.isVerified());
    }
}