package com.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class TestDataFiles {

    private static final String[] FILES = {
        DataConstants.USER_FILE_NAME,
        DataConstants.SHELTER_FILE_NAME,
        DataConstants.RELIEF_REQUEST_FILE_NAME,
        DataConstants.EVENT_FILE_NAME
    };

    private static final Map<String, byte[]> backups = new HashMap<>();

    public static void backUpAll() {
        backups.clear();
        for (String file : FILES) {
            Path path = Paths.get(file);
            try {
                backups.put(file, Files.exists(path) ? Files.readAllBytes(path) : null);
            } catch (IOException e) {
                throw new RuntimeException("Could not back up " + file, e);
            }
        }
    }

    public static void restoreAll() {
        for (Map.Entry<String, byte[]> entry : backups.entrySet()) {
            Path path = Paths.get(entry.getKey());
            try {
                if (entry.getValue() == null) {
                    Files.deleteIfExists(path);
                } else {
                    Files.createDirectories(path.getParent());
                    Files.write(path, entry.getValue());
                }
            } catch (IOException e) {
                throw new RuntimeException("Could not restore " + entry.getKey(), e);
            }
        }
    }

    public static void write(String file, String contents) {
        try {
            Files.write(Paths.get(file), contents.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("Could not write " + file, e);
        }
    }

    public static String read(String file) {
        try {
            return new String(Files.readAllBytes(Paths.get(file)), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("Could not read " + file, e);
        }
    }

    public static void delete(String file) {
        try {
            Files.deleteIfExists(Paths.get(file));
        } catch (IOException e) {
            throw new RuntimeException("Could not delete " + file, e);
        }
    }

    public static void resetUserManagement() {
        try {
            java.lang.reflect.Field field = UserManagement.class.getDeclaredField("userManagement");
            field.setAccessible(true);
            field.set(null, null);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException("Could not reset UserManagement", e);
        }
    }

    public static final String JOHN_ID = "123e4567-e89b-12d3-a456-426614174000";
    public static final String MARIA_ID = "223e4567-e89b-12d3-a456-426614174001";

    public static final String TWO_USERS_JSON = """
        [
          {
            "id": "123e4567-e89b-12d3-a456-426614174000",
            "userName": "johndoe",
            "firstName": "John",
            "lastName": "Doe",
            "password": "password123",
            "safety": "SAFE",
            "specialty": null,
            "contactPhone": "803-555-0101",
            "roles": ["VICTIM", "VOLUNTEER"],
            "location": {
              "street1": "123 Main St", "street2": "Apt 4B", "city": "Columbia",
              "state": "SC", "zipCode": "29201", "latitude": 33.9988, "longitude": -81.0453
            },
            "emergencyContacts": [
              { "name": "Jane Doe", "phoneNumber": "555-0199", "relationship": "Spouse" }
            ]
          },
          {
            "id": "223e4567-e89b-12d3-a456-426614174001",
            "userName": "mrivera",
            "firstName": "Maria",
            "lastName": "Rivera",
            "password": "volunteer456",
            "safety": "CAUTION",
            "specialty": "EMS",
            "contactPhone": null,
            "roles": ["VOLUNTEER"],
            "location": {
              "street1": "840 Harden St", "street2": "", "city": "Columbia",
              "state": "SC", "zipCode": "29205", "latitude": 34.0012, "longitude": -81.0147
            },
            "emergencyContacts": []
          }
        ]
        """;
}