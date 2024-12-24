package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.SessionManager.*;

public class addExerciseController implements Initializable {
    @FXML
    private Button button_cancel;
    @FXML
    private Button button_add;

    @FXML
    private TextField tf_exercisename;
    @FXML
    private TextField tf_link;
    @FXML
    private TextField tf_difficulty;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
        button_add.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                int difficulty;
                try {
                    difficulty = Integer.parseInt(tf_difficulty.getText());
                } catch (NumberFormatException e) {
                    difficulty = 0; // Default value if input is invalid or empty
                }
                DBUtils.addExercise(currentCoachID, tf_exercisename.getText(), tf_link.getText(), difficulty);
                DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null);
            }
        });
    }
}
