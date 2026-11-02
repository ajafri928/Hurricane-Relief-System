package com.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.lang.reflect.Field;
import java.util.UUID;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class HurricaneEventManagementTest {

    private static final String HELENA_ID = "777e4567-e89b-12d3-a456-426614174777";
    private static final String IAN_ID = "888e4567-e89b-12d3-a456-426614174888";

    private static final String TWO_EVENTS_JSON = "["
            + "{\"id\":\"" + HELENA_ID + "\",\"name\":\"Hurricane Helena\",\"category\":4,\"status\":\"ACTIVE\"},"
            + "{\"id\":\"" + IAN_ID + "\",\"name\":\"Hurricane Ian\",\"category\":3,\"status\":\"ENDED\"}"
            + "]";

    private HurricaneEventManagement eventManagement;

    private void resetSingleton() {
        try {
            Field field = HurricaneEventManagement.class.getDeclaredField("hurricaneEventManagement");
            field.setAccessible(true);
            field.set(null, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not reset HurricaneEventManagement", e);
        }
    }

    @Before
    public void setUp() {
        TestDataFiles.backUpAll();
        TestDataFiles.write(DataConstants.EVENT_FILE_NAME, TWO_EVENTS_JSON);
        resetSingleton();
        eventManagement = HurricaneEventManagement.getInstance();
    }

    @After
    public void tearDown() {
        resetSingleton();
        TestDataFiles.restoreAll();
    }

    @Test
    public void getInstanceAlwaysReturnsTheSameObject() {
        assertSame(eventManagement, HurricaneEventManagement.getInstance());
    }

    @Test
    public void getInstanceLoadsEventsFromTheFile() {
        assertEquals("Hurricane Helena", eventManagement.getHurricaneEvent(UUID.fromString(HELENA_ID)).getName());
    }

    @Test
    public void getInstanceWithMissingFileStartsEmpty() {
        TestDataFiles.delete(DataConstants.EVENT_FILE_NAME);
        resetSingleton();
        assertNull(HurricaneEventManagement.getInstance().getHurricaneEvent(UUID.fromString(HELENA_ID)));
    }

    @Test
    public void getHurricaneEventFindsMatchingEvent() {
        assertEquals("Hurricane Ian", eventManagement.getHurricaneEvent(UUID.fromString(IAN_ID)).getName());
    }

    @Test
    public void getHurricaneEventReturnsNullForUnknownId() {
        assertNull(eventManagement.getHurricaneEvent(UUID.randomUUID()));
    }

    @Test
    public void getHurricaneEventReturnsNullForNullId() {
        assertNull(eventManagement.getHurricaneEvent(null));
    }

    @Test
    public void addHurricaneEventCanBeFoundAfterAdding() {
        HurricaneEvent milton = new HurricaneEvent(UUID.randomUUID(), "Hurricane Milton", 5, "ACTIVE");
        eventManagement.addHurricaneEvent(milton);
        assertSame(milton, eventManagement.getHurricaneEvent(milton.getId()));
    }

    @Test
    public void addHurricaneEventDoesNotReplaceExistingEvents() {
        eventManagement.addHurricaneEvent(new HurricaneEvent(UUID.randomUUID(), "Hurricane Milton", 5, "ACTIVE"));
        assertNotNull(eventManagement.getHurricaneEvent(UUID.fromString(HELENA_ID)));
    }

    @Test
    public void addingNullEventDoesNotBreakLookups() {
        // A null event should be ignored, not stored and crash every later lookup.
        eventManagement.addHurricaneEvent(null);
        assertNull(eventManagement.getHurricaneEvent(UUID.randomUUID()));
    }

    @Test
    public void removeHurricaneEventDeletesThatEvent() {
        eventManagement.removeHurricaneEvent(UUID.fromString(HELENA_ID));
        assertNull(eventManagement.getHurricaneEvent(UUID.fromString(HELENA_ID)));
    }

    @Test
    public void removeHurricaneEventLeavesOtherEventsAlone() {
        eventManagement.removeHurricaneEvent(UUID.fromString(HELENA_ID));
        assertNotNull(eventManagement.getHurricaneEvent(UUID.fromString(IAN_ID)));
    }

    @Test
    public void removeHurricaneEventWithUnknownIdRemovesNothing() {
        eventManagement.removeHurricaneEvent(UUID.randomUUID());
        assertNotNull(eventManagement.getHurricaneEvent(UUID.fromString(IAN_ID)));
    }

    @Test
    public void removeHurricaneEventWithNullIdRemovesNothing() {
        eventManagement.removeHurricaneEvent(null);
        assertNotNull(eventManagement.getHurricaneEvent(UUID.fromString(HELENA_ID)));
    }
}