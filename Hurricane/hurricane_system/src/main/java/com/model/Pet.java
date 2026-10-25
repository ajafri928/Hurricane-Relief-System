package com.model;

import java.util.UUID;
import java.util.Objects;

public class Pet {
    private UUID Id;
    private String name;
    private String species;

    public Pet(UUID Id, String name, String species){
        this.Id = Id;
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
        return Objects.equals(Id, other.Id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id);
    }

    @Override
    public String toString() {
        return name + " (" + species + ")";
    }

    //Getters
    public UUID getId() { return Id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }

    //Setters
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
}
