package com.model;

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
                JSONObject shelterJSON =
                        (JSONObject) sheltersJSON.get(i);

                UUID id = getUuid(shelterJSON, SHELTER_ID);
                String name = getString(shelterJSON, SHELTER_NAME);
                int capacity = getInt(shelterJSON, SHELTER_CAPACITY);
                int occupancy = getInt(shelterJSON, SHELTER_OCCUPANCY);

                boolean petFriendly =
                        getBoolean(shelterJSON, SHELTER_PET_FRIENDLY);
                boolean accessible =
                        getBoolean(shelterJSON, SHELTER_ACCESSIBLE);
                boolean medicalStaff =
                        getBoolean(shelterJSON, SHELTER_MEDICAL_STAFF);
                boolean vetStaff =
                        getBoolean(shelterJSON, SHELTER_VET_STAFF);

                String operationalStatus = getString(
                        shelterJSON, SHELTER_OPERATIONAL_STATUS);

                JSONObject locationJSON = (JSONObject) shelterJSON.get(
                        SHELTER_LOCATION);

                Location address = parseLocation(locationJSON);

                Shelter shelter = new Shelter(
                        id, name, address, capacity, occupancy,
                        petFriendly, accessible, medicalStaff, vetStaff,
                        operationalStatus);

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

                UUID id = getUuid(userJSON, USER_ID);
                String userName = getString(userJSON, USER_USERNAME);
                String firstName = getString(userJSON, USER_FIRST_NAME);
                String lastName = getString(userJSON, USER_LAST_NAME);
                String password = getString(userJSON, USER_PASSWORD);

                JSONObject locationJSON = (JSONObject) userJSON.get(
                        USER_LOCATION);

                Location location = parseLocation(locationJSON);

                SafetyCategory safety = parseEnum(
                        userJSON.get(USER_SAFETY),
                        SafetyCategory.class);

                JSONArray rolesJSON = (JSONArray) userJSON.get(USER_ROLES);
                ArrayList<UserRole> roles = new ArrayList<>();

                if (rolesJSON != null) {
                    for (int j = 0; j < rolesJSON.size(); j++) {
                        UserRole role = parseEnum(
                                rolesJSON.get(j), UserRole.class);

                        if (role != null) {
                            roles.add(role);
                        }
                    }
                }

                User user = new User(
                        id, userName, firstName, lastName, password,
                        location, safety, roles);

                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public static ArrayList<ReliefRequest> getReliefRequests() {
        ArrayList<ReliefRequest> requests = new ArrayList<>();

        /*
         * Get the existing users once. This lets a relief request point to
         * the actual User who submitted it instead of creating a new User.
         */
        ArrayList<User> users = getUsers();

        try {
            FileReader reader = new FileReader(RELIEF_REQUEST_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray requestsJSON = (JSONArray) parser.parse(reader);

            for (int i = 0; i < requestsJSON.size(); i++) {
                JSONObject requestJSON =
                        (JSONObject) requestsJSON.get(i);

                UUID id = getUuid(requestJSON, RELIEF_REQUEST_ID);
                String createdAt = getString(
                        requestJSON, RELIEF_REQUEST_DATE);
                String assistance = getString(
                        requestJSON, RELIEF_REQUEST_ASSISTANCE);
                String description = getString(
                        requestJSON, RELIEF_REQUEST_DESCRIPTION);

                int priorityScore = getInt(
                        requestJSON, RELIEF_REQUEST_PRIORITY_SCORE);

                boolean specialNeeds = getBoolean(
                        requestJSON, RELIEF_REQUEST_SPECIAL_NEEDS);

                JSONObject locationJSON = (JSONObject) requestJSON.get(
                        RELIEF_REQUEST_LOCATION);

                Location location = parseLocation(locationJSON);

                UrgencyTier urgency = parseEnum(
                        requestJSON.get(RELIEF_REQUEST_URGENCY),
                        UrgencyTier.class);

                ReliefRequestStatus status = parseEnum(
                        requestJSON.get(RELIEF_REQUEST_STATUS),
                        ReliefRequestStatus.class);

                UUID userId = getUuid(
                        requestJSON, RELIEF_REQUEST_USER_ID);

                User submittedBy = getUserById(users, userId);

                ReliefRequest request = new ReliefRequest(
                        id, createdAt, location, assistance, description,
                        specialNeeds, priorityScore, urgency,
                        status, submittedBy);

                        for (Person p : parsePersons((JSONArray) requestJSON.get(RELIEF_REQUEST_PERSONS))) {
                            request.addPerson(p);
                        }
                        for (Pet p : parsePets((JSONArray) requestJSON.get(RELIEF_REQUEST_PETS))) {
                            request.addPet(p);
                        }
                        request.setContactPhone(getString(requestJSON, RELIEF_REQUEST_CONTACT_PHONE));
                        request.setResponder(getUserById(
                                users, getUuid(requestJSON, RELIEF_REQUEST_ASSIGNED_TO)));

                requests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    public static ArrayList<HurricaneEvent> getHurricaneEvents() {
        ArrayList<HurricaneEvent> events = new ArrayList<>();

        try {
            FileReader reader = new FileReader(EVENT_FILE_NAME);
            JSONParser parser = new JSONParser();
            JSONArray eventsJSON = (JSONArray) parser.parse(reader);

            for (int i = 0; i < eventsJSON.size(); i++) {
                JSONObject eventJSON =
                        (JSONObject) eventsJSON.get(i);

                UUID id = getUuid(eventJSON, HURRICANE_EVENT_ID);

                if (id == null) {
                    id = UUID.randomUUID();
                }

                String name = getString(
                        eventJSON, HURRICANE_EVENT_NAME);

                int category = getInt(
                        eventJSON, HURRICANE_EVENT_CATEGORY);

                String status = getString(
                        eventJSON, HURRICANE_EVENT_STATUS);

                HurricaneEvent event = new HurricaneEvent(
                        id, name, category, status);

                events.add(event);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    /*
     * Helper methods
     */

    private static Location parseLocation(JSONObject locationJSON) {
        if (locationJSON == null) {
            return null;
        }


        UUID id = getUuid(locationJSON, LOCATION_ID);
        String street1 = getString(locationJSON, LOCATION_STREET_1);
        String street2 = getString(locationJSON, LOCATION_STREET_2);
        String city = getString(locationJSON, LOCATION_CITY);
        String state = getString(locationJSON, LOCATION_STATE);
        String zipCode = getString(locationJSON, LOCATION_ZIP_CODE);

        double latitude = getDouble(locationJSON, LOCATION_LATITUDE);
        double longitude = getDouble(locationJSON, LOCATION_LONGITUDE);

        return new Location(
                street1, street2, city, state, zipCode,
                latitude, longitude);
    }

    private static Person parsePerson(JSONObject json) {
        UUID id = getUuid(json, PERSON_ID);
        String firstName = getString(json, PERSON_FIRST_NAME);
        String lastName = getString(json, PERSON_LAST_NAME);
        int age = getInt(json, PERSON_AGE);
        boolean needsMedical = getBoolean(json, PERSON_NEEDS_MEDICAL);
        return new Person(id, firstName, lastName, age, needsMedical);
    }
    
    private static Pet parsePet(JSONObject json) {
        UUID id = getUuid(json, PET_ID);
        String name = getString(json, PET_NAME);
        String species = getString(json, PET_SPECIES);
        return new Pet(id, name, species);
    }
    
    private static ArrayList<Person> parsePersons(JSONArray array) {
        ArrayList<Person> persons = new ArrayList<>();
        if (array == null) {
            return persons;
        }
        for (int i = 0; i < array.size(); i++) {
            persons.add(parsePerson((JSONObject) array.get(i)));
        }
        return persons;
    }
    
    private static ArrayList<Pet> parsePets(JSONArray array) {
        ArrayList<Pet> pets = new ArrayList<>();
        if (array == null) {
            return pets;
        }
        for (int i = 0; i < array.size(); i++) {
            pets.add(parsePet((JSONObject) array.get(i)));
        }
        return pets;
    }

    private static UUID getUuid(JSONObject jsonObject, String key) {
        String idString = getString(jsonObject, key);

        if (idString == null) {
            return null;
        }

        try {
            return UUID.fromString(idString);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid UUID for " + key + ": " + idString);
            return null;
        }
    }

    private static String getString(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    private static int getInt(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    private static double getDouble(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return 0.0;
    }

    private static boolean getBoolean(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return value != null && Boolean.parseBoolean(value.toString());
    }

    private static <T extends Enum<T>> T parseEnum(
            Object value, Class<T> enumType) {

        if (value == null) {
            return null;
        }

        try {
            return Enum.valueOf(
                    enumType, value.toString().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid " + enumType.getSimpleName()
                    + " value: " + value);
            return null;
        }
    }

    private static User getUserById(
            ArrayList<User> users, UUID userId) {

        if (userId == null) {
            return null;
        }

        for (User user : users) {
            if (userId.equals(user.getId())) {
                return user;
            }
        }

        System.err.println("No user found for userId: " + userId);
        return null;
    }

    
}
