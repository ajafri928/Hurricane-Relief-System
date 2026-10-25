package com.model;

import java.util.Objects;
import java.util.UUID;

public class Pet {
    private UUID id;
    private String name;
    private String species;

    public Pet(UUID id, String name, String species){
        this.id = id;
        this.name = name;
        this.species = species;
    }

    public Pet(String name, String species){
        this(UUID.randomUUID(), name, species);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pet other = (Pet) o;
        return Objects.equals(id, other.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return name + " (" + species + ")";
    }

    //Getters
    public UUID getID() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }

    //Setters
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
}
