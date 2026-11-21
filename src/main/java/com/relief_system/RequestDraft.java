package com.relief_system;

import java.util.LinkedHashSet;

import com.model.User;

public class RequestDraft{
    public static final int STEP_COUNT = 5;

    private static RequestDraft saved;
    private static String savedFor;

    public LinkedHashSet<String> needs = new LinkedHashSet<>();
    public String urgency;
    public String description = "";
    public int adults = 1;
    public int children = 0;
    public LinkedHashSet<String> specialNeeds = new LinkedHashSet<>();
    public String street = "";
    public String city = "";
    public String state = "";
    public String zip = "";
    public Double latitude;
    public Double longitude;
    public boolean atLocationNow = true;
    public String phone = "";
    public String contactPreference = "EITHER";
    public String backupContact = "";
    public boolean confirmed;
    public int step = 1;

    public static RequestDraft find(User user) {
        return user != null && user.getUserName().equals(savedFor) ? saved : null;
    }

    public static RequestDraft findOrCreate(User user) {
        RequestDraft draft = find(user);
        if (draft == null) {
            draft = new RequestDraft();
            if (user != null && user.getContactPhone() != null) {
                draft.phone = user.getContactPhone();
            }
        }
        return draft;
    }

    public void save(User user) {
        if (user != null) {
            saved = this;
            savedFor = user.getUserName();
        }
    }

    public static void discard() {
        saved = null;
        savedFor = null;
    }
}
