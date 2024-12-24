package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import static org.example.goalpromatt3.SessionManager.currentCoachSchedule;

public class ScheduleController {

    @FXML
    private Button button_logout, button_athletes, button_schedules, button_home, button_exercises;

    @FXML
    private Button previousMonthButton, nextMonthButton;

    @FXML
    private Label monthLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button addPracticeButton, removePracticeButton;

    private YearMonth currentMonth;


    @FXML
    private void initialize() {

        //Gives action to all buttons
        button_logout.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "hello-view.fxml", "Log-In", null));
        button_athletes.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
        button_exercises.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
        button_home.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "Logged-In.fxml", "Welcome", null));
        button_schedules.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));

        addPracticeButton.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "addPracticeorGame.fxml", "Add Practice or Game", null));
        removePracticeButton.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "removePracticeorGame.fxml", "Remove Practice or Game", null));

        // Initialize the current month to today's date
        currentMonth = YearMonth.now();
        updateCalendar();

        // Button actions for navigating months
        previousMonthButton.setOnAction(e -> changeMonth(-1));
        nextMonthButton.setOnAction(e -> changeMonth(1));
    }

    private void changeMonth(int delta) {
        currentMonth = currentMonth.plusMonths(delta);
        updateCalendar();
    }

    private void updateCalendar() {
        // Clear existing nodes
        calendarGrid.getChildren().clear();

        // Set the current month label
        monthLabel.setText(currentMonth.getMonth().toString() + " " + currentMonth.getYear());

        // Add day names to the first row
        String[] daysOfWeek = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            GridPane.setConstraints(dayLabel, i, 0); // (col, row)
            calendarGrid.getChildren().add(dayLabel);
        }

        // Get the first day of the month and total days
        LocalDate firstDayOfMonth = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int startDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue() % 7; // Adjust Sunday as 0

        // Populate calendar with days
        int row = 1;
        int col = startDayOfWeek;
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentMonth.atDay(day);

            // Create a VBox for each day
            VBox dayBox = new VBox();
            dayBox.getStyleClass().add("day-box");

            // Add the day label
            Label dayLabel = new Label(String.valueOf(day));
            dayBox.getChildren().add(dayLabel);

            // Check for scheduled practices or games on this date
            List<String> events = getScheduleDetailsForDate(date);
            for (String event : events) {
                Label eventLabel = new Label(event);
                eventLabel.getStyleClass().add("event-label");
                dayBox.getChildren().add(eventLabel);
            }

            GridPane.setConstraints(dayBox, col, row);
            calendarGrid.getChildren().add(dayBox);

            // Move to the next column or row
            col++;
            if (col == 7) {
                col = 0;
                row++;
            }
        }
    }

    private List<String> getScheduleDetailsForDate(LocalDate date) {
        List<String> details = new ArrayList<>();

        // Filter schedules for the logged-in coach and the given date
        for (Schedule schedule : SessionManager.loadSchedule(SessionManager.getCurrentCoachID())) {
            if (schedule.getCoachID() == SessionManager.getCurrentCoachID() &&
                    schedule.getDate().equals(date.toString())) {

                // Add details for practice or game
                if (schedule.isPractice()) {
                    details.add("Practice:\n " + schedule.getStartTime() + " - " + schedule.getEndTime());
                }
                if (schedule.isGame()) {
                    details.add("Game:\n " + schedule.getStartTime() + " - " + schedule.getEndTime());
                }
            }
        }

        return details;
    }

}
