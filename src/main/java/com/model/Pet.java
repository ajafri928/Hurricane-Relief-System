package com.model;

import java.util.Objects;
import java.util.UUID;

/** A pet included in a relief request or staying at a shelter. */
public class Pet {
    private UUID Id;
    private String name;
    private String species;

    /** @param Id id @param name name @param species species */
    public Pet(UUID Id, String name, String species){
        this.Id = Id;
        this.name = name;
        this.species = species;
    }

    /** @param name name @param species species */
    public Pet(String name, String species){
        this(UUID.randomUUID(), name, species);
    }

    /** @param o object to compare @return true if same id */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet other = (Pet) o;
        return Objects.equals(Id, other.Id);
    }

    /** @return hash of the id */
    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }

    /** @return name and species */
    @Override
    public String toString() {
        return name + " (" + species + ")";
    }

    /** @return id */
    public UUID getId() { return Id; }
    /** @return name */
    public String getName() { return name; }
    /** @return species */
    public String getSpecies() { return species; }

    /** @param name name */
    public void setName(String name) { this.name = name; }
    /** @param species species */
    public void setSpecies(String species) { this.species = species; }
}