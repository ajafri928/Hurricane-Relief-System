package com.model;

import java.util.ArrayList;

public class ShelterDriver {
    public static void main(String[] args){

        ArrayList<Shelter> shelters = DataLoader.getShelters();

        System.out.println("Loaded " + shelters.size() + " shelters from JSON file.");
        for (Shelter shelter: shelters){
            System.out.println(shelter);
        }

        shelters.get(0).checkIn("tester");

        DataWriter.saveShelters(shelters);

        ArrayList<Shelter> new_tester = DataLoader.getShelters();
        System.out.println(new_tester.get(0).getOccupancy());
    }
}