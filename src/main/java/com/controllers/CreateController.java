package com.controllers;

import java.io.IOException;

import com.relief_system.App;

import javafx.fxml.FXML;

public class CreateController {

    @FXML
    private void switchToPrimary() throws IOException {
        App.setRoot("new");
    }
}
