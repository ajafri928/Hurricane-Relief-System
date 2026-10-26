package com.model;
import java.util.Objects;
import java.util.UUID;

/** A person included in a relief request or staying at a shelter. */
public class Person {
    private final UUID Id;
    private String firstName;
    private String lastName;
    private int age;
    private boolean needsMedicalAssistance;

    /** @param Id id @param firstName first name @param lastName last name @param age age @param needsMedicalAssistance if they need medical help */
    public Person(UUID Id, String firstName, String lastName, int age, boolean needsMedicalAssistance) {
        this.Id = Id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.needsMedicalAssistance = needsMedicalAssistance;
    }

    /** @param firstName first name @param lastName last name @param age age */
    public Person(String firstName, String lastName, int age) {
        this(UUID.randomUUID(), firstName, lastName, age, false);
    }

    /** @param o object to compare @return true if same id */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person other = (Person) o;
        return Objects.equals(Id, other.Id);
    }

    /** @return hash of the id */
    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }

    /** @return id */
    public UUID getId() { return Id; }
    /** @return first name */
    public String getFirstName() { return firstName; }
    /** @return last name */
    public String getLastName() { return lastName; }
    /** @return first and last name */
    public String getFullName() { return firstName + " " + lastName; }
    /** @return age */
    public int getAge() { return age; }
    /** @return if they need medical help */
    public boolean needsMedicalAssistance() { return needsMedicalAssistance; }

    /** @param firstName first name */
    public void setFirstName(String firstName) { this.firstName = firstName; }
    /** @param lastName last name */
    public void setLastName(String lastName) { this.lastName = lastName; }
    /** @param age age */
    public void setAge(int age) { this.age = age; }
    /** @param needsMedicalAssistance if they need medical help */
    public void setNeedsMedicalAssistance(boolean needsMedicalAssistance) {
        this.needsMedicalAssistance = needsMedicalAssistance;
    }
}