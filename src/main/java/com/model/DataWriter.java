package com.model;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Saves shelters, users, and relief requests to the JSON files.
 *
 * @author Sahil
 */
@SuppressWarnings("unchecked")
public class DataWriter extends DataConstants{
    /**
     * Saves all shelters to the shelters JSON file.
     *
     * @param shelters the shelters to save
     */
    public static void saveShelters(ArrayList<Shelter> shelters) {
        JSONArray sheltersJSON = new JSONArray();

        for (Shelter shelter : shelters) {
            sheltersJSON.add(getShelterJSON(shelter));
        }
        try {
                FileWriter file = new FileWriter(SHELTER_FILE_NAME);
                file.write(sheltersJSON.toJSONString());
                file.flush();
                file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Turns a shelter into JSON, including its amenities, resources, occupants, and pets.
     *
     * @param shelter the shelter to save
     * @return the shelter as a JSON object
     */
    public static JSONObject getShelterJSON(Shelter shelter) {
        JSONObject shelterJSON = new JSONObject();
        shelterJSON.put(SHELTER_ID, shelter.getId().toString());
        shelterJSON.put(SHELTER_NAME, shelter.getName());
        shelterJSON.put(SHELTER_LOCATION, getLocationJSON(shelter.getLocation()));
        shelterJSON.put(SHELTER_CAPACITY, shelter.getCapacity());
        shelterJSON.put(SHELTER_OCCUPANCY, shelter.getOccupancy());
        shelterJSON.put(SHELTER_PET_FRIENDLY, shelter.isPetFriendly());
        shelterJSON.put(SHELTER_ACCESSIBLE, shelter.isAccessible());
        shelterJSON.put(SHELTER_MEDICAL_STAFF, shelter.hasMedicalStaff());
        shelterJSON.put(SHELTER_VET_STAFF, shelter.hasVetStaff());
        shelterJSON.put(SHELTER_OPERATIONAL_STATUS, shelter.getOperationalStatus());

    JSONArray amenitiesJSON = new JSONArray();
    for (String amenity : shelter.getAmenities()) {
        amenitiesJSON.add(amenity);
    }
    shelterJSON.put(SHELTER_AMENITIES, amenitiesJSON);

    JSONArray resourcesJSON = new JSONArray();
    for (ShelterResource resource : shelter.getShelterResources()) {
        resourcesJSON.add(getResourceJSON(resource));
    }
    shelterJSON.put(SHELTER_RESOURCES, resourcesJSON);

    JSONArray occupantsJSON = new JSONArray();
    for (Person person : shelter.getOccupants()) {
        occupantsJSON.add(getPersonJSON(person));
    }
    shelterJSON.put(SHELTER_OCCUPANTS, occupantsJSON);

    JSONArray petsJSON = new JSONArray();
    for (Pet pet : shelter.getPetsOnSite()) {
        petsJSON.add(getPetJSON(pet));
    }
    shelterJSON.put(SHELTER_PETS_ON_SITE, petsJSON);

    return shelterJSON;
}

// Turns a location into JSON
private static JSONObject getLocationJSON(Location location) {
    JSONObject locationJSON = new JSONObject();
    locationJSON.put(LOCATION_STREET_1, location.getAddressLine1());
    locationJSON.put(LOCATION_STREET_2, location.getAddressLine2());
    locationJSON.put(LOCATION_CITY, location.getCity());
    locationJSON.put(LOCATION_STATE, location.getState());
    locationJSON.put(LOCATION_ZIP_CODE, location.getZipCode());
    locationJSON.put(LOCATION_LATITUDE, location.getLatitude());
    locationJSON.put(LOCATION_LONGITUDE, location.getLongitude());
    return locationJSON;
}
// Turns a shelter resource into JSON
private static JSONObject getResourceJSON(ShelterResource resource) {
    JSONObject resourceJSON = new JSONObject();
    resourceJSON.put(RESOURCE_TYPE, resource.getType());
    resourceJSON.put(RESOURCE_UNIT, resource.getUnit());
    resourceJSON.put(RESOURCE_QUANTITY, resource.getQuantity());
    resourceJSON.put(RESOURCE_AVAILABLE_UNTIL,
            resource.getAvailableUntil() == null ? null : resource.getAvailableUntil().toString());
    resourceJSON.put(RESOURCE_LOW_STOCK_THRESHOLD, resource.getLowStockThreshold());
    return resourceJSON;
}

/**
 * Turns a person into JSON. Public so other save code (like relief requests) can reuse it.
 *
 * @param person the person to save
 * @return the person as a JSON object
 */
public static JSONObject getPersonJSON(Person person) {
    JSONObject personJSON = new JSONObject();
    personJSON.put(PERSON_ID, person.getId().toString());
    personJSON.put(PERSON_FIRST_NAME, person.getFirstName());
    personJSON.put(PERSON_LAST_NAME, person.getLastName());
    personJSON.put(PERSON_AGE, person.getAge());
    personJSON.put(PERSON_NEEDS_MEDICAL, person.needsMedicalAssistance());
    return personJSON;
}

/**
 * Turns a pet into JSON.
 *
 * @param pet the pet to save
 * @return the pet as a JSON object
 */
public static JSONObject getPetJSON(Pet pet) {
    JSONObject petJSON = new JSONObject();
    petJSON.put(PET_ID, pet.getId().toString());
    petJSON.put(PET_NAME, pet.getName());
    petJSON.put(PET_SPECIES, pet.getSpecies());
    return petJSON;
}

// Turns an emergency contact into JSON
private static JSONObject getEmergencyContactJSON(EmergencyContact contact) {
    JSONObject contactJSON = new JSONObject();
    contactJSON.put(CONTACT_NAME, contact.getName());
    contactJSON.put(CONTACT_PHONE, contact.getPhone());
    contactJSON.put(CONTACT_RELATIONSHIP, contact.getRelationship());
    return contactJSON;
}
    /**
     * Saves all users to the users JSON file.
     *
     * @param users the users to save
     */
    public static void saveUsers(ArrayList<User> users) {
        JSONArray usersJSON = new JSONArray();

        for (User user : users) {
            usersJSON.add(getUserJSON(user));
        }

        try {
            FileWriter file = new FileWriter(USER_FILE_NAME);
            file.write(usersJSON.toJSONString());
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Turns a user into JSON, including their roles and emergency contacts.
     *
     * @param user the user to save
     * @return the user as a JSON object
     */
    public static JSONObject getUserJSON(User user) {
        JSONObject userJSON = new JSONObject();

        userJSON.put(USER_ID, user.getId().toString());
        userJSON.put(USER_USERNAME, user.getUserName());
        userJSON.put(USER_FIRST_NAME, user.getFirstName());
        userJSON.put(USER_LAST_NAME, user.getLastName());
        userJSON.put(USER_PASSWORD, user.getPassword());
        userJSON.put(USER_LOCATION, getLocationJSON(user.getLocation()));
        userJSON.put(USER_SAFETY, user.getSafety() == null ? null : user.getSafety().toString());
        userJSON.put(USER_SPECIALTY, user.getSpecialty() == null ? null : user.getSpecialty().toString());
        userJSON.put(USER_CONTACT_PHONE, user.getContactPhone());
        JSONArray rolesJSON = new JSONArray();
        for (UserRole role : user.getRoles()) {
            rolesJSON.add(role.toString());
        }
        userJSON.put(USER_ROLES, rolesJSON);

        JSONArray contactsJSON = new JSONArray();
        for (EmergencyContact contact : user.getEmergencyContacts()) {
            contactsJSON.add(getEmergencyContactJSON(contact));
        }
        userJSON.put(USER_EMERGENCY_CONTACTS, contactsJSON);

        return userJSON;
    }
    /**
     * Saves all relief requests to the relief requests JSON file.
     *
     * @param requests the relief requests to save
     */
    public static void saveReliefRequests(ArrayList<ReliefRequest> requests) {
        JSONArray requestsJSON = new JSONArray();

        for (ReliefRequest request: requests) {
            requestsJSON.add(getReliefRequestJSON(request));
        }

        try {
            FileWriter file = new FileWriter(RELIEF_REQUEST_FILE_NAME);
            file.write(requestsJSON.toJSONString());
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * Turns a relief request into JSON, including its people, pets, and comments.
     *
     * @param request the relief request to save
     * @return the relief request as a JSON object
     */
    public static JSONObject getReliefRequestJSON(ReliefRequest request) {
        JSONObject requestJSON = new JSONObject();

        requestJSON.put(RELIEF_REQUEST_ID, request.getId().toString());
        requestJSON.put(RELIEF_REQUEST_DATE, request.getCreatedAt());
        requestJSON.put(RELIEF_REQUEST_USER_ID,
                request.getSubmittedBy() == null ? null : request.getSubmittedBy().getId().toString());
        requestJSON.put(RELIEF_REQUEST_ASSISTANCE, request.getAssistance());
        requestJSON.put(RELIEF_REQUEST_DESCRIPTION, request.getDescription());
        requestJSON.put(RELIEF_REQUEST_LOCATION, getLocationJSON(request.getLocation()));
        requestJSON.put(RELIEF_REQUEST_PERSONS, getPersonsJSON(request.getRequestedPersons()));
        requestJSON.put(RELIEF_REQUEST_PETS, getPetsJSON(request.getPets()));
        requestJSON.put(RELIEF_REQUEST_CONTACT_PHONE, request.getContactPhone());
        requestJSON.put(RELIEF_REQUEST_ASSIGNED_TO,
                request.getResponder() == null ? null : request.getResponder().getId().toString());        
                requestJSON.put(RELIEF_REQUEST_SPECIAL_NEEDS, request.hasSpecialNeeds());
        requestJSON.put(RELIEF_REQUEST_PRIORITY_SCORE, request.getPriorityScore());
        requestJSON.put(RELIEF_REQUEST_URGENCY,
                request.getUrgency() == null ? null : request.getUrgency().toString());
        requestJSON.put(RELIEF_REQUEST_STATUS,
                request.getStatus() == null ? null : request.getStatus().toString());

    JSONArray commentsJSON = new JSONArray();         
    for (RequestComment comment : request.getComments()) {
        commentsJSON.add(getCommentJSON(comment));
    }
    requestJSON.put(RELIEF_REQUEST_COMMENTS, commentsJSON);

        return requestJSON;
    }
    // Turns a request comment into JSON
    private static JSONObject getCommentJSON(RequestComment comment) {
    JSONObject commentJSON = new JSONObject();

    commentJSON.put(COMMENT_AUTHOR_ID,
            comment.getAuthor() == null ? null : comment.getAuthor().getId().toString());
    commentJSON.put(COMMENT_TEXT, comment.getText());
    commentJSON.put(COMMENT_TIMESTAMP, comment.getTimeStamp().toString());

    return commentJSON;
}

// Turns a list of people into a JSON array
private static JSONArray getPersonsJSON(ArrayList<Person> persons) {
    JSONArray personsJSON = new JSONArray();
    for (Person person : persons) {
        personsJSON.add(getPersonJSON(person));
    }
    return personsJSON;
}

// Turns a list of pets into a JSON array
private static JSONArray getPetsJSON(ArrayList<Pet> pets) {
    JSONArray petsJSON = new JSONArray();
    for (Pet pet : pets) {
        petsJSON.add(getPetJSON(pet));
    }
    return petsJSON;
}

}