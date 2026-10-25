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

    
}
