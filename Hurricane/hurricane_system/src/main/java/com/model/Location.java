package com.model;

import com.location.Address;



/**
 * A place in the system, as a street address, GPS coordinates, or both.
 */
public class Location {
    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String zipCode;
    private double latitude;
    private double longitude;

    /**
     * Makes a location from a street address.
     *
     * @param addressLine1 the street address
     * @param city         the city
     * @param state        the state
     * @param zipCode      the ZIP code
     */
    public Location(String addressLine1, String city, String state, String zipCode) {
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    /**
     * Makes a GPS-only location with no street address.
     *
     * @param latitude  the latitude
     * @param longitude the longitude
     */
    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Makes a location with a full address and GPS coordinates (used by DataLoader).
     *
     * @param addressLine1 the street address
     * @param addressLine2 apartment, suite, or building (can be null)
     * @param city         the city
     * @param state        the state
     * @param zipCode      the ZIP code
     * @param latitude     the latitude
     * @param longitude    the longitude
     */
    public Location(String addressLine1, String addressLine2, String city, String state, String zipCode, double latitude, double longitude) {
        this.addressLine1 = addressLine1;
        this.addressLine2 = addressLine2;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Formats the address on one line, skipping any parts that are missing.
     * If there is no address at all, returns the GPS coordinates instead.
     *
     * @return the address, like "123 Main St, Columbia, SC 29201"
     */
    public String formatAddress() {
        StringBuilder sb = new StringBuilder();
        if (addressLine1 != null && !addressLine1.isEmpty()) {
            sb.append(addressLine1);
        }
        if (addressLine2 != null && !addressLine2.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(addressLine2);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        if (state != null && !state.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(state);
        }
        if (zipCode != null && !zipCode.isEmpty()) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(zipCode);
        }
        // GPS-only location: no address parts, so show the coordinates instead
        if (sb.length() == 0) {
            return latitude + ", " + longitude;
        }
        return sb.toString();
    }

    /** @return the latitude */
    public double getLatitude() { return latitude; }
    /** @return the longitude */
    public double getLongitude() { return longitude; }
    /** @return the city */
    public String getCity() { return city; }
    /** @return the street address */
    public String getAddressLine1() { return addressLine1; }
    /** @return the second address line, or null */
    public String getAddressLine2() { return addressLine2; }
    /** @return the state */
    public String getState() { return state; }
    /** @return the ZIP code */
    public String getZipCode() { return zipCode; }

    /**
     * Converts this location to an Address for the map service.
     *
     * @return the matching Address
     */
    public Address toAddress(){
        return new Address(addressLine1, city, state, zipCode);
    }

    @Override
    /** @return the formatted address */
    public String toString() {
        return formatAddress();
    }
}