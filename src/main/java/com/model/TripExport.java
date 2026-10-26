package com.model;
 
import java.io.FileWriter;
 
import com.location.Address;
import com.location.MapTools;
 
/**
 * Writes a printable trip sheet for a responder transporting a family
 * to a shelter. The sheet lists who is being moved, any pets, the pickup
 * and destination addresses, and driving directions for both legs.
 *
 * @author Sahil
 */
public class TripExport {
 
    /**
     * Writes a formatted trip sheet to the given file path.
     *
     * @param request   the relief request being fulfilled
     * @param responder the volunteer making the trip
     * @param shelter   the destination shelter
     * @param filePath  where to write the .txt file
     */
    public static void exportTrip(ReliefRequest request, User responder,
                                  Shelter shelter, String filePath) {
 
        StringBuilder sheet = new StringBuilder();
 
        sheet.append("========================================\n");
        sheet.append("          RELIEF TRIP SHEET\n");
        sheet.append("========================================\n\n");
 
        sheet.append("Request ID: ").append(request.getId()).append("\n");
        sheet.append("Responder:  ").append(responder.getFirstName())
             .append(" ").append(responder.getLastName()).append("\n\n");
 
        sheet.append("----------------------------------------\n");
        sheet.append("PEOPLE TO TRANSPORT\n");
        sheet.append("----------------------------------------\n");
 
        for (Person person : request.getRequestedPersons()) {
            sheet.append("  ").append(person.getFullName())
                 .append(", age ").append(person.getAge());
            if (person.needsMedicalAssistance()) {
                sheet.append("   ** NEEDS MEDICAL ASSISTANCE **");
            }
            sheet.append("\n");
        }
        sheet.append("  Total people: ")
             .append(request.getPeopleCount()).append("\n\n");
 
        if (request.getPets() != null && !request.getPets().isEmpty()) {
            sheet.append("----------------------------------------\n");
            sheet.append("ACCOMPANYING PETS\n");
            sheet.append("----------------------------------------\n");
            for (Pet pet : request.getPets()) {
                sheet.append("  ").append(pet.getName())
                     .append(" (").append(pet.getSpecies()).append(")\n");
            }
            sheet.append("\n");
        }
 
        sheet.append("----------------------------------------\n");
        sheet.append("PICKUP\n");
        sheet.append("----------------------------------------\n");
        sheet.append("  Address: ")
             .append(request.getLocation().formatAddress()).append("\n");
        sheet.append("  Contact: ").append(request.getContactPhone()).append("\n\n");
 
        sheet.append("----------------------------------------\n");
        sheet.append("DESTINATION SHELTER\n");
        sheet.append("----------------------------------------\n");
        sheet.append("  ").append(shelter.getName()).append("\n");
        sheet.append("  ").append(shelter.getLocation().formatAddress()).append("\n");
        sheet.append("  Pet friendly:  ").append(shelter.isPetFriendly()).append("\n");
        sheet.append("  Medical staff: ").append(shelter.hasMedicalStaff()).append("\n\n");
 
        appendDirections(sheet, "DIRECTIONS: YOU TO PICKUP",
                responder.getLocation(), request.getLocation());
 
        appendDirections(sheet, "DIRECTIONS: PICKUP TO SHELTER",
                request.getLocation(), shelter.getLocation());
 
        sheet.append("========================================\n");
 
        try {
            FileWriter writer = new FileWriter(filePath);
            writer.write(sheet.toString());
            writer.flush();
            writer.close();
            System.out.println("Trip sheet written to " + filePath);
        } catch (Exception e) {
            System.err.println("Could not write the trip sheet: " + e.getMessage());
        }
    }
 
    /**
     * Appends a titled block of driving directions between two locations.
     * If the map service cannot be reached the block says so rather than
     * failing the whole export.
     *
     * @param sheet the sheet being built
     * @param title the heading for this block
     * @param from  where the leg starts
     * @param to    where the leg ends
     */
    private static void appendDirections(StringBuilder sheet, String title,
                                         Location from, Location to) {
        sheet.append("----------------------------------------\n");
        sheet.append(title).append("\n");
        sheet.append("----------------------------------------\n");
 
        try {
            Address start = from.toAddress();
            Address end = to.toAddress();
            String[] steps = MapTools.getInstance().getDrivingDirections(start, end);
 
            for (int i = 0; i < steps.length; i++) {
                sheet.append("  ").append(i + 1).append(". ")
                     .append(steps[i]).append("\n");
            }
        } catch (Exception e) {
            sheet.append("  Directions unavailable: ")
                 .append(e.getMessage()).append("\n");
        }
        sheet.append("\n");
    }
}
 
