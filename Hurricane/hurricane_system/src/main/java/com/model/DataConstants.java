package com.model;

import java.io.File;

public abstract class DataConstants{

protected static final String SHELTER_FILE_NAME = "jsonfiles" + File.separator + "Shelters.json";
protected static final String USER_FILE_NAME = "jsonfiles" + File.separator + "Users.json";
protected static final String HURRICANE_EVENT_FILE_NAME = "jsonfiles" + File.separator + "HurricaneEvents.json";
protected static final String RELIEF_REQUEST_FILE_NAME = "jsonfiles" + File.separator + "ReliefRequests.json";
protected static final String EVENT_FILE_NAME = "jsonfiles" + File.separator + "HurricaneEvents.json";

protected static final String USER_ID = "id";
protected static final String USER_USERNAME = "userName";
protected static final String USER_FIRST_NAME = "firstName";
protected static final String USER_LAST_NAME = "lastName";
protected static final String USER_PASSWORD = "password";
protected static final String USER_LOCATION = "location";
protected static final String USER_SAFETY = "safety";
protected static final String USER_ROLES = "roles";
protected static final String USER_EMERGENCY_CONTACTS = "emergencyContacts";

protected static final String SHELTER_ID = "id";
protected static final String SHELTER_NAME = "name";
protected static final String SHELTER_LOCATION = "location";
protected static final String SHELTER_CAPACITY = "capacity";
protected static final String SHELTER_OCCUPANCY = "occupancy";
protected static final String SHELTER_PET_FRIENDLY = "petFriendly";
protected static final String SHELTER_ACCESSIBLE = "accessible";
protected static final String SHELTER_MEDICAL_STAFF = "medicalStaff";
protected static final String SHELTER_VET_STAFF = "vetStaff";
protected static final String SHELTER_OPERATIONAL_STATUS = "operationalStatus";

protected static final String RELIEF_REQUEST_ID = "id";
protected static final String RELIEF_REQUEST_DATE = "requestDate";
protected static final String RELIEF_REQUEST_ASSISTANCE = "assistance";
protected static final String RELIEF_REQUEST_LOCATION = "location";
protected static final String RELIEF_REQUEST_PRIORITY_SCORE = "priorityScore";
protected static final String RELIEF_REQUEST_URGENCY = "urgency";
protected static final String RELIEF_REQUEST_STATUS = "status";
protected static final String RELIEF_REQUEST_USER_ID = "userId";
protected static final String RELIEF_REQUEST_DESCRIPTION = "description";
protected static final String RELIEF_REQUEST_SPECIAL_NEEDS = "specialNeeds";

protected static final String HURRICANE_EVENT_ID = "id";
protected static final String HURRICANE_EVENT_NAME = "name";
protected static final String HURRICANE_EVENT_CATEGORY = "category";
protected static final String HURRICANE_EVENT_STATUS = "status";

protected static final String SHELTER_AMENITIES = "amenities";
protected static final String SHELTER_RESOURCES = "shelterResources";

protected static final String LOCATION_ID = "id";
protected static final String LOCATION_STREET_1 = "street1";
protected static final String LOCATION_STREET_2 = "street2";
protected static final String LOCATION_CITY = "city";
protected static final String LOCATION_STATE = "state";
protected static final String LOCATION_ZIP_CODE = "zipCode";
protected static final String LOCATION_LATITUDE = "latitude";
protected static final String LOCATION_LONGITUDE = "longitude";

protected static final String RESOURCE_TYPE = "type";
protected static final String RESOURCE_QUANTITY = "quantity";
protected static final String RESOURCE_UNIT = "unit";
protected static final String RESOURCE_AVAILABLE_UNTIL = "availableUntil";
protected static final String RESOURCE_LOW_STOCK_THRESHOLD = "lowStockThreshold";


protected static final String RELIEF_REQUEST_COMMENTS = "comments";
protected static final String COMMENT_AUTHOR_ID = "authorId";
protected static final String COMMENT_TEXT = "text";
protected static final String COMMENT_TIMESTAMP = "timeStamp";

protected static final String USER_SPECIALTY = "specialty";
protected static final String USER_CONTACT_PHONE = "contactPhone";

protected static final String RELIEF_REQUEST_PERSONS = "requestedPersons";
protected static final String RELIEF_REQUEST_PETS = "pets";
protected static final String RELIEF_REQUEST_CONTACT_PHONE = "contactPhone";
protected static final String RELIEF_REQUEST_ASSIGNED_TO = "assignedTo";

protected static final String PERSON_ID = "id";
protected static final String PERSON_FIRST_NAME = "firstName";
protected static final String PERSON_LAST_NAME = "lastName";
protected static final String PERSON_AGE = "age";
protected static final String PERSON_NEEDS_MEDICAL = "needsMedicalAssistance";

protected static final String PET_ID = "id";
protected static final String PET_NAME = "name";
protected static final String PET_SPECIES = "species";

protected static final String SHELTER_OCCUPANTS = "occupants";
protected static final String SHELTER_PETS_ON_SITE = "petsOnSite";

}
