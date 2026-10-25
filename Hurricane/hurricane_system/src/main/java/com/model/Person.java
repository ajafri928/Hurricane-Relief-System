package com.model;
import java.util.UUID;

public class Person {
    private final UUID id;
    private String firstName;
    private String lastName;
    private int age;
    private boolean needsMedicalAssistance;

    public Person(UUID id,String firstName, String lastName, int age, boolean needsMedicalAssistance) {
        this.id = null;
    }

    public Person(String firstName, String lastName, int age) {
        this.id = null;
    }
}
