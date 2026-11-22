package com.relief_system;

import java.util.ArrayList;

import com.location.Address;
import com.location.Coordinates;
import com.location.MapTools;
import com.model.Location;
import com.model.Person;
import com.model.Pet;
import com.model.ReliefRequest;
import com.model.ReliefSystem;
import com.model.UrgencyTier;
import com.model.User;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RequestHelpController {
    private static final String[] STEP_NAMES = { "Help needed", "Details", "Location", "Contact", "Review" };
    private static final int MAX_DESCRIPTION = 500;
    private static final int MAX_PEOPLE = 20;

    @FXML private Region progress1;
    @FXML private Region progress2;
    @FXML private Region progress3;
    @FXML private Region progress4;
    @FXML private Region progress5;
    @FXML private Label stepLabel;
    @FXML private HBox draftSavedLabel;
    @FXML private ScrollPane stepScroll;
    @FXML private Label errorLabel;

    @FXML private VBox step1Pane;
    @FXML private VBox step2Pane;
    @FXML private VBox step3Pane;
    @FXML private VBox step4Pane;
    @FXML private VBox step5Pane;

    @FXML private Label needsCountLabel;
    @FXML private Label call911Text;
    @FXML private GridPane needsGrid;

    @FXML private HBox urgencyOptions;
    @FXML private Label urgencyHint;
    @FXML private TextArea descriptionArea;
    @FXML private Label descriptionCount;
    @FXML private Label adultsCount;
    @FXML private Label childrenCount;
    @FXML private FlowPane specialNeedsOptions;

    @FXML private Label mapStatus;
    @FXML private TextField streetField;
    @FXML private TextField cityField;
    @FXML private TextField stateField;
    @FXML private TextField zipField;
    @FXML private CheckBox atLocationSwitch;

    @FXML private TextField phoneField;
    @FXML private HBox contactOptions;
    @FXML private TextField backupField;

    @FXML private FlowPane reviewNeeds;
    @FXML private Label reviewUrgency;
    @FXML private Label reviewPeople;
    @FXML private Label reviewSpecial;
    @FXML private Label reviewDescription;
    @FXML private Label reviewAddress;
    @FXML private Label reviewAtLocation;
    @FXML private Label reviewContact;
    @FXML private Label reviewContactExtra;
    @FXML private CheckBox confirmCheck;

    @FXML private Button backButton;
    @FXML private Button continueButton;
    @FXML private StackPane leaveOverlay;

    private final ReliefSystem facade = ReliefSystem.getInstance();
    private User user;
    private RequestDraft draft;

    @FXML
    private void initialize() {
        user = facade.getCurrentUser();
        boolean resuming = RequestDraft.find(user) != null;
        draft = RequestDraft.findOrCreate(user);
        setShown(draftSavedLabel, resuming);
        hideError();

        descriptionArea.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > MAX_DESCRIPTION) {
                descriptionArea.setText(newText.substring(0, MAX_DESCRIPTION));
                return;
            }
            descriptionCount.setText(newText.length() + "/" + MAX_DESCRIPTION);
        });

        for (TextField field : new TextField[] { streetField, cityField, stateField, zipField }) {
            field.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
                if (!isFocused) {
                    lookUpAddress();
                }
            });
        }


        loadDraftIntoScreen();
        showStep(draft.step);
    }

    private void showStep(int step) {
        draft.step = step;
        VBox[] panes = { step1Pane, step2Pane, step3Pane, step4Pane, step5Pane };
        for (int i = 0; i < panes.length; i++) {
            setShown(panes[i], i + 1 == step);
        }

        Region[] segments = { progress1, progress2, progress3, progress4, progress5 };
        for (int i = 0; i < segments.length; i++) {
            segments[i].getStyleClass().remove("progress-done");
            if (i < step) {
                segments[i].getStyleClass().add("progress-done");
            }
        }

        stepLabel.setText("Step " + step + " of " + RequestDraft.STEP_COUNT + " · " + STEP_NAMES[step - 1]);
        backButton.setText(step == 1 ? "Cancel" : "Back");
        continueButton.setText(step == RequestDraft.STEP_COUNT ? "Submit Request" : "Continue");
        continueButton.setDisable(step == RequestDraft.STEP_COUNT && !draft.confirmed);

        if (step == RequestDraft.STEP_COUNT) {
            fillReview();
        }
        hideError();
        stepScroll.setVvalue(0);
    }

    @FXML
    private void handleContinue() {
        collectStep(draft.step);
        String problem = checkStep(draft.step);
        if (problem != null) {
            showError(problem);
            return;
        }
        if (draft.step == RequestDraft.STEP_COUNT) {
            submit();
            return;
        }
        saveDraft();
        showStep(draft.step + 1);
    }

    @FXML
    private void handleBack() {
        collectStep(draft.step);
        if (draft.step == 1) {
            handleClose();
            return;
        }
        saveDraft();
        showStep(draft.step - 1);
    }

    @FXML
    private void editStep(MouseEvent event) {
        int step = Integer.parseInt((String) ((Node) event.getSource()).getUserData());
        showStep(step);
    }

    private void collectStep(int step) {
        switch (step) {
            case 2 -> draft.description = descriptionArea.getText().trim();
            case 3 -> {
                draft.street = streetField.getText().trim();
                draft.city = cityField.getText().trim();
                draft.state = stateField.getText().trim().toUpperCase();
                draft.zip = zipField.getText().trim();
                draft.atLocationNow = atLocationSwitch.isSelected();
            }
            case 4 -> {
                draft.phone = phoneField.getText().trim();
                draft.backupContact = backupField.getText().trim();
            }
            default -> {
            }
        }
    }

    private String checkStep(int step) {
        switch (step) {
            case 1:
                return draft.needs.isEmpty() ? "Pick at least one kind of help." : null;
            case 2:
                if (draft.urgency == null) {
                    return "Choose how urgent it is.";
                }
                return draft.adults + draft.children == 0 ? "Add at least one person who needs help." : null;
            case 3:
                if (draft.street.isEmpty() || draft.city.isEmpty()) {
                    return "Enter your street address and city.";
                }
                if (!draft.state.matches("[A-Z]{2}")) {
                    return "Use the two-letter state code, like SC.";
                }
                return draft.zip.matches("\\d{5}") ? null : "ZIP code must be 5 digits.";
            case 4:
                return phoneDigits(draft.phone).length() == 10 ? null : "Enter a 10-digit phone number.";
            case 5:
                return draft.confirmed ? null : "Please confirm your information is accurate.";
            default:
                return null;
        }
    }

    @FXML
    private void handleCall911() {
        call911Text.setText("Dial 911 on your phone now. Come back to finish this request when you're safe.");
    }

    @FXML
    private void toggleNeed(MouseEvent event) {
        Node tile = (Node) event.getSource();
        String need = (String) tile.getUserData();
        if (!draft.needs.remove(need)) {
            draft.needs.add(need);
        }
        showNeeds();
        hideError();
    }

    private void showNeeds() {
        for (Node tile : needsGrid.getChildren()) {
            setSelected(tile, draft.needs.contains((String) tile.getUserData()));
        }
        needsCountLabel.setText("Select all that apply · " + draft.needs.size() + " selected");
    }

    @FXML
    private void chooseUrgency(javafx.event.ActionEvent event) {
        draft.urgency = (String) ((Node) event.getSource()).getUserData();
        showUrgency();
        hideError();
    }

    private void showUrgency() {
        for (Node option : urgencyOptions.getChildren()) {
            setSelected(option, draft.urgency != null && draft.urgency.equals(option.getUserData()));
        }
        urgencyHint.setText(draft.urgency == null ? "Pick the option that fits best." : switch (draft.urgency) {
            case "LOW" -> "Low = can wait a few days";
            case "MEDIUM" -> "Medium = needed within 2-3 days";
            case "HIGH" -> "High = needed within 24 hours";
            default -> "Critical = needed right now. If a life is in danger, call 911.";
        });
    }

    @FXML
    private void adultsMinus() {
        draft.adults = Math.max(0, draft.adults - 1);
        showCounts();
    }

    @FXML
    private void adultsPlus() {
        draft.adults = Math.min(MAX_PEOPLE, draft.adults + 1);
        showCounts();
    }

    @FXML
    private void childrenMinus() {
        draft.children = Math.max(0, draft.children - 1);
        showCounts();
    }

    @FXML
    private void childrenPlus() {
        draft.children = Math.min(MAX_PEOPLE, draft.children + 1);
        showCounts();
    }

    private void showCounts() {
        adultsCount.setText(String.valueOf(draft.adults));
        childrenCount.setText(String.valueOf(draft.children));
        hideError();
    }

    @FXML
    private void toggleSpecialNeed(javafx.event.ActionEvent event) {
        String need = (String) ((Node) event.getSource()).getUserData();
        if (!draft.specialNeeds.remove(need)) {
            draft.specialNeeds.add(need);
        }
        showSpecialNeeds();
    }

    private void showSpecialNeeds() {
        for (Node chip : specialNeedsOptions.getChildren()) {
            setSelected(chip, draft.specialNeeds.contains((String) chip.getUserData()));
        }
    }

    @FXML
    private void useMyLocation() {
        Location home = user == null ? null : user.getLocation();
        if (home == null) {
            mapStatus.setText("No home address on your account");
            return;
        }
        streetField.setText(nullToEmpty(home.getAddressLine1()));
        cityField.setText(nullToEmpty(home.getCity()));
        stateField.setText(nullToEmpty(home.getState()));
        zipField.setText(nullToEmpty(home.getZipCode()));
        collectStep(3);

        if (home.getLatitude() != 0 || home.getLongitude() != 0) {
            saveCoordinates(home.getLatitude(), home.getLongitude(), "✓ Using your home address");
        } else {
            lookUpAddress();
        }
    }

    private void lookUpAddress() {
        collectStep(3);
        if (draft.street.isEmpty() || draft.city.isEmpty() || draft.state.isEmpty()) {
            return;
        }
        Address address = new Address(draft.street, draft.city, draft.state, draft.zip);
        draft.latitude = null;
        draft.longitude = null;
        mapStatus.setText("Looking up your address…");

        Thread lookup = new Thread(() -> {
            try {
                Coordinates found = MapTools.getInstance().getCoordinates(address);
                Platform.runLater(() -> saveCoordinates(found.getLatitude(), found.getLongitude(), "✓ Address found on the map"));
            } catch (IllegalStateException e) {
                Platform.runLater(() -> mapStatus.setText("Address lookup needs MAP_API_KEY. You can still continue."));
            } catch (Exception e) {
                Platform.runLater(() -> mapStatus.setText("Couldn't find that address. Check it, or continue anyway."));
            }
        });
        lookup.setDaemon(true);
        lookup.start();
    }

    private void saveCoordinates(double lat, double lng, String status) {
        draft.latitude = lat;
        draft.longitude = lng;
        mapStatus.setText(status);
    }

    @FXML
    private void chooseContact(javafx.event.ActionEvent event) {
        draft.contactPreference = (String) ((Node) event.getSource()).getUserData();
        showContactPreference();
    }

    private void showContactPreference() {
        for (Node option : contactOptions.getChildren()) {
            setSelected(option, draft.contactPreference.equals(option.getUserData()));
        }
    }

    private void fillReview() {
        reviewNeeds.getChildren().clear();
        for (String need : draft.needs) {
            Label chip = new Label(need);
            chip.getStyleClass().add("review-chip");
            reviewNeeds.getChildren().add(chip);
        }

        String urgency = draft.urgency == null ? "Not set" : titleCase(draft.urgency);
        reviewUrgency.setText(urgency);
        reviewUrgency.getStyleClass().removeAll("urgency-low", "urgency-medium", "urgency-high", "urgency-critical");
        if (draft.urgency != null) {
            reviewUrgency.getStyleClass().add("urgency-" + draft.urgency.toLowerCase());
        }

        reviewPeople.setText(plural(draft.adults, "adult") + ", " + plural(draft.children, "child", "children"));
        reviewSpecial.setText(draft.specialNeeds.isEmpty() ? "None" : String.join(", ", draft.specialNeeds));
        reviewDescription.setText(draft.description);
        setShown(reviewDescription, !draft.description.isEmpty());

        reviewAddress.setText(draft.street + ", " + draft.city + ", " + draft.state + " " + draft.zip);
        reviewAtLocation.setText(draft.atLocationNow ? "You're at this location now" : "You're not at this location right now");

        reviewContact.setText(formatPhone(draft.phone) + " · " + preferenceText());

        reviewContactExtra.setText("Backup: " + draft.backupContact);
        setShown(reviewContactExtra, !draft.backupContact.isEmpty());

        confirmCheck.setSelected(draft.confirmed);
    }

    @FXML
    private void confirmChanged() {
        draft.confirmed = confirmCheck.isSelected();
        continueButton.setDisable(!draft.confirmed);
        hideError();
    }

    private void submit() {
        if (user == null) {
            showError("Please log in before submitting a request.");
            return;
        }

        Location location = draft.latitude != null
                ? new Location(draft.street, "", draft.city, draft.state, draft.zip, draft.latitude, draft.longitude)
                : new Location(draft.street, draft.city, draft.state, draft.zip);

        ArrayList<Person> people = new ArrayList<>();
        for (int i = 1; i <= draft.adults; i++) {
            people.add(new Person("Adult", String.valueOf(i), 18));
        }
        for (int i = 1; i <= draft.children; i++) {
            people.add(new Person("Child", String.valueOf(i), 10));
        }
        ArrayList<Pet> pets = new ArrayList<>();
        if (draft.specialNeeds.contains("Pets")) {
            pets.add(new Pet("Pet", "pet"));
        }

        ReliefRequest request = facade.submitReliefRequest(location, String.join(", ", draft.needs),
                people, pets, formatPhone(draft.phone));
        request.setUrgency(switch (draft.urgency) {
            case "CRITICAL" -> UrgencyTier.LIFE_THREATENING;
            case "HIGH" -> UrgencyTier.URGENT;
            default -> UrgencyTier.STANDARD;
        });
        request.setDescription(fullDescription());

        RequestDraft.discard();
        RequestSubmittedController.lastSubmitted = request;
        App.setRoot("request-submitted");
    }

    private String fullDescription() {
        StringBuilder text = new StringBuilder(draft.description);
        if (!draft.specialNeeds.isEmpty()) {
            text.append("\nSpecial needs: ").append(String.join(", ", draft.specialNeeds));
        }
        if (!draft.atLocationNow) {
            text.append("\nNot at this location right now.");
        }
        text.append("\nContact: ").append(preferenceText());
        if (!draft.backupContact.isEmpty()) {
            text.append("\nBackup contact: ").append(draft.backupContact);
        }
        return text.toString().trim();
    }

    private String preferenceText() {
        return switch (draft.contactPreference) {
            case "CALL" -> "Prefers a call";
            case "TEXT" -> "Prefers text";
            default -> "Call or text";
        };
    }

    @FXML
    private void handleClose() {
        collectStep(draft.step);
        leaveOverlay.setVisible(true);
    }

    @FXML
    private void closeLeavePopup() {
        leaveOverlay.setVisible(false);
    }

    @FXML
    private void saveDraftAndExit() {
        collectStep(draft.step);
        saveDraft();
        App.setRoot(NavController.HOME);
    }

    @FXML
    private void discardRequest() {
        RequestDraft.discard();
        App.setRoot(NavController.HOME);
    }

    private void saveDraft() {
        draft.save(user);
        setShown(draftSavedLabel, true);
    }

    private void loadDraftIntoScreen() {
        showNeeds();
        showUrgency();
        descriptionArea.setText(draft.description);
        showCounts();
        showSpecialNeeds();

        streetField.setText(draft.street);
        cityField.setText(draft.city);
        stateField.setText(draft.state);
        zipField.setText(draft.zip);
        atLocationSwitch.setSelected(draft.atLocationNow);
        if (draft.latitude != null) {
            saveCoordinates(draft.latitude, draft.longitude, "✓ Address found on the map");
        }

        phoneField.setText(draft.phone);
        showContactPreference();
        backupField.setText(draft.backupContact);
    }

    private void setSelected(Node node, boolean selected) {
        node.getStyleClass().remove("selected");
        if (selected) {
            node.getStyleClass().add("selected");
        }
    }

    private void setShown(Node node, boolean shown) {
        node.setVisible(shown);
        node.setManaged(shown);
    }

    private void showError(String message) {
        errorLabel.setText(message);
        setShown(errorLabel, true);
        stepScroll.setVvalue(0);
    }

    private void hideError() {
        setShown(errorLabel, false);
    }

    private static String phoneDigits(String phone) {
        if (phone == null) {
            return "";
        }
        String digits = phone.replaceAll("\\D", "");

        return digits.length() == 11 && digits.startsWith("1") ? digits.substring(1) : digits;
    }

    private static String formatPhone(String phone) {
        String d = phoneDigits(phone);
        if (d.length() != 10) {
            return phone;
        }
        return "(" + d.substring(0, 3) + ") " + d.substring(3, 6) + "-" + d.substring(6);
    }

    private static String plural(int count, String word) {
        return plural(count, word, word + "s");
    }

    private static String plural(int count, String singular, String pluralWord) {
        return count + " " + (count == 1 ? singular : pluralWord);
    }

    private static String titleCase(String text) {
        String lower = text.replace('_', ' ').toLowerCase();
        return Character.toUpperCase(lower.charAt(0)) + lower.substring(1);
    }

    private static String nullToEmpty(String text) {
        return text == null ? "" : text;
    }
}
