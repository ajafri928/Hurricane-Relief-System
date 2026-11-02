package com.model;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 * Unit tests for ShelterManagement.
 *
 * ShelterManagement is a singleton that loads the real Shelters.json at
 * startup, so these tests cannot assume an empty list. Instead they add
 * shelters with a capacity far larger than any real one and then filter
 * with a group size no real shelter could hold. That isolates the test
 * shelters from the loaded data without touching the file.
 *
 * save() is deliberately not tested, because it writes to the real
 * Shelters.json and would corrupt the demo data.
 *
 * @author Sahil
 */
public class ShelterManagementTest {

    /** Larger than any real shelter, so only test shelters can hold it. */
    private static final int HUGE_GROUP = 90000;

    private final ArrayList<UUID> added = new ArrayList<>();

    /** Removes every shelter this test added, so tests stay independent. */
    @After
    public void removeTestShelters() {
        for (UUID id : added) {
            ShelterManagement.getInstance().removeShelter(id);
        }
        added.clear();
    }

    /** Adds a test shelter and records its id for cleanup. */
    private Shelter addShelter(String name, int capacity, boolean petFriendly,
                               boolean medicalStaff) {
        UUID id = UUID.randomUUID();
        Location loc = new Location("1 Cooper St", "Charleston", "SC", "29403");
        Shelter s = new Shelter(id, name, loc, capacity, 0,
                petFriendly, true, medicalStaff, false, "OPEN");
        ShelterManagement.getInstance().addShelter(s);
        added.add(id);
        return s;
    }

    private Location charleston() {
        return new Location("18 Bogard St", "Charleston", "SC", "29403");
    }

    // ------------------------------------------------------------------
    // getInstance
    // ------------------------------------------------------------------

    /**
     * The singleton must hand back the same object every time, or two parts
     * of the program would each hold their own shelter list and changes made
     * through one would be invisible to the other.
     */
    @Test
    public void getInstanceReturnsTheSameObjectEveryTime() {
        assertSame(ShelterManagement.getInstance(), ShelterManagement.getInstance());
    }

    /**
     * The constructor loads from DataLoader rather than starting empty. If it
     * started empty, every shelter search would find nothing and a later save
     * would overwrite the JSON file with an empty array.
     */
    @Test
    public void getInstanceLoadsSheltersFromTheDataFile() {
        assertFalse(ShelterManagement.getInstance().getShelters().isEmpty());
    }

    /** Callers loop over the result directly, so it must never be null. */
    @Test
    public void getSheltersNeverReturnsNull() {
        assertNotNull(ShelterManagement.getInstance().getShelters());
    }

    // ------------------------------------------------------------------
    // addShelter and removeShelter
    // ------------------------------------------------------------------

    /** A newly added shelter must be visible to every later lookup. */
    @Test
    public void addShelterMakesTheShelterFindable() {
        Shelter s = addShelter("Unit Test Shelter", HUGE_GROUP + 10, true, true);
        assertTrue(ShelterManagement.getInstance().getShelters().contains(s));
    }

    /** Adding must grow the list by exactly one, not replace or duplicate. */
    @Test
    public void addShelterGrowsTheListByOne() {
        int before = ShelterManagement.getInstance().getShelters().size();
        addShelter("Unit Test Shelter", HUGE_GROUP + 10, true, true);
        assertEquals(before + 1, ShelterManagement.getInstance().getShelters().size());
    }

    /** Removal is by id, and the shelter must be gone afterwards. */
    @Test
    public void removeShelterDeletesTheMatchingShelter() {
        Shelter s = addShelter("Doomed Shelter", HUGE_GROUP + 10, true, true);
        ShelterManagement.getInstance().removeShelter(s.getId());
        added.remove(s.getId());
        assertFalse(ShelterManagement.getInstance().getShelters().contains(s));
    }

    /**
     * Removing an id that is not present must leave the list untouched rather
     * than throwing or deleting something else. This matters because the
     * facade passes ids that come from user input.
     */
    @Test
    public void removeShelterWithUnknownIdChangesNothing() {
        int before = ShelterManagement.getInstance().getShelters().size();
        ShelterManagement.getInstance().removeShelter(UUID.randomUUID());
        assertEquals(before, ShelterManagement.getInstance().getShelters().size());
    }

    /** Removing one shelter must not disturb the others. */
    @Test
    public void removeShelterLeavesOtherSheltersAlone() {
        Shelter keep = addShelter("Keep Me", HUGE_GROUP + 10, true, true);
        Shelter drop = addShelter("Drop Me", HUGE_GROUP + 10, true, true);
        ShelterManagement.getInstance().removeShelter(drop.getId());
        added.remove(drop.getId());
        assertTrue(ShelterManagement.getInstance().getShelters().contains(keep));
    }

    // ------------------------------------------------------------------
    // getAvailableShelters
    // ------------------------------------------------------------------

