package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class HurricaneEventManagement {
    private static HurricaneEventManagement hurricaneEventManagement;
    private ArrayList<HurricaneEvent> hurricaneEvents;

    private HurricaneEventManagement() {
        hurricaneEvents = DataLoader.getHurricaneEvents();
    }

    public static HurricaneEventManagement getInstance() {
        if(hurricaneEventManagement == null){
            hurricaneEventManagement = new HurricaneEventManagement();
        }
        return hurricaneEventManagement;
    }

    public HurricaneEvent getHurricaneEvent(UUID id) {
        for(HurricaneEvent event : hurricaneEvents) {
            if (event.getId().equals(id)) {
                return event;
            }
        }
        return null;
    }

    public void addHurricaneEvent(HurricaneEvent event) {
        hurricaneEvents.add(event);
    }

    public void removeHurricaneEvent(UUID id) {
        hurricaneEvents.removeIf(event -> event.getId().equals(id));
    }

}
