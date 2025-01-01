package org.example.goalpromatt3;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert.AlertType;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class sendExercisesController {

    @FXML
    private ComboBox<String> exerciseComboBox;

    @FXML
    private VBox athleteVBox;

    @FXML
    private Button sendButton, button_cancel;

    @FXML
    private TextArea messageTextArea; // For personal message input

    @FXML
    private void initialize() {
        // Cancel button to return to exercises.fxml
        button_cancel.setOnAction(event -> DBUtils.changeScene(event, "exercises.fxml", "Exercises", null));

        int coachID = SessionManager.getCurrentCoachID();

        // Populate exercise ComboBox using SessionManager's fetchCoachExercises
        populateExerciseComboBox(coachID);

        // Populate athlete VBox with checkboxes for multi-selection using SessionManager's loadAthletesForCoach
        populateAthleteVBox(coachID);
    }

    private void populateExerciseComboBox(int coachID) {
        ArrayList<Exercise> exercises = SessionManager.fetchCoachExercises(coachID);
        exerciseComboBox.getItems().clear();
        for (Exercise exercise : exercises) {
            exerciseComboBox.getItems().add(exercise.getName()); // Assuming Exercise class has a getName method
        }
    }

    private void populateAthleteVBox(int coachID) {
        ArrayList<Athlete> athletes = SessionManager.loadAthletesForCoach(coachID);
        athleteVBox.getChildren().clear();

        for (Athlete athlete : athletes) {
            // Create a CheckBox for each athlete
            CheckBox athleteCheckBox = new CheckBox(athlete.getFirstName() + " " + athlete.getLastName());
            athleteCheckBox.setUserData(athlete.getAthleteID()); // Assign unique athleteID as user data
            athleteVBox.getChildren().add(athleteCheckBox);

            // Debugging: Ensure the user data is correctly set
            System.out.println("Added CheckBox for athlete ID: " + athlete.getAthleteID());
        }
    }

    @FXML
    private void sendExercise(ActionEvent event) throws SQLException {
        String selectedExercise = exerciseComboBox.getValue();
        String personalMessage = messageTextArea.getText();
        int coachID = SessionManager.getCurrentCoachID();

        if (selectedExercise == null || selectedExercise.trim().isEmpty()) {
            DBUtils.showAlert("No Exercise Selected", "Please select an exercise to send.", AlertType.WARNING);
            return;
        }

        List<Integer> selectedAthletes = new ArrayList<>();

        // Iterate through the VBox to find selected athletes
        for (Node node : athleteVBox.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                // Retrieve the athlete ID from user data
                Integer athleteID = (Integer) checkBox.getUserData();
                selectedAthletes.add(athleteID);

                // Debugging: Print each selected athlete ID
                System.out.println("Selected athlete ID: " + athleteID);
            }
        }

        if (selectedAthletes.isEmpty()) {
            DBUtils.showAlert( "No Athletes Selected", "Please select at least one athlete to send the exercise.", AlertType.WARNING);
            return;
        }

        // Send emails to selected athletes
        for (int athleteID : selectedAthletes) {
            DBUtils.sendExerciseEmail(coachID, athleteID, selectedExercise, personalMessage);
        }

        DBUtils.showAlert("Success", "Emails sent successfully to selected athletes.", AlertType.INFORMATION);
        DBUtils.changeScene(event, "Logged-In.fxml", "Hello", null);
    }


}
