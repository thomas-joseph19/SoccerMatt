package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.SessionManager.currentCoachSchedule;

public class RemovePracticeorGameController implements Initializable {

    @FXML
    private DatePicker datePicker;
    @FXML
    private Button buttonRemove;
    @FXML
    private Button buttonCancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        buttonCancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));
        buttonRemove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                LocalDate selectedDate = datePicker.getValue();

                if (selectedDate == null) {
                    DBUtils.showAlert("Error", "Please select a date", Alert.AlertType.ERROR);
                    return;
                }

                // Remove matching practices/games from currentCoachSchedule
                boolean removed = false;
                Iterator<Schedule> iterator = currentCoachSchedule.iterator();
                while (iterator.hasNext()) {
                    Schedule schedule = iterator.next();
                    if (schedule.getCoachID() == SessionManager.getCurrentCoachID() &&
                            schedule.getDate().equals(selectedDate.toString())) {
                        iterator.remove(); // Remove from ArrayList
                        DBUtils.removeScheduleFromDatabase(schedule); // Remove from database
                        removed = true;
                    }
                }

                if (removed) {
                    DBUtils.showAlert("Success", "Practice/Game on " + selectedDate + " has been removed.", Alert.AlertType.INFORMATION);
                    DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null);
                } else {
                    DBUtils.showAlert("Error", "No Practice/Game found on " + selectedDate + ".", Alert.AlertType.ERROR);
                }
            }
        });

    }
}