    /**
     * The normal case. A shelter with more open spots than the group needs
     * must appear in the results.
     */
    @Test
    public void availableSheltersIncludesAShelterWithEnoughRoom() {
        Shelter s = addShelter("Roomy", HUGE_GROUP + 100, true, true);
        assertTrue(ShelterManagement.getInstance()
                .getAvailableShelters(HUGE_GROUP).contains(s));
    }

    /**
     * The boundary. The filter is >=, so a shelter with exactly enough space
     * must qualify. An off-by-one here would turn away families the shelter
     * could actually hold.
     */
    @Test
    public void availableSheltersIncludesAShelterWithExactlyEnoughRoom() {
        Shelter s = addShelter("Exact Fit", HUGE_GROUP, true, true);
        assertTrue(ShelterManagement.getInstance()
                .getAvailableShelters(HUGE_GROUP).contains(s));
    }

    /** A shelter one spot short must be excluded. */
    @Test
    public void availableSheltersExcludesAShelterOneSpotShort() {
        Shelter s = addShelter("One Short", HUGE_GROUP - 1, true, true);
        assertFalse(ShelterManagement.getInstance()
                .getAvailableShelters(HUGE_GROUP).contains(s));
    }

    /**
     * When no shelter can hold the group the method must return an empty list
     * rather than null, because the facade loops over the result.
     */
    @Test
    public void availableSheltersReturnsEmptyListWhenNothingHasRoom() {
        ArrayList<Shelter> result = ShelterManagement.getInstance()
                .getAvailableShelters(HUGE_GROUP);
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    /**
     * Asking for zero people should match everything, since every shelter has
     * at least zero open spots. Confirms the comparison is not strict.
     */
    @Test
    public void availableSheltersWithZeroPeopleReturnsEveryShelter() {
        ShelterManagement m = ShelterManagement.getInstance();
        assertEquals(m.getShelters().size(), m.getAvailableShelters(0).size());
    }

    // ------------------------------------------------------------------
    // findNearestAvailable
    // ------------------------------------------------------------------

    /**
     * When exactly one shelter qualifies the method short circuits and returns
     * it without calling the map service. This is the cheapest path and the
     * one these tests rely on to stay fast and offline.
     */
    @Test
    public void findNearestReturnsTheOnlyQualifyingShelter() {
        Shelter s = addShelter("Only Option", HUGE_GROUP + 10, true, true);
        assertSame(s, ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * The error case. When nothing qualifies the method returns null, and the
     * caller is expected to handle it rather than receive an empty shelter.
     */
    @Test
    public void findNearestReturnsNullWhenNothingQualifies() {
        assertNull(ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * The Carter family refuses to leave their dog, so a shelter that turns
     * pets away must be filtered out even when it has plenty of room.
     */
    @Test
    public void findNearestSkipsSheltersThatRefusePetsWhenPetsAreNeeded() {
        addShelter("No Pets Allowed", HUGE_GROUP + 10, false, true);
        assertNull(ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * Sophie needs medical assistance, so a shelter without medical staff must
     * be filtered out regardless of its capacity or pet policy.
     */
    @Test
    public void findNearestSkipsSheltersWithoutMedicalStaffWhenMedicalIsNeeded() {
        addShelter("No Nurse", HUGE_GROUP + 10, true, false);
        assertNull(ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * A shelter that fails both filters must still be excluded. Guards against
     * an implementation that treats the two conditions as an either/or.
     */
    @Test
    public void findNearestSkipsSheltersThatFailBothFilters() {
        addShelter("Neither", HUGE_GROUP + 10, false, false);
        assertNull(ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * When no filters are requested a shelter lacking both features is still a
     * valid match. Confirms the filters are optional rather than always on.
     */
    @Test
    public void findNearestAcceptsAnyShelterWhenNoFiltersAreRequested() {
        Shelter s = addShelter("Plain Shelter", HUGE_GROUP + 10, false, false);
        assertSame(s, ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, false, false));
    }

    /**
     * Capacity is checked before the feature filters, so a shelter that has
     * every requested feature but no room must still be rejected.
     */
    @Test
    public void findNearestRejectsAQualifyingShelterThatIsTooSmall() {
        addShelter("Perfect But Full", HUGE_GROUP - 1, true, true);
        assertNull(ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true));
    }

    /**
     * With more than one candidate the method consults the map service to pick
     * the closest by driving distance. It must return one of the candidates
     * rather than null, even if the service is unreachable, because there is a
     * fallback to the first qualifying shelter.
     */
    @Test
    public void findNearestReturnsOneOfTheCandidatesWhenSeveralQualify() {
        Shelter a = addShelter("Candidate A", HUGE_GROUP + 10, true, true);
        Shelter b = addShelter("Candidate B", HUGE_GROUP + 10, true, true);
        Shelter chosen = ShelterManagement.getInstance()
                .findNearestAvailable(charleston(), HUGE_GROUP, true, true);
        assertTrue(chosen == a || chosen == b);
    }
}
