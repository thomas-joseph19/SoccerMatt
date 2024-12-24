package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class isGoalieController implements Initializable {
    @FXML
    private Button button_continue;

    @FXML
    private RadioButton radio_yes;
    @FXML
    private RadioButton radio_no;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_continue.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if ((!radio_no.isSelected() && !radio_yes.isSelected()) || (radio_yes.isSelected() && radio_no.isSelected())) {
                    DBUtils.showAlert("Error", "Please choose one of the following choices", Alert.AlertType.ERROR);
                }

              else if (radio_no.isSelected()) {
                    DBUtils.changeScene(actionEvent, "addAthlete.fxml", "Add Athlete", null);

                }


                else if (radio_yes.isSelected()) {
                    DBUtils.changeScene(actionEvent, "addGoalie.fxml", "Add Athlete", null);
                }
            }
        });

    }
}
