/*package com.model;

import java.util.ArrayList;
import java.util.UUID;

public class RegionManagement {
    private static RegionManagement regionManagement;
    private ArrayList<GeographicRegion> regions;

    //private RegionManagement() {
    //  regions = DataLoader.getGeographicRegions();
    //}

    public static RegionManagement getInstance() {
        if (regionManagement == null) {
            regionManagement = new RegionManagement();
        }
        return regionManagement;
    }

    public ArrayList<GeographicRegion> getRegions() {
        return regions;
    }

    public GeographicRegion getRegion(UUID Id) {
        for (GeographicRegion region : regions) {
            if (region.getId().equals(Id)) {
                return region;
            }
        }
        return null;
    }

    public void addRegion(GeographicRegion region) {
        regions.add(region);
    }

    public void removeRegion(UUID id) {
        regions.removeIf(region -> region.getId().equals(id));
    }
    //public UUID getId() { return Id; }
}
*/
