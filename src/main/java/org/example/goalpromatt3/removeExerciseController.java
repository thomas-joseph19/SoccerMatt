package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class removeExerciseController implements Initializable {
    @FXML
    private TextField tf_exerciseName;  // TextField to input the exercise name

    @FXML
    private Button button_remove, button_cancel;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                String exerciseName = tf_exerciseName.getText();  // Get the text from the input field

                if (exerciseName == null || exerciseName.trim().isEmpty()) {
                    // If the input is empty, show an error
                    DBUtils.showAlert("Error", "Please enter a valid exercise name", Alert.AlertType.ERROR);
                } else {
                    // Call the DBUtils method to remove the exercise from the database
                    DBUtils.removeExerciseByName(exerciseName);

                    // Show a success message once the exercise is removed
                    DBUtils.showAlert("Success", "Exercise removed successfully", Alert.AlertType.INFORMATION);
                    // Optionally clear the text field after successful removal
                    DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null);
                }
            }
        });
        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
    }
}
