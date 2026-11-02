package com.model;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for ShelterResource.
 *
 * Covers the low stock comparison and its boundaries, both constructors
 * and the defaults they leave behind, and the quantity guard.
 *
 * @author Sahil
 */
public class ShelterResourceTest {


    // checkStockLevel

    /** The normal low stock case: quantity has run down past the threshold. */
    @Test
    public void checkStockLevelTrueWhenQuantityBelowThreshold() {
        ShelterResource water = new ShelterResource("Water", 10, null, 50, "cases");
        assertTrue(water.checkStockLevel());
    }

    /** The boundary. Catches a strict less-than written where <= was meant. */
    @Test
    public void checkStockLevelTrueWhenQuantityExactlyAtThreshold() {
        ShelterResource water = new ShelterResource("Water", 50, null, 50, "cases");
        assertTrue(water.checkStockLevel());
    }

    /** The normal healthy case: a well stocked resource is not flagged. */
    @Test
    public void checkStockLevelFalseWhenQuantityAboveThreshold() {
        ShelterResource water = new ShelterResource("Water", 80, null, 50, "cases");
        assertFalse(water.checkStockLevel());
    }

    /** Zero is the lower bound and the most urgent case. */
    @Test
    public void checkStockLevelTrueWhenQuantityIsZero() {
        ShelterResource water = new ShelterResource("Water", 0, null, 50, "cases");
        assertTrue(water.checkStockLevel());
    }

    /** Degenerate case confirming <= from the other direction. */
    @Test
    public void checkStockLevelTrueWhenQuantityAndThresholdAreBothZero() {
        ShelterResource water = new ShelterResource("Water", 0, null, 0, "cases");
        assertTrue(water.checkStockLevel());
    }

    /** A zero threshold should mean never flag, not always flag. */
    @Test
    public void checkStockLevelFalseWhenThresholdIsZeroAndStockExists() {
        ShelterResource water = new ShelterResource("Water", 10, null, 0, "cases");
        assertFalse(water.checkStockLevel());
    }

    // ------------------------------------------------------------------
    // two-argument constructor
    // ------------------------------------------------------------------

    /** Confirms the parameter reaches its field, not just gets declared. */
    @Test
    public void twoArgConstructorSetsType() {
        ShelterResource cots = new ShelterResource("Cots", 25);
        assertEquals("Cots", cots.getType());
    }

    /** Same check for the second parameter. */
    @Test
    public void twoArgConstructorSetsQuantity() {
        ShelterResource cots = new ShelterResource("Cots", 25);
        assertEquals(25, cots.getQuantity());
    }

    /** Pins the unset default so a later change to it is caught. */
    @Test
    public void twoArgConstructorLeavesThresholdAtZero() {
        ShelterResource cots = new ShelterResource("Cots", 25);
        assertEquals(0, cots.getLowStockThreshold());
    }

    /** Documents that the short constructor cannot set a unit. */
    @Test
    public void twoArgConstructorLeavesUnitNull() {
        ShelterResource cots = new ShelterResource("Cots", 25);
        assertNull(cots.getUnit());
    }

    /** Matters because DataWriter calls toString() on this field. */
    @Test
    public void twoArgConstructorLeavesAvailableUntilNull() {
        ShelterResource cots = new ShelterResource("Cots", 25);
        assertNull(cots.getAvailableUntil());
    }

    /** Documents the consequence of the zero default: low stock never fires. */
    @Test
    public void resourceWithOneUnitLeftShouldReportLowStock() {
        ShelterResource cots = new ShelterResource("Cots", 1);
        assertTrue("a resource down to its last unit should be flagged low",
                cots.checkStockLevel());
    }

    // ------------------------------------------------------------------
    // five-argument constructor
    // ------------------------------------------------------------------

    /** One assertion per parameter so a failure names the exact field. */
    @Test
    public void fullConstructorSetsType() {
        LocalDateTime expiry = LocalDateTime.of(2026, 10, 15, 12, 0);
        ShelterResource meals = new ShelterResource("Meals", 620, expiry, 200, "boxes");
        assertEquals("Meals", meals.getType());
    }

    /** Separate tests also expose transposed arguments. */
    @Test
    public void fullConstructorSetsQuantity() {
        LocalDateTime expiry = LocalDateTime.of(2026, 10, 15, 12, 0);
        ShelterResource meals = new ShelterResource("Meals", 620, expiry, 200, "boxes");
        assertEquals(620, meals.getQuantity());
    }

    @Test
    public void fullConstructorSetsAvailableUntil() {
        LocalDateTime expiry = LocalDateTime.of(2026, 10, 15, 12, 0);
        ShelterResource meals = new ShelterResource("Meals", 620, expiry, 200, "boxes");
        assertEquals(expiry, meals.getAvailableUntil());
    }

    @Test
    public void fullConstructorSetsLowStockThreshold() {
        LocalDateTime expiry = LocalDateTime.of(2026, 10, 15, 12, 0);
        ShelterResource meals = new ShelterResource("Meals", 620, expiry, 200, "boxes");
        assertEquals(200, meals.getLowStockThreshold());
    }

    @Test
    public void fullConstructorSetsUnit() {
        LocalDateTime expiry = LocalDateTime.of(2026, 10, 15, 12, 0);
        ShelterResource meals = new ShelterResource("Meals", 620, expiry, 200, "boxes");
        assertEquals("boxes", meals.getUnit());
    }

    /** Expiry is optional, and DataLoader passes whatever the JSON holds. */
    @Test
    public void fullConstructorAcceptsNullAvailableUntil() {
        ShelterResource meals = new ShelterResource("Meals", 620, null, 200, "boxes");
        assertNull(meals.getAvailableUntil());
    }

    // ------------------------------------------------------------------
    // setQuantity
    // ------------------------------------------------------------------

    /** The normal path: a valid new value replaces the old one. */
    @Test
    public void setQuantityUpdatesTheQuantity() {
        ShelterResource water = new ShelterResource("Water", 100, null, 50, "cases");
        water.setQuantity(75);
        assertEquals(75, water.getQuantity());
    }

    /** Zero is the guard's boundary. Running out is legitimate. */
    @Test
    public void setQuantityAcceptsZero() {
        ShelterResource water = new ShelterResource("Water", 100, null, 50, "cases");
        water.setQuantity(0);
        assertEquals(0, water.getQuantity());
    }

    /** The error case. A negative inventory count is meaningless. */
    @Test(expected = IllegalArgumentException.class)
    public void setQuantityRejectsNegativeValues() {
        ShelterResource water = new ShelterResource("Water", 100, null, 50, "cases");
        water.setQuantity(-5);
    }

    /** Confirms checkStockLevel reads current state, not a cached value. */
    @Test
    public void loweringQuantityBelowThresholdTriggersLowStock() {
        ShelterResource water = new ShelterResource("Water", 100, null, 50, "cases");
        assertFalse(water.checkStockLevel());
        water.setQuantity(40);
        assertTrue(water.checkStockLevel());
    }
}
