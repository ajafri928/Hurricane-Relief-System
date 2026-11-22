package com.relief_system;

import java.util.ArrayList;
import java.util.Comparator;

import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;
import com.model.ReliefSystem;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public class MyRequestsController {
    private enum Filter { ALL, ACTIVE, COMPLETED }

    private final ReliefSystem facade = ReliefSystem.getInstance();
    private final ArrayList<ReliefRequest> requests = new ArrayList<>();

    @FXML
    private NavController navController;
    @FXML
    private Label summaryLabel;
    @FXML
    private Button allTab;
    @FXML
    private Button activeTab;
    @FXML
    private Button completedTab;
    @FXML
    private VBox cardList;
    @FXML
    private VBox emptyState;
    @FXML
    private Label emptyBody;

    @FXML
    private void initialize() {
        navController.setActive(NavController.MY_REQUESTS);
        requests.addAll(facade.getMyRequests());
        requests.sort(Comparator.comparing(ReliefRequest::getCreatedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        int active = 0;
        int completed = 0;
        for (ReliefRequest request : requests) {
            if (!RequestFormat.isFinished(request.getStatus())) {
                active++;
            }
            if (request.getStatus() == ReliefRequestStatus.COMPLETE) {
                completed++;
            }
        }
        summaryLabel.setText(active + " active · " + completed + " completed");
        applyFilter(Filter.ALL);
    }

    @FXML
    private void showAll() {
        applyFilter(Filter.ALL);
    }

    @FXML
    private void showActive() {
        applyFilter(Filter.ACTIVE);
    }

    @FXML
    private void showCompleted() {
        applyFilter(Filter.COMPLETED);
    }

    private void applyFilter(Filter filter) {
        select(allTab, filter == Filter.ALL);
        select(activeTab, filter == Filter.ACTIVE);
        select(completedTab, filter == Filter.COMPLETED);

        cardList.getChildren().clear();
        for (ReliefRequest request : requests) {
            boolean finished = RequestFormat.isFinished(request.getStatus());
            boolean match = switch (filter) {
                case ALL -> true;
                case ACTIVE -> !finished;
                case COMPLETED -> finished;
            };
            if (match) {
                cardList.getChildren().add(card(request));
            }
        }

        boolean empty = cardList.getChildren().isEmpty();
        emptyBody.setText(requests.isEmpty() ? "When you ask for help, your requests show up here."
                : "Nothing matches this filter.");
        Ui.show(emptyState, empty);
        Ui.show(cardList, !empty);
    }

    private void select(Button tab, boolean selected) {
        tab.getStyleClass().remove("filter-tab-selected");
        if (selected) {
            tab.getStyleClass().add("filter-tab-selected");
        }
    }

    private VBox card(ReliefRequest request) {
        String[] parts = String.valueOf(request.getAssistance()).split(" - ", 2);

        Label title = Ui.label(parts[0], "section-title");
        HBox.setHgrow(title, Priority.ALWAYS);
        title.setMaxWidth(Double.MAX_VALUE);
        HBox top = new HBox(8, title, Ui.chip(request.getStatus()));
        top.setAlignment(Pos.TOP_LEFT);

        VBox card = new VBox(6, top);
        card.getStyleClass().add("white-card");
        if (parts.length > 1) {
            card.getChildren().add(Ui.label(parts[1], "flow-subheading"));
        }
        if (request.getLocation() != null) {
            card.getChildren().add(Ui.label(request.getLocation().formatAddress(), "hint"));
        }
        card.getChildren().add(Ui.label(RequestFormat.peopleAndPets(request), "hint"));
        card.getChildren().add(new Separator());

        SVGPath chevron = new SVGPath();
        chevron.setContent("M5 2 L10 7 L5 12");
        chevron.getStyleClass().add("chevron");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(8, Ui.label(Ui.submitted(request), "hint"), spacer, chevron);
        footer.setAlignment(Pos.CENTER_LEFT);
        card.getChildren().add(footer);

        card.setOnMouseClicked(event -> {
            facade.selectRequest(request.getId());
            App.setRoot("request-details");
        });
        return card;
    }

    @FXML
    private void requestHelp() {
        App.setRoot("request-help");
    }

    @FXML
    private void findShelter() {
        App.setRoot("shelters");
    }
}
