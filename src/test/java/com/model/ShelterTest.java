package com.model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Unit tests for Shelter.
 *
 * Covers both constructors, the two check-in and check-out paths, the
 * low-stock filter, and the space calculation.
 *
 * @author Sahil
 */
public class ShelterTest {

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    /** Builds a shelter with the given capacity and occupancy, pet friendly. */
    private Shelter shelter(int capacity, int occupancy) {
        return new Shelter(UUID.randomUUID(), "Test Shelter", address(),
                capacity, occupancy, true, true, true, false, "OPEN");
    }

    /** Builds a shelter that refuses pets. */
    private Shelter shelterWithoutPets(int capacity, int occupancy) {
        return new Shelter(UUID.randomUUID(), "No Pets Shelter", address(),
                capacity, occupancy, false, true, true, false, "OPEN");
    }

    private Location address() {
        return new Location("1 Cooper St", "Charleston", "SC", "29403");
    }

    private ArrayList<Person> people(int howMany) {
        ArrayList<Person> list = new ArrayList<>();
        for (int i = 0; i < howMany; i++) {
            list.add(new Person("First" + i, "Last" + i, 30 + i));
        }
        return list;
    }

    private ArrayList<Pet> noPets() {
        return new ArrayList<Pet>();
    }

    private ArrayList<Pet> onePet() {
        ArrayList<Pet> pets = new ArrayList<>();
        pets.add(new Pet("Buddy", "Dog"));
        return pets;
    }

    // ------------------------------------------------------------------
    // four-argument constructor
    // ------------------------------------------------------------------

