package com.relief_system;

import java.util.ArrayList;

import com.model.HurricaneEvent;
import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;
import com.model.ReliefSystem;
import com.model.SafetyCategory;
import com.model.User;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class HomeController {
    private static final String BADGE_CHECK = "M2 7.5 L5.5 11 L12 3.5";
    private static final String BADGE_TRIANGLE = "M7 1.5 L13 12.5 H1 Z M7 5.5 V8.5 M7 10.3 V10.6";
    private static final String BADGE_ALERT = "M7 1 A6 6 0 1 0 7 13 A6 6 0 1 0 7 1 Z M7 4 V7.8 M7 9.8 V10.1";

    @FXML
    private Label greetingLabel;
    @FXML
    private HBox safetyBadge;
    @FXML
    private SVGPath safetyIcon;
    @FXML
    private Label safetyText;

    @FXML
    private VBox requestCard;
    @FXML
    private Label requestHeader;
    @FXML
    private Label requestTitle;
    @FXML
    private HBox statusChip;
    @FXML
    private SVGPath statusIcon;
    @FXML
    private Label statusText;
    @FXML
    private Label peopleLabel;
    @FXML
    private HBox volunteerRow;
    @FXML
    private Label volunteerName;
    @FXML
    private HBox etaRow;
    @FXML
    private Label requestMessage;

    @FXML
    private HBox hurricaneCard;
    @FXML
    private Label hurricaneName;
    @FXML
    private Label hurricaneStatus;

    @FXML
    private VBox noRequestSection;
    @FXML
    private Button imSafeButton;
    @FXML
    private HBox markedSafeBox;

    @FXML
    private NavController navController;

    private ReliefSystem facade = ReliefSystem.getInstance();

    private ReliefRequest shownRequest;

    @FXML
    private void initialize() {
        navController.setActive(NavController.HOME);
        refresh();
    }

    private void refresh() {
        User user = facade.getCurrentUser();
        if (user == null) {
            return;
        }

        greetingLabel.setText("Hi, " + user.getFirstName());
        showSafety(user.getSafety());
        showRequest(pickRequestToShow(facade.getMyRequests()));
        showHurricane(facade.getActiveHurricane());
    }

    private void showSafety(SafetyCategory safety) {
        if (safety == null) {
            safety = SafetyCategory.CAUTION;
        }
        switch (safety) {
            case SAFE -> setBadge("Safe", "badge-safe", BADGE_CHECK);
            case CAUTION -> setBadge("Caution", "badge-caution", BADGE_TRIANGLE);
            case HIGH_RISK -> setBadge("High Risk", "badge-high-risk", BADGE_TRIANGLE);
            case IN_IMMEDIATE_DANGER -> setBadge("Danger", "badge-danger", BADGE_ALERT);
        }

        boolean isSafe = safety == SafetyCategory.SAFE;
        setShown(markedSafeBox, isSafe);
        setShown(imSafeButton, !isSafe);
    }

    private void setBadge(String text, String colorClass, String icon) {
        safetyBadge.getStyleClass().removeAll("badge-safe", "badge-caution", "badge-high-risk", "badge-danger");
        safetyBadge.getStyleClass().add(colorClass);
        safetyText.setText(text);
        safetyIcon.setContent(icon);
    }

    private ReliefRequest pickRequestToShow(ArrayList<ReliefRequest> requests) {
        ReliefRequest newestActive = null;
        ReliefRequest newestFinished = null;
        for (ReliefRequest request : requests) {
            if (RequestFormat.isFinished(request.getStatus())) {
                if (isNewer(request, newestFinished)) {
                    newestFinished = request;
                }
            } else if (isNewer(request, newestActive)) {
                newestActive = request;
            }
        }
        return newestActive != null ? newestActive : newestFinished;
    }

    private boolean isNewer(ReliefRequest a, ReliefRequest b) {
        if (b == null) {
            return true;
        }
        String timeA = a.getCreatedAt() == null ? "" : a.getCreatedAt();
        String timeB = b.getCreatedAt() == null ? "" : b.getCreatedAt();
        return timeA.compareTo(timeB) > 0;
    }

    private void showRequest(ReliefRequest request) {
        shownRequest = request;
        setShown(requestCard, request != null);
        setShown(noRequestSection, request == null);
        if (request == null) {
            return;
        }

        ReliefRequestStatus status = request.getStatus();
        boolean finished = RequestFormat.isFinished(status);
        requestHeader.setText(finished ? "Recent Request" : "Your Active Request");
        requestTitle.setText(request.getAssistance());
        peopleLabel.setText(RequestFormat.peopleAndPets(request));

        RequestFormat.StatusChip chip = RequestFormat.chipFor(status);
        statusChip.getStyleClass().removeAll("chip-pending", "chip-assigned", "chip-complete", "chip-danger");
        statusChip.getStyleClass().add(chip.styleClass());
        statusText.setText(chip.text());
        statusIcon.setContent(chip.icon());

        boolean hasVolunteer = status == ReliefRequestStatus.ASSIGNED || status == ReliefRequestStatus.HELP_EN_ROUTE;
        User volunteer = request.getResponder();
        setShown(volunteerRow, hasVolunteer);
        setShown(etaRow, hasVolunteer);
        if (hasVolunteer) {
            volunteerName.setText(volunteer == null ? "Not available" : RequestFormat.fullName(volunteer));
        }

        requestMessage.setText(messageFor(status, volunteer));
    }

    private String messageFor(ReliefRequestStatus status, User volunteer) {
        String name = volunteer == null ? "A volunteer" : volunteer.getFirstName();
        if (status == null) {
            return "Your request is submitted. No volunteer is assigned yet.";
        }
        return switch (status) {
            case SUBMITTED, APPROVED -> "Your request is submitted. No volunteer is assigned yet.";
            case ASSIGNED -> name + " has accepted your request.";
            case HELP_EN_ROUTE -> name + " is on the way to your pick up location.";
            case COMPLETE -> "Your request has been marked complete.";
            case ESCALATED_TO_911 -> "Your request was sent to 911 emergency services.";
            case DENIED -> "Your request couldn't be accepted. You can submit a new one.";
        };
    }

    private void showHurricane(HurricaneEvent hurricane) {
        setShown(hurricaneCard, hurricane != null);
        if (hurricane == null) {
            return;
        }
        hurricaneName.setText(hurricane.getName());
        hurricaneStatus.setText("Category " + hurricane.getCategory() + " - " + titleCase(hurricane.getStatus()));
    }

    @FXML
    private void handleImSafe() {
        facade.markUserSafe(SafetyCategory.SAFE);
        refresh();
    }

    @FXML
    private void handleGetHelp() {
        App.setRoot(NavController.GET_HELP);
    }

    @FXML
    private void handleViewRequest() {
        if (shownRequest == null) {
            return;
        }
        facade.selectRequest(shownRequest.getId());
        App.setRoot("request-details");
    }

    private void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    private String titleCase(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        String lower = text.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }
}