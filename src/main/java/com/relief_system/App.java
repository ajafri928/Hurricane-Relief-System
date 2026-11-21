package com.relief_system;

import java.io.IOException;
import java.net.URL;

import com.model.ReliefSystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class App extends Application {
    private static final String START_SCREEN = "welcome";

        private static final String[] DEV_LOGIN = null;

    private static Scene scene;

    @Override
    public void start(Stage stage) {
        String firstScreen = START_SCREEN;
        if (DEV_LOGIN != null && ReliefSystem.getInstance().signIn(DEV_LOGIN[0], DEV_LOGIN[1])) {
            firstScreen = "home";
        }

        Parent root = loadFXML(firstScreen);
        if (root == null) {
            root = new Label("Screen \"" + firstScreen + "\" isn't built yet.");
        }
        scene = new Scene(root, 400, 760);
        stage.setTitle("Lifeline Link");
        stage.setScene(scene);
        stage.show();
    }
        @Override
    public void stop() {
        if (ReliefSystem.getInstance().getCurrentUser() != null) {
            ReliefSystem.getInstance().signOut();
        }
    }
    public static boolean setRoot(String fxml) {
        Parent root = loadFXML(fxml);
        if (root == null) {
            return false;
        }
        scene.setRoot(root);
        return true;
    }

    private static Parent loadFXML(String fxml) {
        URL url = App.class.getResource(fxml + ".fxml");
        if (url == null) {
            System.err.println("Screen \"" + fxml + "\" isn't built yet (no " + fxml + ".fxml).");
            return null;
        }
        try {
            return new FXMLLoader(url).load();
        } catch (IOException e) {
            System.err.println("Couldn't open screen \"" + fxml + "\":");
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch();
    }
}