    /** Capacity drives every space calculation, so it must reach the field. */
    @Test
    public void shortConstructorSetsCapacity() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertEquals(250, s.getCapacity());
    }

    /** This constructor never touches occupancy; it must default to zero. */
    @Test
    public void shortConstructorStartsAtZeroOccupancy() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertEquals(0, s.getOccupancy());
    }

    /** An uninitialized list would make the first checkIn throw an NPE. */
    @Test
    public void shortConstructorInitializesOccupantsList() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNotNull(s.getOccupants());
        assertTrue(s.getOccupants().isEmpty());
    }

    /** checkIn also writes to the pet list, so it must exist. */
    @Test
    public void shortConstructorInitializesPetsOnSiteList() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNotNull(s.getPetsOnSite());
        assertTrue(s.getPetsOnSite().isEmpty());
    }

    /** addAmenity writes here. */
    @Test
    public void shortConstructorInitializesAmenitiesList() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNotNull(s.getAmenities());
        assertTrue(s.getAmenities().isEmpty());
    }

    /** getLowStockResources loops over this list, so a null would throw. */
    @Test
    public void shortConstructorInitializesResourcesList() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNotNull(s.getShelterResources());
        assertTrue(s.getShelterResources().isEmpty());
    }

    /** Documents that a shelter built this way has no status at all. */
    @Test
    public void shortConstructorLeavesOperationalStatusNull() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNull(s.getOperationalStatus());
    }

    /** All four flags default false, hiding this shelter from filtered searches. */
    @Test
    public void shortConstructorLeavesBooleanFlagsFalse() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertFalse(s.isPetFriendly());
        assertFalse(s.isAccessible());
        assertFalse(s.hasMedicalStaff());
        assertFalse(s.hasVetStaff());
    }

    // ------------------------------------------------------------------
    // ten-argument constructor
    // ------------------------------------------------------------------

    /** DataLoader uses this; a lost occupancy makes every shelter look empty. */
    @Test
    public void fullConstructorSetsOccupancy() {
        Shelter s = shelter(250, 112);
        assertEquals(112, s.getOccupancy());
    }

    /** Four consecutive booleans are easy to transpose, so check each one. */
    @Test
    public void fullConstructorSetsPetFriendly() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250, 0,
                true, false, false, false, "OPEN");
        assertTrue(s.isPetFriendly());
        assertFalse(s.isAccessible());
    }

    @Test
    public void fullConstructorSetsAccessible() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250, 0,
                false, true, false, false, "OPEN");
        assertTrue(s.isAccessible());
        assertFalse(s.isPetFriendly());
    }

    @Test
    public void fullConstructorSetsMedicalStaff() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250, 0,
                false, false, true, false, "OPEN");
        assertTrue(s.hasMedicalStaff());
        assertFalse(s.hasVetStaff());
    }

    @Test
    public void fullConstructorSetsVetStaff() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250, 0,
                false, false, false, true, "OPEN");
        assertTrue(s.hasVetStaff());
        assertFalse(s.hasMedicalStaff());
    }

    /** The last parameter, where truncation errors tend to land. */
    @Test
    public void fullConstructorSetsOperationalStatus() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250, 0,
                true, true, true, true, "AT_CAPACITY");
        assertEquals("AT_CAPACITY", s.getOperationalStatus());
    }

    /** DataLoader calls addAmenity after construction, so lists must exist. */
    @Test
    public void fullConstructorAlsoInitializesCollections() {
        Shelter s = shelter(250, 112);
        assertNotNull(s.getOccupants());
        assertNotNull(s.getPetsOnSite());
        assertNotNull(s.getAmenities());
        assertNotNull(s.getShelterResources());
    }

    // ------------------------------------------------------------------
    // checkIn(persons, pets)
    // ------------------------------------------------------------------

    /** Felix must see the occupant list grow; this tests the list. */
    @Test
    public void groupCheckInAddsEveryPersonToOccupantList() {
        Shelter s = shelter(100, 0);
        s.checkIn(people(3), noPets());
        assertEquals(3, s.getOccupants().size());
    }

    /** The counter is separate state from the list and can break alone. */
    @Test
    public void groupCheckInRaisesOccupancyByPersonCount() {
        Shelter s = shelter(100, 10);
        s.checkIn(people(3), noPets());
        assertEquals(13, s.getOccupancy());
    }

    /** The brief is explicit that a pet is not a person. */
    @Test
    public void groupCheckInDoesNotCountPetsTowardOccupancy() {
        Shelter s = shelter(100, 0);
        s.checkIn(people(1), onePet());
        assertEquals(1, s.getOccupancy());
    }

    /** Confirms the pet lands somewhere rather than being silently dropped. */
    @Test
    public void groupCheckInRecordsPetsInPetsOnSite() {
        Shelter s = shelter(100, 0);
        s.checkIn(people(1), onePet());
        assertEquals(1, s.getPetsOnSite().size());
    }

    /** Most families arrive without animals, so this is the common path. */
    @Test
    public void groupCheckInAcceptsEmptyPetList() {
        Shelter s = shelter(100, 0);
        s.checkIn(people(2), noPets());
        assertEquals(2, s.getOccupancy());
        assertTrue(s.getPetsOnSite().isEmpty());
    }

    /** The guard boundary: a group that exactly fills the space is allowed. */
    @Test
    public void groupCheckInFillsShelterToExactCapacity() {
        Shelter s = shelter(10, 7);
        s.checkIn(people(3), noPets());
        assertEquals(10, s.getOccupancy());
        assertEquals(0, s.getAvailableSpace());
    }

    /** The error case: a shelter must refuse more people than it can hold. */
    @Test(expected = IllegalStateException.class)
    public void groupCheckInThrowsWhenGroupExceedsCapacity() {
        Shelter s = shelter(10, 8);
        s.checkIn(people(3), noPets());
    }

    /** The Carters refuse to leave Buddy, so this must fail loudly. */
    @Test(expected = IllegalStateException.class)
    public void groupCheckInThrowsWhenPetsBroughtToNonPetFriendlyShelter() {
        Shelter s = shelterWithoutPets(100, 0);
        s.checkIn(people(1), onePet());
    }

    /** The guard must fire before any mutation, or state is left corrupted. */
    @Test
    public void groupCheckInDoesNotChangeOccupancyWhenItThrows() {
        Shelter s = shelter(10, 8);
        try {
            s.checkIn(people(3), noPets());
            fail("expected IllegalStateException");
        } catch (IllegalStateException expected) {
            assertEquals(8, s.getOccupancy());
            assertTrue(s.getOccupants().isEmpty());
        }
    }

    // ------------------------------------------------------------------
    // checkOut(persons, pets)
    // ------------------------------------------------------------------

    /** The inverse of check-in, needed when a family leaves. */
    @Test
    public void groupCheckOutRemovesPeopleFromOccupantList() {
        Shelter s = shelter(100, 0);
        ArrayList<Person> family = people(3);
        s.checkIn(family, noPets());
        s.checkOut(family, noPets());
        assertTrue(s.getOccupants().isEmpty());
    }

    /** The method decrements only inside the remove-success branch. */
    @Test
    public void groupCheckOutLowersOccupancyByPersonCount() {
        Shelter s = shelter(100, 10);
        ArrayList<Person> family = people(3);
        s.checkIn(family, noPets());
        s.checkOut(family, noPets());
        assertEquals(10, s.getOccupancy());
    }

    /** Pets leave with their family and must not linger on the roster. */
    @Test
    public void groupCheckOutRemovesPetsFromSite() {
        Shelter s = shelter(100, 0);
        ArrayList<Person> family = people(1);
        ArrayList<Pet> pets = onePet();
        s.checkIn(family, pets);
        s.checkOut(family, pets);
        assertTrue(s.getPetsOnSite().isEmpty());
    }

    /** Guards against occupancy drifting from a mistaken check-out. */
    @Test
    public void groupCheckOutIgnoresPeopleWhoWereNeverCheckedIn() {
        Shelter s = shelter(100, 20);
        s.checkOut(people(3), noPets());
        assertEquals(20, s.getOccupancy());
    }

    /** The degenerate case must not produce a negative occupancy. */
    @Test
    public void groupCheckOutOfEmptyShelterLeavesOccupancyAtZero() {
        Shelter s = shelter(100, 0);
        s.checkOut(people(2), noPets());
        assertEquals(0, s.getOccupancy());
    }

    // ------------------------------------------------------------------
    // checkIn(String) and checkOut(String)
    // ------------------------------------------------------------------

    /** Documents the behaviour of the single-person path. */
    @Test
    public void qrCheckInRaisesOccupancyByOne() {
        Shelter s = shelter(100, 5);
        s.checkIn("QR-123");
        assertEquals(6, s.getOccupancy());
    }

    /** The parameter is never read, so two different codes behave identically. */
    @Test
   public void qrCheckInShouldRecordWhoCheckedIn() {
        Shelter s = shelter(100, 0);
        s.checkIn("QR-123");
        assertFalse("the shelter must record which person checked in",
                s.getOccupants().isEmpty());
    }

    /** Reveals that the QR path and the group path track different state. */
    @Test
    public void qrCheckInShouldAddThePersonToTheOccupantList() {
        Shelter s = shelter(100, 0);
        s.checkIn("QR-123");
        assertEquals("occupancy and the occupant list must agree",
                s.getOccupancy(), s.getOccupants().size());
    }

    /** Unlike the group version this path has no capacity guard. */
    @Test
    public void qrCheckInShouldNotExceedCapacity() {
        Shelter s = shelter(2, 2);
        s.checkIn("QR-123");
        assertEquals("a full shelter must not accept another person",
                2, s.getOccupancy());
    }

    /** The same missing guard in the other direction. */
    @Test
    public void qrCheckOutShouldNotDriveOccupancyNegative() {
        Shelter s = shelter(100, 0);
        s.checkOut("QR-123");
        assertEquals("occupancy must never fall below zero",
                0, s.getOccupancy());
    }
    // ------------------------------------------------------------------
    // getLowStockResources
    // ------------------------------------------------------------------

    /** The normal filtering case with a mix of low and healthy supplies. */
    @Test
    public void lowStockReturnsOnlyResourcesAtOrBelowThreshold() {
        Shelter s = shelter(100, 0);
        s.addResource(new ShelterResource("Blankets", 12, null, 40, "individual"));
        s.addResource(new ShelterResource("Water", 1200, null, 300, "cases"));
        assertEquals(1, s.getLowStockResources().size());
    }

    /** Confirms the filter excludes rather than returning everything. */
    @Test
    public void lowStockExcludesWellStockedResources() {
        Shelter s = shelter(100, 0);
        s.addResource(new ShelterResource("Water", 1200, null, 300, "cases"));
        assertTrue(s.getLowStockResources().isEmpty());
    }

    /** Callers loop over the result, so it must be empty rather than null. */
    @Test
    public void lowStockReturnsEmptyListWhenNothingIsLow() {
        Shelter s = shelter(100, 0);
        s.addResource(new ShelterResource("Water", 1200, null, 300, "cases"));
        assertNotNull(s.getLowStockResources());
    }

    /** The degenerate case on a fresh shelter, where the loop never runs. */
    @Test
    public void lowStockReturnsEmptyListWhenShelterHasNoResources() {
        Shelter s = shelter(100, 0);
        assertTrue(s.getLowStockResources().isEmpty());
    }

    /** Inherits checkStockLevel's boundary rather than reimplementing it. */
    @Test
    public void lowStockIncludesResourceExactlyAtThreshold() {
        Shelter s = shelter(100, 0);
        s.addResource(new ShelterResource("Cots", 40, null, 40, "individual"));
        assertEquals(1, s.getLowStockResources().size());
    }

    // ------------------------------------------------------------------
    // getAvailableSpace / getAvailableSpaces
    // ------------------------------------------------------------------

    /** The arithmetic the check-in guard and the shelter search depend on. */
    @Test
    public void availableSpaceIsCapacityMinusOccupancy() {
        Shelter s = shelter(250, 112);
        assertEquals(138, s.getAvailableSpace());
    }

    /** The boundary at which a shelter stops accepting people. */
    @Test
    public void availableSpaceIsZeroWhenShelterIsFull() {
        Shelter s = shelter(50, 50);
        assertEquals(0, s.getAvailableSpace());
    }

    /** Reachable through the unguarded QR path; the method does no clamping. */
    @Test
    public void availableSpaceGoesNegativeWhenOvercapacity() {
        Shelter s = shelter(50, 55);
        assertEquals(-5, s.getAvailableSpace());
    }

    /** The two methods are duplicates; this documents that they agree. */
    @Test
    public void availableSpacesMatchesAvailableSpace() {
        Shelter s = shelter(250, 112);
        assertEquals(s.getAvailableSpace(), s.getAvailableSpaces());
    }

    // ------------------------------------------------------------------
    // toString
    // ------------------------------------------------------------------

    /** Used in demo output, so the identifying field must appear. */
    @Test
    public void toStringIncludesTheShelterName() {
        Shelter s = new Shelter(UUID.randomUUID(), "Bull Street Shelter",
                address(), 250);
        assertTrue(s.toString().contains("Bull Street Shelter"));
    }

    /** toString is called implicitly in concatenation and must not throw. */
    @Test
    public void toStringDoesNotThrowOnAnEmptyShelter() {
        Shelter s = new Shelter(UUID.randomUUID(), "Gym", address(), 250);
        assertNotNull(s.toString());
    }
}
