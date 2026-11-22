package com.relief_system;

import com.model.Person;
import com.model.Pet;
import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;
import com.model.ReliefSystem;
import com.model.Shelter;
import com.model.User;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class RequestDetailsController {
    private static final String[] STEPS = { "Request submitted", "Volunteer assigned", "Help en route", "Complete" };

    private final ReliefSystem facade = ReliefSystem.getInstance();
    private ReliefRequest request;

    @FXML
    private Label requestIdLabel;
    @FXML
    private HBox titleRow;
    @FXML
    private Label subtitleLabel;
    @FXML
    private Label submittedLabel;
    @FXML
    private VBox timeline;
    @FXML
    private VBox volunteerCard;
    @FXML
    private Label volunteerInitials;
    @FXML
    private Label volunteerName;
    @FXML
    private Label volunteerRole;
    @FXML
    private VBox shelterCard;
    @FXML
    private Label shelterName;
    @FXML
    private Label shelterAddress;
    @FXML
    private Label petsChip;
    @FXML
    private Label medicalChip;
    @FXML
    private Label spotsChip;
    @FXML
    private VBox peopleCard;
    @FXML
    private VBox peopleList;
    @FXML
    private Label cancelLink;
    @FXML
    private VBox cancelOverlay;
    @FXML
    private Label cancelText;
    @FXML
    private Label cancelSummary;

    @FXML
    private void initialize() {
        request = facade.getSelectedRequest();
        if (request == null) {
            App.setRoot("my-requests");
            return;
        }
        ReliefRequestStatus status = request.getStatus();
        String[] parts = String.valueOf(request.getAssistance()).split(" - ", 2);

        requestIdLabel.setText("Request " + RequestFormat.displayId(request.getId()));
        Label title = Ui.label(parts[0], "section-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);
        titleRow.getChildren().addAll(title, Ui.chip(status));
        subtitleLabel.setText(parts.length > 1 ? parts[1] : "");
        Ui.show(subtitleLabel, parts.length > 1);
        submittedLabel.setText(Ui.submitted(request));
        buildTimeline(status);

        User responder = request.getResponder();
        Ui.show(volunteerCard, responder != null);
        if (responder != null) {
            volunteerInitials.setText(initials(responder));
            volunteerName.setText(RequestFormat.fullName(responder));
            volunteerRole.setText(responder.getSpecialty() == null ? "Volunteer"
                    : responder.getSpecialty().name().replace('_', ' '));
        }

        Shelter shelter = request.getDestinationShelter();
        Ui.show(shelterCard, shelter != null);
        if (shelter != null) {
            shelterName.setText(shelter.getName());
            shelterAddress.setText(shelter.getLocation() == null ? "" : shelter.getLocation().formatAddress());
            Ui.show(petsChip, shelter.isPetFriendly());
            Ui.show(medicalChip, shelter.hasMedicalStaff());
            spotsChip.setText(shelter.getAvailableSpaces() + " spots open");
        }

        for (Person person : request.getRequestedPersons()) {
            String text = person.getFullName() + ", " + person.getAge();
            if (person.needsMedicalAssistance()) {
                text += " · Needs medical help";
            }
            peopleList.getChildren().add(Ui.label(text, "field-heading"));
        }
        for (Pet pet : request.getPets()) {
            peopleList.getChildren().add(Ui.label(pet.getName() + " — " + pet.getSpecies(), "field-heading"));
        }
        Ui.show(peopleCard, !peopleList.getChildren().isEmpty());

        Ui.show(cancelLink, !RequestFormat.isFinished(status));
    }

    private String initials(User user) {
        String first = user.getFirstName() == null || user.getFirstName().isEmpty() ? "" : user.getFirstName().substring(0, 1);
        String last = user.getLastName() == null || user.getLastName().isEmpty() ? "" : user.getLastName().substring(0, 1);
        return (first + last).toUpperCase();
    }

    private void buildTimeline(ReliefRequestStatus status) {
        int reached = switch (status) {
            case SUBMITTED, APPROVED -> 0;
            case ASSIGNED -> 1;
            case HELP_EN_ROUTE -> 2;
            case COMPLETE -> 3;
            default -> -1;
        };
        Ui.show(timeline, reached >= 0);
        for (int i = 0; i < STEPS.length && reached >= 0; i++) {
            boolean done = i < reached || reached == STEPS.length - 1;
            boolean current = i == reached && !done;

            StackPane dot = new StackPane();
            dot.getStyleClass().add("timeline-dot");
            dot.setMinSize(24, 24);
            dot.setMaxSize(24, 24);
            if (done) {
                dot.getStyleClass().add("timeline-done");
                SVGPath check = new SVGPath();
                check.setContent(RequestFormat.ICON_CHECK);
                check.getStyleClass().add("timeline-check");
                check.setScaleX(0.6);
                check.setScaleY(0.6);
                dot.getChildren().add(check);
            } else if (current) {
                dot.getStyleClass().add("timeline-current");
                StackPane inner = new StackPane();
                inner.getStyleClass().add("timeline-inner-dot");
                inner.setMinSize(8, 8);
                inner.setMaxSize(8, 8);
                dot.getChildren().add(inner);
            }

            Label name = new Label(STEPS[i]);
            name.getStyleClass().add("timeline-title");
            HBox row = new HBox(12, dot, name);
            row.setAlignment(Pos.CENTER_LEFT);
            timeline.getChildren().add(row);

            if (i < STEPS.length - 1) {
                StackPane line = new StackPane();
                line.getStyleClass().add(i < reached ? "timeline-line-done" : "timeline-line");
                line.setMinSize(2, 16);
                line.setMaxSize(2, 16);
                VBox.setMargin(line, new javafx.geometry.Insets(0, 0, 0, 11));
                timeline.getChildren().add(line);
            }
        }
    }

    @FXML
    private void openCancel() {
        User responder = request.getResponder();
        cancelText.setText(responder == null ? "This will cancel your request."
                : RequestFormat.fullName(responder) + " is already assigned to this request. Cancelling will end it.");
        cancelSummary.setText(String.valueOf(request.getAssistance()).replace(" - ", " · ") + "\n"
                + RequestFormat.peopleAndPets(request) + " · " + Ui.submitted(request));
        Ui.show(cancelOverlay, true);
    }

    @FXML
    private void closeCancel() {
        Ui.show(cancelOverlay, false);
    }

    @FXML
    private void confirmCancel() {
        facade.cancelRequest();
        App.setRoot("my-requests");
    }

    @FXML
    private void back() {
        App.setRoot("my-requests");
    }
}
