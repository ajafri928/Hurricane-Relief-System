package com.model;
import java.util.UUID;

public class Person {
    private final UUID id;
    private String firstName;
    private String lastName;
    private int age;
    private boolean needsMedicalAssistance;

    public Person(UUID id, String firstName, String lastName, int age, boolean needsMedicalAssistance) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.needsMedicalAssistance = needsMedicalAssistance;
    }

    public Person(String firstName, String lastName, int age) {
        this(UUID.randomUUID(), firstName, lastName, age, false);
    }

    public UUID getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getFullName() { return firstName + " " + lastName; }
    public int getAge() { return age; }
    public boolean needsMedicalAssistance() { return needsMedicalAssistance; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setAge(int age) { this.age = age; }
    public void setNeedsMedicalAssistance(boolean needsMedicalAssistance) {
        this.needsMedicalAssistance = needsMedicalAssistance;
    }
}
