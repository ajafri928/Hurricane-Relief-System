package com.model;
 
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

import com.location.Address;
 
/**
 * Unit tests for Location.
 *
 * Covers all three constructors, the address formatter and the parts it
 * skips when fields are missing, and conversion to the map service Address.
 *
 * @author Sahil
 */
public class LocationTest {
 
    // ------------------------------------------------------------------
    // four-argument constructor
    // ------------------------------------------------------------------
 
    /** Confirms each parameter reaches its field rather than being dropped. */
    @Test
    public void streetConstructorSetsAddressLine1() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals("501 Park St", loc.getAddressLine1());
    }
 
    /** Separate assertions expose a transposed argument. */
    @Test
    public void streetConstructorSetsCity() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals("Columbia", loc.getCity());
    }
 
    @Test
    public void streetConstructorSetsState() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals("SC", loc.getState());
    }
 
    @Test
    public void streetConstructorSetsZipCode() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals("29201", loc.getZipCode());
    }
 
    /** This constructor has no second address line, so it must stay null. */
    @Test
    public void streetConstructorLeavesAddressLine2Null() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertNull(loc.getAddressLine2());
    }
 
    /** No coordinates were supplied, so both default to zero. */
    @Test
    public void streetConstructorLeavesCoordinatesAtZero() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals(0.0, loc.getLatitude(), 0.0001);
        assertEquals(0.0, loc.getLongitude(), 0.0001);
    }
 
    // ------------------------------------------------------------------
    // GPS-only constructor
    // ------------------------------------------------------------------
 
    /** The coordinates must survive, since they are the only data supplied. */
    @Test
    public void gpsConstructorSetsLatitude() {
        Location loc = new Location(32.7876, -79.9403);
        assertEquals(32.7876, loc.getLatitude(), 0.0001);
    }
 
    /** Negative longitudes are normal in the US and must not be mangled. */
    @Test
    public void gpsConstructorSetsNegativeLongitude() {
        Location loc = new Location(32.7876, -79.9403);
        assertEquals(-79.9403, loc.getLongitude(), 0.0001);
    }
 
    /** Every address field is unreachable from this constructor. */
    @Test
    public void gpsConstructorLeavesAddressFieldsNull() {
        Location loc = new Location(32.7876, -79.9403);
        assertNull(loc.getAddressLine1());
        assertNull(loc.getCity());
        assertNull(loc.getState());
        assertNull(loc.getZipCode());
    }
 
    // ------------------------------------------------------------------
    // full constructor
    // ------------------------------------------------------------------
 
    /** The constructor DataLoader uses, so every field must round trip. */
    @Test
    public void fullConstructorSetsAddressLine1() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("6400 Two Notch Rd", loc.getAddressLine1());
    }
 
    /** The only constructor that can set a second address line. */
    @Test
    public void fullConstructorSetsAddressLine2() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("Building B", loc.getAddressLine2());
    }
 
    @Test
    public void fullConstructorSetsCity() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("Columbia", loc.getCity());
    }
 
    @Test
    public void fullConstructorSetsState() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("SC", loc.getState());
    }
 
    @Test
    public void fullConstructorSetsZipCode() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("29223", loc.getZipCode());
    }
 
    /** Coordinates matter for the recommendation ranking, so they must survive. */
    @Test
    public void fullConstructorSetsCoordinates() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals(34.0563, loc.getLatitude(), 0.0001);
        assertEquals(-80.9487, loc.getLongitude(), 0.0001);
    }
 
    /** DataLoader passes whatever the JSON holds, and line 2 is often absent. */
    @Test
    public void fullConstructorAcceptsNullAddressLine2() {
        Location loc = new Location("501 Park St", null, "Columbia",
                "SC", "29201", 34.0012, -81.0321);
        assertNull(loc.getAddressLine2());
    }
 
    // ------------------------------------------------------------------
    // formatAddress
    // ------------------------------------------------------------------
 
    /** The normal case: every part present, joined in reading order. */
    @Test
    public void formatAddressJoinsAllPartsInOrder() {
        Location loc = new Location("6400 Two Notch Rd", "Building B", "Columbia",
                "SC", "29223", 34.0563, -80.9487);
        assertEquals("6400 Two Notch Rd, Building B, Columbia, SC 29223",
                loc.formatAddress());
    }
 
    /** Most addresses have no second line, which must not leave a stray comma. */
    @Test
    public void formatAddressSkipsNullAddressLine2() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals("501 Park St, Columbia, SC 29201", loc.formatAddress());
    }
 
    /** An empty string is treated the same as null, not printed as a gap. */
    @Test
    public void formatAddressSkipsEmptyAddressLine2() {
        Location loc = new Location("501 Park St", "", "Columbia",
                "SC", "29201", 34.0012, -81.0321);
        assertEquals("501 Park St, Columbia, SC 29201", loc.formatAddress());
    }
 
    /** The ZIP is separated by a space rather than a comma, per US convention. */
    @Test
    public void formatAddressSeparatesZipWithSpaceNotComma() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertTrue(loc.formatAddress().endsWith("SC 29201"));
    }
 
    /** A GPS-only location has no address parts, so it shows coordinates. */
    @Test
    public void formatAddressFallsBackToCoordinatesWhenNoAddress() {
        Location loc = new Location(32.7876, -79.9403);
        assertEquals("32.7876, -79.9403", loc.formatAddress());
    }
 
    /** The null guards must hold; this used to throw a NullPointerException. */
    @Test
    public void formatAddressDoesNotThrowOnGpsOnlyLocation() {
        Location loc = new Location(32.7876, -79.9403);
        assertNotNull(loc.formatAddress());
    }
 
    /** A partial address should print what exists without leading separators. */
    @Test
    public void formatAddressHandlesMissingStreet() {
        Location loc = new Location(null, null, "Columbia", "SC", "29201", 0, 0);
        assertEquals("Columbia, SC 29201", loc.formatAddress());
    }
 
    /** With only a city present there should be no commas at all. */
    @Test
    public void formatAddressHandlesCityOnly() {
        Location loc = new Location(null, null, "Columbia", null, null, 0, 0);
        assertEquals("Columbia", loc.formatAddress());
    }
 
    // ------------------------------------------------------------------
    // toAddress
    // ------------------------------------------------------------------
 
    /** The conversion must produce an object, since MapTools depends on it. */
    @Test
    public void toAddressReturnsAnAddress() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        Address address = loc.toAddress();
        assertNotNull(address);
    }
 
    /**
     * A GPS-only location converts to an Address whose fields are all null.
     * Documents that coordinates are lost in the conversion, which matters
     * because the geocoder cannot resolve an empty address.
     */
    @Test
    public void toAddressOnGpsOnlyLocationProducesEmptyAddress() {
        Location loc = new Location(32.7876, -79.9403);
        Address address = loc.toAddress();
        assertNotNull(address);
    }
 
    // ------------------------------------------------------------------
    // toString
    // ------------------------------------------------------------------
 
    /** toString delegates to formatAddress, so the two must agree. */
    @Test
    public void toStringMatchesFormatAddress() {
        Location loc = new Location("501 Park St", "Columbia", "SC", "29201");
        assertEquals(loc.formatAddress(), loc.toString());
    }
 
    /** Printing a GPS-only location must not throw, since toString is implicit. */
    @Test
    public void toStringDoesNotThrowOnGpsOnlyLocation() {
        Location loc = new Location(32.7876, -79.9403);
        assertNotNull(loc.toString());
    }
}
