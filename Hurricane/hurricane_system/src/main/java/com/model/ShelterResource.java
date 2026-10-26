package com.model;

import java.time.LocalDateTime;

/** A supply a shelter has, like water or blankets. */
public class ShelterResource {
    private String type;
    private int quantity;
    private LocalDateTime availableUntil;
    private int lowStockThreshold;
    private String unit;

    /** @param type resource type @param quantity amount on hand */
    public ShelterResource(String type, int quantity) {
        this.type = type;
        this.quantity = quantity;
    }

    /** @param type resource type @param quantity amount on hand @param availableUntil when it runs out @param lowStockThreshold low stock level @param unit unit of measure */
    public ShelterResource(String type, int quantity, LocalDateTime availableUntil, int lowStockThreshold,
            String unit) {
        this.type = type;
        this.quantity = quantity;
        this.availableUntil = availableUntil;
        this.lowStockThreshold = lowStockThreshold;
        this.unit = unit;
    }

    /** @return true if stock is at or below the threshold */
    public boolean checkStockLevel() {
        return quantity <= lowStockThreshold;
    }

    /** @return type */
    public String getType() {
        return type;
    }

    /** @return quantity */
    public int getQuantity() {
        return quantity;
    }

    /** @return when it runs out */
    public LocalDateTime getAvailableUntil() {
        return availableUntil;
    }

    /** @return low stock threshold */
    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    /** @return unit */
    public String getUnit() {
        return unit;
    }

    /** @param quantity new quantity, can't be negative */
    public void setQuantity(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity can't be negative");
        }
        this.quantity = quantity;
    }

    /** @param availableUntil when it runs out */
    public void setAvailableUntil(LocalDateTime availableUntil) {
        this.availableUntil = availableUntil;
    }

    /** @param lowStockThreshold low stock level */
    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }

}