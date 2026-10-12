package com.model;

import java.time.LocalDateTime;

public class ShelterResource {
    private String type;
    private int quantity;
    private LocalDateTime availableUntil;
    private int lowStockThreshold;
    private String unit;

    public ShelterResource(String type, int quantity){
        this.type = type;
        this.quantity = quantity;
    }

    public ShelterResource(String type, int quantity, LocalDateTime availableUntil, int lowStockThreshold, String unit){
        this.type = type;
        this.quantity = quantity;
        this.availableUntil = availableUntil;
        this.lowStockThreshold = lowStockThreshold;
        this.unit = unit;
    }

    public boolean checkStockLevel (){ //Q: This should probably be a string?
        return (quantity <= lowStockThreshold) ? true : false;
    }

// Getters
public String getType() { return type; }
public int getQuantity() { return quantity; }
public LocalDateTime getAvailableUntil() { return availableUntil; }
public int getLowStockThreshold() { return lowStockThreshold; }
public String getUnit() { return unit; }

// Setters
public void setQuantity(int quantity) {
    if (quantity < 0) {
        throw new IllegalArgumentException("Quantity can't be negative");
    }
    this.quantity = quantity;
}

public void setAvailableUntil(LocalDateTime availableUntil) { this.availableUntil = availableUntil; }
public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }    

}
