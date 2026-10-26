package com.model;

import java.util.UUID;

/** Makes random UUIDs. */
public class IDGenerator {

    /** @return a random UUID */
    public static UUID generateUUID() {
        return UUID.randomUUID();
    }

    /** @param args not used */
    public static void main(String[] args) {
        System.out.println(generateUUID());
    }
}