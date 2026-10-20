package com.model;

import java.util.UUID;

public class IDGenerator {

    /**
     * Generates a random Type 4 UUID.
     * 
     * @return A unique 36-character UUID (e.g., "123e4567-e89b-12d3-a456-426614174000")
     */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }
    public static void main(String[] args) {
        System.out.println(generateUUID());
    }
}
