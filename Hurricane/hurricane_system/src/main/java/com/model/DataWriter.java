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
    locationJSON.put(LOCATION_ID, location.getId().toString());
    locationJSON.put(LOCATION_STREET_1, location.getStreet1());
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
    resourceJSON.put(RESOURCE_AVAILABLE_UNTIL, resource.getAvailableUntil());
    resourceJSON.put(RESOURCE_LOW_STOCK_THRESHOLD, resource.getLowStockThreshold());
    return resourceJSON;
}