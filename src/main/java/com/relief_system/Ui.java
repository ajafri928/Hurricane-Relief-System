package com.relief_system;

import java.time.LocalDateTime;

import com.model.ReliefRequest;
import com.model.ReliefRequestStatus;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

final class Ui {
    private Ui() {
    }

    static void show(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    static void error(Control field, boolean error) {
        field.getStyleClass().remove("field-error");
        if (error) {
            field.getStyleClass().add("field-error");
        }
    }

    static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        label.setWrapText(true);
        return label;
    }

    static HBox chip(ReliefRequestStatus status) {
        RequestFormat.StatusChip chip = RequestFormat.chipFor(status);
        SVGPath icon = new SVGPath();
        icon.setContent(chip.icon());
        icon.getStyleClass().add("chip-icon");
        Label text = new Label(chip.text());
        text.getStyleClass().add("chip-text");
        HBox box = new HBox(6, icon, text);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().addAll("status-chip", "chip-small", chip.styleClass());
        return box;
    }

    static String submitted(ReliefRequest request) {
        LocalDateTime time = RequestFormat.parse(request.getCreatedAt());
        return time == null ? "Submitted: not available" : "Submitted " + RequestFormat.shortTime(time);
    }
}
