package com.relief_system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.UUID;

import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;
import com.model.User;

public final class RequestFormat {
    public static final String ICON_CLOCK = "M7 1 A6 6 0 1 0 7 13 A6 6 0 1 0 7 1 Z M7 4 V7 L9.5 8.5";
    public static final String ICON_PERSON = "M7 6.5 A2.5 2.5 0 1 0 7 1.5 A2.5 2.5 0 1 0 7 6.5 Z M2 13 C2.3 10.2 4.3 9 7 9 C9.7 9 11.7 10.2 12 13";
    public static final String ICON_CAR = "M2 9.5 L3.2 5.5 C3.4 4.8 4 4.5 4.6 4.5 H9.4 C10 4.5 10.6 4.8 10.8 5.5 L12 9.5 V12 H2 Z M2 9.5 H12 M4 12 V13.2 M10 12 V13.2 M4.2 10.8 H4.3 M9.7 10.8 H9.8";
    public static final String ICON_CHECK = "M2 7.5 L5.5 11 L12 3.5";
    public static final String ICON_ALERT = "M7 1 A6 6 0 1 0 7 13 A6 6 0 1 0 7 1 Z M7 4 V7.8 M7 9.8 V10.1";

    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("h:mm a");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("MMM d");

    private RequestFormat() {
    }

    public record StatusChip(String text, String styleClass, String icon) {
    }

    public static String displayId(UUID id) {
        if (id == null) {
            return "#LL-00000";
        }
        return String.format("#LL-%05d", Math.abs(id.hashCode()) % 100000);
    }

    public static StatusChip chipFor(ReliefRequestStatus status) {
        if (status == null) {
            return new StatusChip("Pending match", "chip-pending", ICON_CLOCK);
        }
        return switch (status) {
            case SUBMITTED, APPROVED -> new StatusChip("Pending match", "chip-pending", ICON_CLOCK);
            case ASSIGNED -> new StatusChip("Assigned", "chip-assigned", ICON_PERSON);
            case HELP_EN_ROUTE -> new StatusChip("Help en route", "chip-assigned", ICON_CAR);
            case COMPLETE -> new StatusChip("Complete", "chip-complete", ICON_CHECK);
            case ESCALATED_TO_911 -> new StatusChip("Escalated to 911", "chip-danger", ICON_ALERT);
            case DENIED -> new StatusChip("Not accepted", "chip-danger", ICON_ALERT);
        };
    }

    public static boolean isFinished(ReliefRequestStatus status) {
        return status == ReliefRequestStatus.COMPLETE || status == ReliefRequestStatus.DENIED;
    }

    public static String peopleAndPets(ReliefRequest request) {
        int people = request.getRequestedPersons().size();
        String text = people + (people == 1 ? " person" : " people");
        int pets = request.getPets().size();
        if (pets == 1) {
            String species = request.getPets().get(0).getSpecies();
            text += " - 1 " + (species == null || species.isBlank() ? "pet" : species.toLowerCase());
        } else if (pets > 1) {
            text += " - " + pets + " pets";
        }
        return text;
    }

    public static String fullName(User user) {
        if (user == null) {
            return "";
        }
        return (user.getFirstName() + " " + user.getLastName()).trim();
    }

    public static String shortTime(LocalDateTime time) {
        if (time.toLocalDate().equals(LocalDate.now())) {
            return TIME.format(time);
        }
        return DATE.format(time);
    }

    public static LocalDateTime parse(String isoTime) {
        if (isoTime == null || isoTime.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(isoTime);
        } catch (DateTimeParseException e) {
            return null;
        }
    }
}
