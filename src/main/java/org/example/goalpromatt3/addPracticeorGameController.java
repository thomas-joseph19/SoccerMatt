package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ComboBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class addPracticeorGameController implements Initializable {

    @FXML
    private DatePicker dp_date; // DatePicker to select the date

    @FXML
    private RadioButton rb_practice; // RadioButton for "Practice"

    @FXML
    private RadioButton rb_game; // RadioButton for "Game"

    @FXML
    private ComboBox<String> startTimeComboBox; // ComboBox for selecting start time

    @FXML
    private ComboBox<String> endTimeComboBox; // ComboBox for selecting end time

    @FXML
    private Button button_add; // Button to add the entry

    @FXML
    private Button button_cancel; // Button to cancel and close the dialog

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize the time ComboBoxes with available times
        for (int i = 8; i < 20; i++) {
            String startHour = (i % 12 == 0 ? 12 : i % 12) + ":00";
            String endHour = (i % 12 == 0 ? 12 : i % 12) + ":30";
            startTimeComboBox.getItems().add(startHour);
            endTimeComboBox.getItems().add(endHour);

            int coachID = SessionManager.getCurrentCoachID();
            ArrayList<Schedule> currentSchedule = SessionManager.loadSchedule(coachID);
        }

        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));

        button_add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (dp_date.getValue() == null || (!rb_practice.isSelected() && !rb_game.isSelected()) ||
                        startTimeComboBox.getValue() == null || endTimeComboBox.getValue() == null) {
                    DBUtils.showAlert("Input Error", "Please select a date, choose either 'Practice' or 'Game', and pick start and end times.", Alert.AlertType.ERROR);
                    return;
                }

                // Retrieve values from the UI
                String selectedDate = dp_date.getValue().toString(); // Convert the selected date to a string
                String startTime = startTimeComboBox.getValue();
                String endTime = endTimeComboBox.getValue();
                boolean isPractice = rb_practice.isSelected();
                boolean isGame = rb_game.isSelected();

                // Get the current coach ID
                int coachID = SessionManager.getCurrentCoachID();

                // Call the DBUtils method to add the schedule
                DBUtils.addSchedule(coachID, selectedDate, isPractice, isGame, startTime, endTime);
                DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null);
            }
        });
    }
}
