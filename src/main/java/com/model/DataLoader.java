package com.model;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.UUID;
import java.time.LocalDateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 * Loads shelters, users, relief requests, and hurricane events from the JSON files.
 *
 * @author Anish
 */
public class DataLoader extends DataConstants {

    /**
     * Loads every shelter, including its amenities, resources, occupants, and pets.
     *
     * @return the list of shelters, or an empty list if the file can't be read
     */
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
                
                // Load the shelter's amenities from the JSON array.
                JSONArray amenitiesJSON = (JSONArray) shelterJSON.get(
                        SHELTER_AMENITIES);

                if (amenitiesJSON != null) {
                    for (Object amenityObject : amenitiesJSON) {
                        shelter.addAmenity(amenityObject.toString());
                    }
                }

                // Load the shelter's resources from the JSON array.
                JSONArray resourcesJSON = (JSONArray) shelterJSON.get(
                        SHELTER_RESOURCES);

                if (resourcesJSON != null) {
                    for (Object resourceObject : resourcesJSON) {
                        shelter.addResource(parseResource((JSONObject) resourceObject));
                    }
                }

                // Load the people checked into the shelter.
                JSONArray occupantsJSON = (JSONArray) shelterJSON.get(SHELTER_OCCUPANTS);

                if (occupantsJSON != null) {
                    for (Object personObject : occupantsJSON) {
                        shelter.getOccupants().add(parsePerson((JSONObject) personObject));
                    }
                }

                // Load the pets staying at the shelter.
                JSONArray petsJSON = (JSONArray) shelterJSON.get(SHELTER_PETS_ON_SITE);

                if (petsJSON != null) {
                    for (Object petObject : petsJSON) {
                        shelter.getPetsOnSite().add(parsePet((JSONObject) petObject));
                    }
                }

                shelters.add(shelter);
            }

            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return shelters;
    }

    /**
     * Loads every user, including their roles, location, and emergency contacts.
     *
     * @return the list of users, or an empty list if the file can't be read
     */
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
                VolunteerRole specialty = parseEnum(userJSON.get(USER_SPECIALTY), VolunteerRole.class);
                String contactPhone = getString(userJSON, USER_CONTACT_PHONE);


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
                user.setSpecialty(specialty);
                user.setContactPhone(contactPhone);

                JSONArray contactsJSON = (JSONArray) userJSON.get(USER_EMERGENCY_CONTACTS);
                if (contactsJSON != null) {
                    for (Object c : contactsJSON) {
                        JSONObject contactJSON = (JSONObject) c;
                        user.getEmergencyContacts().add(new EmergencyContact(
                                getString(contactJSON, CONTACT_NAME),
                                getString(contactJSON, CONTACT_PHONE),
                                getString(contactJSON, CONTACT_RELATIONSHIP)));
                    }
                }
                users.add(user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    /**
     * Loads every relief request and links it to the users who submitted and claimed it.
     *
     * @return the list of relief requests, or an empty list if the file can't be read
     */
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

                JSONArray commentsJSON = (JSONArray) requestJSON.get(RELIEF_REQUEST_COMMENTS);
                if (commentsJSON != null) {
                    for (Object c : commentsJSON) {
                        JSONObject commentJSON = (JSONObject) c;
                        User author = getUserById(users, getUuid(commentJSON, COMMENT_AUTHOR_ID));
                        LocalDateTime time = parseDateTime(getString(commentJSON, COMMENT_TIMESTAMP));
                        request.addComment(new RequestComment(
                                author, getString(commentJSON, COMMENT_TEXT),
                                time == null ? LocalDateTime.now() : time));
                    }
                }

                requests.add(request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requests;
    }

    /**
     * Loads every hurricane event.
     *
     * @return the list of hurricane events, or an empty list if the file can't be read
     */
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

    // ----- Helper methods -----

    // Builds a Location from JSON, or returns null if there isn't one
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

    // Reads a shelter resource (water, blankets, etc.)
    private static ShelterResource parseResource(JSONObject resourceJSON) {
        return new ShelterResource(
                getString(resourceJSON, RESOURCE_TYPE),
                getInt(resourceJSON, RESOURCE_QUANTITY),
                parseDateTime(getString(resourceJSON, RESOURCE_AVAILABLE_UNTIL)),
                getInt(resourceJSON, RESOURCE_LOW_STOCK_THRESHOLD),
                getString(resourceJSON, RESOURCE_UNIT));
    }

    // Turns "2026-10-15T12:00:00" (with or without a trailing Z) into a LocalDateTime, or null
    private static LocalDateTime parseDateTime(String text) {
        if (text == null) {
            return null;
        }
        try {
            if (text.endsWith("Z")) {
                text = text.substring(0, text.length() - 1);
            }
            return LocalDateTime.parse(text);
        } catch (Exception e) {
            System.err.println("Invalid date: " + text);
            return null;
        }
    }

    // Builds a Person from JSON, making a new id if one is missing
    private static Person parsePerson(JSONObject json) {
        UUID id = getUuid(json, PERSON_ID);
        if (id == null) id = UUID.randomUUID();
        String firstName = getString(json, PERSON_FIRST_NAME);
        String lastName = getString(json, PERSON_LAST_NAME);
        int age = getInt(json, PERSON_AGE);
        boolean needsMedical = getBoolean(json, PERSON_NEEDS_MEDICAL);
        return new Person(id, firstName, lastName, age, needsMedical);
    }
    
    // Builds a Pet from JSON, making a new id if one is missing
    private static Pet parsePet(JSONObject json) {
        UUID id = getUuid(json, PET_ID);
        if (id == null) id = UUID.randomUUID();
        String name = getString(json, PET_NAME);
        String species = getString(json, PET_SPECIES);
        return new Pet(id, name, species);
    }
    
    // Turns a JSON array into a list of Persons (empty if the array is missing)
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
    
    // Turns a JSON array into a list of Pets (empty if the array is missing)
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

    // Reads a UUID, or null if it's missing or invalid
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

    // Reads a String, or null if it's missing
    private static String getString(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value == null) {
            return null;
        }

        return value.toString();
    }

    // Reads an int, or 0 if it's missing
    private static int getInt(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Number) {
            return ((Number) value).intValue();
        }

        return 0;
    }

    // Reads a double, or 0.0 if it's missing
    private static double getDouble(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        return 0.0;
    }

    // Reads a boolean, or false if it's missing
    private static boolean getBoolean(JSONObject jsonObject, String key) {
        Object value = jsonObject.get(key);

        if (value instanceof Boolean) {
            return (Boolean) value;
        }

        return value != null && Boolean.parseBoolean(value.toString());
    }

    // Turns a String into the matching enum value, or null if it doesn't match
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

    // Finds the user with the given id, or null if there's no match
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