package com.relief_system;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.model.ReliefRequest;
import com.model.ReliefSystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RequestSubmittedController {
    @FXML
    private Label requestIdLabel;
    @FXML
    private Label submittedTimeLabel;
    @FXML
    private NavController navController;

    static ReliefRequest lastSubmitted;

    @FXML
    private void initialize() {
        navController.setActive(NavController.GET_HELP);

        ReliefRequest request = lastSubmitted;
        if (request == null) {
            return;
        }
        requestIdLabel.setText(RequestFormat.displayId(request.getId()));

        LocalDateTime time = RequestFormat.parse(request.getCreatedAt());
        if (time != null) {
            String clock = DateTimeFormatter.ofPattern("h:mm a").format(time);
            boolean today = time.toLocalDate().equals(LocalDate.now());
            submittedTimeLabel.setText((today ? "Today" : DateTimeFormatter.ofPattern("MMM d").format(time)) + " at " + clock);
        }

    }

    @FXML
    private void handleViewRequest() {
        if (lastSubmitted != null) {
            ReliefSystem.getInstance().selectRequest(lastSubmitted.getId());
        }
        App.setRoot("request-details");
    }

    @FXML
    private void handleBackToHome() {
        App.setRoot(NavController.HOME);
    }
}