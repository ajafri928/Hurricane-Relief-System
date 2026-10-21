package com.model;
import java.io.FileWriter;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class DataWriter extends DataConstants{
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

    return shelterJSON;
}

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
private static JSONObject getResourceJSON(ShelterResource resource) {
    JSONObject resourceJSON = new JSONObject();
    resourceJSON.put(RESOURCE_TYPE, resource.getType());
    resourceJSON.put(RESOURCE_UNIT, resource.getUnit());
    resourceJSON.put(RESOURCE_QUANTITY, resource.getQuantity());
    resourceJSON.put(RESOURCE_AVAILABLE_UNTIL, resource.getAvailableUntil().toString());
    resourceJSON.put(RESOURCE_LOW_STOCK_THRESHOLD, resource.getLowStockThreshold());
    return resourceJSON;
}
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

    public static JSONObject getUserJSON(User user) {
        JSONObject userJSON = new JSONObject();

        userJSON.put(USER_ID, user.getId().toString());
        userJSON.put(USER_USERNAME, user.getUserName());
        userJSON.put(USER_FIRST_NAME, user.getFirstName());
        userJSON.put(USER_LAST_NAME, user.getLastName());
        userJSON.put(USER_PASSWORD, user.getPassword());
        userJSON.put(USER_LOCATION, getLocationJSON(user.getLocation()));
        userJSON.put(USER_SAFETY, user.getSafety() == null ? null : user.getSafety().toString());

        JSONArray rolesJSON = new JSONArray();
        for (UserRole role : user.getRoles()) {
            rolesJSON.add(role.toString());
        }
        userJSON.put(USER_ROLES, rolesJSON);

        return userJSON;
    }
    public static void saveReliefRequests(ArrayList<ReliefRequest> requests) {
        JSONArray requestsJSON = new JSONArray();

        for (ReliefRequest request: requests) {
            requestsJSON.add(getReliefRequestJSON(request));
        }

        try {
            FileWriter file = new FileWriter(USER_FILE_NAME);
            file.write(requestsJSON.toJSONString());
            file.flush();
            file.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static JSONObject getReliefRequestJSON(ReliefRequest request) {
        JSONObject requestJSON = new JSONObject();

        requestJSON.put(RELIEF_REQUEST_ID, request.getId().toString());
        requestJSON.put(RELIEF_REQUEST_DATE, request.getCreatedAt());
        requestJSON.put(RELIEF_REQUEST_USER_ID,
                request.getSubmittedBy() == null ? null : request.getSubmittedBy().getId().toString());
        requestJSON.put(RELIEF_REQUEST_ASSISTANCE, request.getAssistance());
        requestJSON.put(RELIEF_REQUEST_DESCRIPTION, request.getDescription());
        requestJSON.put(RELIEF_REQUEST_LOCATION, getLocationJSON(request.getLocation()));
        requestJSON.put(RELIEF_REQUEST_PEOPLE_COUNT, request.getPeople());
        requestJSON.put(RELIEF_REQUEST_SPECIAL_NEEDS, request.hasSpecialNeeds());
        requestJSON.put(RELIEF_REQUEST_PRIORITY_SCORE, request.getPriorityScore());
        requestJSON.put(RELIEF_REQUEST_URGENCY,
                request.getUrgency() == null ? null : request.getUrgency().toString());
        requestJSON.put(RELIEF_REQUEST_STATUS,
                request.getStatus() == null ? null : request.getStatus().toString());

        return requestJSON;
    }

}