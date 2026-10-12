package com.model;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataLoader extends DataConstants {

    public static ArrayList<Shelter> getShelters() {
        ArrayList<Shelter> shelters = new ArrayList<>();

        try {
            FileReader reader = new FileReader(SHELTER_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray sheltersJSON = (JSONArray) parser.parse(reader);

            for (int i = 0; i < sheltersJSON.size(); i++) {
                JSONObject shelterJSON = (JSONObject) sheltersJSON.get(i);

                UUID id = UUID.fromString((String) shelterJSON.get(SHELTER_ID));
                String name = (String) shelterJSON.get(SHELTER_NAME);
                int capacity = (int) (long) shelterJSON.get(SHELTER_CAPACITY);
                int occupancy = (int) (long) shelterJSON.get(SHELTER_OCCUPANCY);

                boolean petFriendly = (Boolean) shelterJSON.get(SHELTER_PET_FRIENDLY);
                boolean accessible = Boolean.TRUE.equals(shelterJSON.get(SHELTER_ACCESSIBLE));
                boolean medicalStaff = Boolean.TRUE.equals(shelterJSON.get(SHELTER_MEDICAL_STAFF));
                boolean vetStaff = Boolean.TRUE.equals(shelterJSON.get(SHELTER_VET_STAFF));

                String operationalStatus = (String) shelterJSON.get(SHELTER_OPERATIONAL_STATUS);

                JSONObject locationJSON = (JSONObject) shelterJSON.get(SHELTER_LOCATION);
                Location address = null;
                if (locationJSON != null) {
                    UUID locId = UUID.fromString((String) locationJSON.get("id"));
                    String street1 = (String) locationJSON.get("street1");
                    String street2 = (String) locationJSON.get("street2");
                    String city = (String) locationJSON.get("city");
                    String state = (String) locationJSON.get("state");
                    String zipCode = (String) locationJSON.get("zipCode");
                    double latitude = (Double) locationJSON.get("latitude");
                    double longitude = (Double) locationJSON.get("longitude");

                    address = new Location(locId, street1, street2, city, state, zipCode, latitude, longitude);
                }

                    Shelter shelter = new Shelter(
                    id, name, address, capacity, occupancy, 
                    petFriendly, accessible, medicalStaff, vetStaff, operationalStatus
                );

                shelters.add(shelter);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return shelters;
    }

    public static ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();

        try {
            FileReader reader = new FileReader(USER_FILE_NAME);
            JSONParser parser = new JSONParser();
            Object parsedData = parser.parse(reader);

            JSONArray usersJSON;
            if (parsedData instanceof JSONArray) {
                usersJSON = (JSONArray) parsedData;
            } else if (parsedData instanceof JSONObject) {
                usersJSON = new JSONArray();
                usersJSON.add(parsedData); 
            } else {
                return users;
            }

            for (int i = 0; i < usersJSON.size(); i++) {
                JSONObject userJSON = (JSONObject) usersJSON.get(i);

                UUID id = UUID.fromString((String) userJSON.get(USER_USERNAME != null ? USER_ID : "id"));
                String userName = (String) userJSON.get(USER_USERNAME);
                String firstName = (String) userJSON.get(USER_FIRST_NAME);
                String lastName = (String) userJSON.get(USER_LAST_NAME);
                String password = (String) userJSON.get(USER_PASSWORD);

                JSONObject locationJSON = (JSONObject) userJSON.get(USER_LOCATION);
                Location location = null;
                if (locationJSON != null) {
                    UUID locId = UUID.fromString((String) locationJSON.get("id"));
                    String street1 = (String) locationJSON.get("street1");
                    String street2 = (String) locationJSON.get("street2");
                    String city = (String) locationJSON.get("city");
                    String state = (String) locationJSON.get("state");
                    String zipCode = (String) locationJSON.get("zipCode");
                    double latitude = (Double) locationJSON.get("latitude");
                    double longitude = (Double) locationJSON.get("longitude");

                    location = new Location(locId, street1, street2, city, state, zipCode, latitude, longitude);
                }

                String safetyString = (String) userJSON.get(USER_SAFETY);
                SafetyCategory safety = null;
                if (safetyString != null) {
                    try {
                        safety = SafetyCategory.valueOf(safetyString.toUpperCase());
                    } catch (Exception e) {

                    }
                }

                // Roles
                JSONArray rolesJSONArray = (JSONArray) userJSON.get(USER_ROLES);
                ArrayList<UserRole> roles = new ArrayList<>();
                if (rolesJSONArray != null) {
                    for (int j = 0; j < rolesJSONArray.size(); j++) {
                        String roleStr = (String) rolesJSONArray.get(j);
                        try {
                            roles.add(UserRole.valueOf(roleStr.toUpperCase()));
                        } catch (Exception e) {
                        
                        }
                    }
                }

                User user = new User(id, userName, firstName, lastName, password, location, safety, roles);
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static ArrayList<ReliefRequest> getReliefRequests() {
        ArrayList<ReliefRequest> requests = new ArrayList<>();

        try {
            FileReader reader = new FileReader(RELIEF_REQUEST_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray requestsJSON = (JSONArray) parser.parse(reader);

            for (int i = 0; i < requestsJSON.size(); i++) {
                JSONObject requestJSON = (JSONObject) requestsJSON.get(i);

                UUID id = UUID.fromString((String) requestJSON.get(RELIEF_REQUEST_ID));
                String createdAt = (String) requestJSON.get(RELIEF_REQUEST_DATE);
                String assistance = (String) requestJSON.get(RELIEF_REQUEST_ASSISTANCE);
                int people = (int) (long) requestJSON.get(RELIEF_REQUEST_PEOPLE_COUNT);
                int priorityScore = (int) (long) requestJSON.get(RELIEF_REQUEST_PRIORITY_SCORE);

                JSONObject locationJSON = (JSONObject) requestJSON.get(RELIEF_REQUEST_LOCATION);
                Location location = null;
                if (locationJSON != null) {
                    UUID locId = UUID.fromString((String) locationJSON.get("id"));
                    String street1 = (String) locationJSON.get("street1");
                    String street2 = (String) locationJSON.get("street2");
                    String city = (String) locationJSON.get("city");
                    String state = (String) locationJSON.get("state");
                    String zipCode = (String) locationJSON.get("zipCode");
                    double latitude = (Double) locationJSON.get("latitude");
                    double longitude = (Double) locationJSON.get("longitude");

                    location = new Location(locId, street1, street2, city, state, zipCode, latitude, longitude);
                }

                // Enums
                String urgencyStr = (String) requestJSON.get(RELIEF_REQUEST_URGENCY);
                UrgencyTier urgency = UrgencyTier.valueOf(urgencyStr);
                if (urgencyStr != null) {
                    try {
                        urgency = UrgencyTier.valueOf(urgencyStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: Invalid UrgencyTier constant '" + urgencyStr + "'. Setting to default/null.");
                    }
                }

                String statusStr = (String) requestJSON.get(RELIEF_REQUEST_STATUS);
                ReliefRequestStatus status = null;
                if (statusStr != null) {
                    try {
                        status = ReliefRequestStatus.valueOf(statusStr.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        System.err.println("Warning: Invalid ReliefRequestStatus constant '" + statusStr + "'. Setting to default/null.");
                    }
                }

                statusStr = (String) requestJSON.get(RELIEF_REQUEST_STATUS);
                status = ReliefRequestStatus.valueOf(statusStr);

                ReliefRequest request = new ReliefRequest(
                    id, createdAt, location, assistance, "", 
                    people, false, priorityScore, urgency, status
                );

                requests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public static ArrayList<HurricaneEvent> getHurricaneEvents() {
        ArrayList<HurricaneEvent> events = new ArrayList<>();

        try (FileReader reader = new FileReader(EVENT_FILE_NAME)) {
            JSONParser parser = new JSONParser();
            JSONArray eventsJSON = (JSONArray) parser.parse(reader);

            for (Object obj : eventsJSON) {
                JSONObject eventJSON = (JSONObject) obj;

                String idStr = (String) eventJSON.get(HURRICANE_EVENT_ID);
                UUID id = (idStr != null) ? UUID.fromString(idStr) : UUID.randomUUID();
                
                String name = (String) eventJSON.get(HURRICANE_EVENT_NAME);
                
                int category = 0;
                if (eventJSON.get(HURRICANE_EVENT_CATEGORY) != null) {
                    category = ((Number) eventJSON.get(HURRICANE_EVENT_CATEGORY)).intValue();
                }

                String status = (String) eventJSON.get(HURRICANE_EVENT_STATUS);

                events.add(new HurricaneEvent(id, name, category, status));
            }
        } catch (FileNotFoundException e) {
            System.err.println("Hurricane events file not found: " + EVENT_FILE_NAME);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }
}
