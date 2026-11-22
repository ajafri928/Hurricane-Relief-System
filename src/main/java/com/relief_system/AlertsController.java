package com.relief_system;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

import com.model.HurricaneEvent;
import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;
import com.model.ReliefSystem;
import com.model.User;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class AlertsController {
    private static final String ICON_STORM = "M8 1 A7 7 0 1 0 8 15 A7 7 0 1 0 8 1 Z M8 5 A3 3 0 1 0 8 11 A3 3 0 1 0 8 5 Z M3 3 L5.9 5.9 M13 3 L10.1 5.9 M3 13 L5.9 10.1 M13 13 L10.1 10.1";
    private static final String ICON_CLOCK = "M8 1.5 A6.5 6.5 0 1 0 8 14.5 A6.5 6.5 0 1 0 8 1.5 Z M8 4.5 V8 L10.5 9.5";
    private static final String ICON_PERSON = "M8 7.5 A3 3 0 1 0 8 1.5 A3 3 0 1 0 8 7.5 Z M2 15 C2.4 11.8 4.8 10.3 8 10.3 C11.2 10.3 13.6 11.8 14 15";
    private static final String ICON_CHECK = "M2.5 8.5 L6.5 12.5 L13.5 4";
    private static final String ICON_WARNING = "M8 1.5 L15 14 H1 Z M8 6 V9.5 M8 11.8 V12";

    private record Alert(String section, String color, String icon, String title, String message, LocalDateTime time) {
    }

    @FXML
    private VBox alertsList;
    @FXML
    private Button allTab;
    @FXML
    private Button urgentTab;
    @FXML
    private Button myRequestTab;
    @FXML
    private NavController navController;

    private final ArrayList<Alert> alerts = new ArrayList<>();
    private String filter = "ALL";

    @FXML
    private void initialize() {
        navController.setActive(NavController.ALERTS);
        buildAlerts();
        redraw();
    }

    private void buildAlerts() {
        ReliefSystem facade = ReliefSystem.getInstance();

        HurricaneEvent hurricane = facade.getActiveHurricane();
        if (hurricane != null) {
            String name = hurricane.getName().replaceFirst("(?i)^hurricane\\s+", "");
            alerts.add(new Alert("URGENT", "alert-orange", ICON_STORM,
                    name + " is now Category " + hurricane.getCategory(),
                    "Stay alert and be ready to move to a shelter.", null));
        }

        if (facade.getCurrentUser() != null) {
            for (ReliefRequest request : facade.getMyRequests()) {
                addRequestAlerts(request);
            }
        }

        alerts.sort(Comparator.comparing((Alert a) -> sectionOrder(a.section()))
                .thenComparing(Alert::time, Comparator.nullsLast(Comparator.reverseOrder())));
    }

    private void addRequestAlerts(ReliefRequest request) {
        ReliefRequestStatus status = request.getStatus();
        String section = RequestFormat.isFinished(status) ? "EARLIER" : "YOUR REQUEST";
        String what = request.getAssistance() == null ? "Your request" : request.getAssistance();
        User volunteer = request.getResponder();
        String name = volunteer == null ? "A volunteer" : volunteer.getFirstName();

        alerts.add(new Alert(section, section.equals("EARLIER") ? "alert-grey" : "alert-blue", ICON_CLOCK,
                "Request submitted", what, RequestFormat.parse(request.getCreatedAt())));

        if (status == ReliefRequestStatus.ASSIGNED) {
            alerts.add(new Alert(section, "alert-green", ICON_PERSON, "Volunteer assigned",
                    name + " accepted your request.", null));
        } else if (status == ReliefRequestStatus.HELP_EN_ROUTE) {
            alerts.add(new Alert(section, "alert-green", ICON_CHECK, "Volunteer on the way",
                    name + " is on the way to you.", null));
        } else if (status == ReliefRequestStatus.COMPLETE) {
            alerts.add(new Alert(section, "alert-grey", ICON_CHECK, "Request complete", what, null));
        } else if (status == ReliefRequestStatus.ESCALATED_TO_911) {
            alerts.add(new Alert("URGENT", "alert-red", ICON_WARNING, "Sent to 911",
                    what + " was escalated to emergency services.", null));
        }
    }

    private int sectionOrder(String section) {
        return switch (section) {
            case "URGENT" -> 0;
            case "YOUR REQUEST" -> 1;
            default -> 2;
        };
    }

    @FXML
    private void showAll() {
        setFilter("ALL", allTab);
    }

    @FXML
    private void showUrgent() {
        setFilter("URGENT", urgentTab);
    }

    @FXML
    private void showMyRequest() {
        setFilter("MY_REQUEST", myRequestTab);
    }

    private void setFilter(String newFilter, Button tab) {
        filter = newFilter;
        for (Button b : new Button[] { allTab, urgentTab, myRequestTab }) {
            b.getStyleClass().remove("filter-tab-selected");
        }
        tab.getStyleClass().add("filter-tab-selected");
        redraw();
    }

    private void redraw() {
        alertsList.getChildren().clear();
        String currentSection = null;
        for (Alert alert : alerts) {
            boolean urgent = alert.section().equals("URGENT");
            if (filter.equals("URGENT") && !urgent || filter.equals("MY_REQUEST") && urgent) {
                continue;
            }
            if (!alert.section().equals(currentSection)) {
                currentSection = alert.section();
                Label heading = new Label(currentSection);
                heading.getStyleClass().add("alert-section");
                if (urgent) {
                    heading.getStyleClass().add("alert-section-urgent");
                }
                alertsList.getChildren().add(heading);
            }
            alertsList.getChildren().add(buildCard(alert));
        }
        if (currentSection == null) {
            Label empty = new Label("No alerts right now.");
            empty.getStyleClass().add("empty-body");
            alertsList.getChildren().add(empty);
        }
    }

    private HBox buildCard(Alert alert) {
        Region bar = new Region();
        bar.getStyleClass().add("alert-bar");

        SVGPath icon = new SVGPath();
        icon.setContent(alert.icon());
        icon.getStyleClass().add("alert-icon");
        StackPane iconBox = new StackPane(icon);
        iconBox.getStyleClass().add("alert-icon-box");
        iconBox.setMinSize(36, 36);
        iconBox.setMaxSize(36, 36);

        Label title = new Label(alert.title());
        title.getStyleClass().add("alert-title");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Label time = new Label(alert.time() == null ? "" : RequestFormat.shortTime(alert.time()));
        time.getStyleClass().add("alert-time");
        HBox titleRow = new HBox(6, title, spacer, time);
        titleRow.setAlignment(Pos.CENTER_LEFT);

        Label message = new Label(alert.message());
        message.getStyleClass().add("alert-message");
        message.setWrapText(true);

        VBox text = new VBox(3, titleRow, message);
        HBox.setHgrow(text, Priority.ALWAYS);
        HBox body = new HBox(12, iconBox, text);
        body.getStyleClass().add("alert-body");
        HBox.setHgrow(body, Priority.ALWAYS);

        HBox card = new HBox(bar, body);
        card.getStyleClass().addAll("alert-card", alert.color());
        return card;
    }
}