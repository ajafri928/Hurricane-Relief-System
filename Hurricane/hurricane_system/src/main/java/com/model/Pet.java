package com.model;

import java.util.UUID;
import java.util.Objects;

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

    public UUID getID() { return id; }
    public String getName() { return name; }
    public String getSpecies() { return species; }
    
    public void setName(String name) { this.name = name; }
    public void setSpecies(String species) { this.species = species; }
}
