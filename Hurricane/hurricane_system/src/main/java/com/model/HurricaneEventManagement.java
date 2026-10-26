package com.model;

import java.util.ArrayList;
import java.util.UUID;

/** Singleton that stores and manages hurricane events. */
public class HurricaneEventManagement {
    private static HurricaneEventManagement hurricaneEventManagement;
    private ArrayList<HurricaneEvent> hurricaneEvents;

    private HurricaneEventManagement() {
        hurricaneEvents = DataLoader.getHurricaneEvents();
    }

    /** @return the single instance */
    public static HurricaneEventManagement getInstance() {
        if(hurricaneEventManagement == null){
            hurricaneEventManagement = new HurricaneEventManagement();
        }
        return hurricaneEventManagement;
    }

    /** @param id event id @return the matching event, or null */
    public HurricaneEvent getHurricaneEvent(UUID id) {
        for(HurricaneEvent event : hurricaneEvents) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    /** @param event event to add */
    public void addHurricaneEvent(HurricaneEvent event) {
        hurricaneEvents.add(event);
    }

    /** @param id id of event to remove */
    public void removeHurricaneEvent(UUID id) {
        hurricaneEvents.removeIf(event -> event.getId().equals(id));
    }

}