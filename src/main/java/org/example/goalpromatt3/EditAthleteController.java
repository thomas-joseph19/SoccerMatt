package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.athletefinderController.*;

public class EditAthleteController implements Initializable {
    @FXML
    private TextField tf_athletefirstname;
    @FXML
    private TextField tf_athletelastname;
    @FXML
    private TextField tf_athleteage;
    @FXML
    private TextField tf_gaurdianfirstname;
    @FXML
    private TextField tf_gaurdianlastname;
    @FXML
    private TextField tf_gaurdianemail;
    @FXML
    private TextField tf_skill1;
    @FXML
    private TextField tf_skill2;
    @FXML
    private TextField tf_skill3;
    @FXML
    private TextField tf_phonenumber;

    @FXML
    private Button button_cancel;
    @FXML
    private Button button_editathlete;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


            //cancel button
        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));

        button_editathlete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                TextField[] textfields = {tf_skill1, tf_skill2, tf_skill3, tf_phonenumber, tf_athleteage};
                Integer[] parsed = new Integer[5];
                // Parse the values using a loop
                for (int i = 0; i < textfields.length; i++) {
                    try {
                        parsed[i] = Integer.parseInt(textfields[i].getText());
                    } catch (NumberFormatException e) {
                        parsed[i] = null; // Default value if input is invalid or empty
                    }
                }

                Integer athleteID = DBUtils.getAthleteID(thefirstname, thelastname);
                if (DBUtils.isWithinSkills(parsed[0], parsed[1], parsed[2])) {
                    DBUtils.editPlayer(actionEvent, athleteID, tf_athletefirstname.getText(), tf_athletelastname.getText(), tf_gaurdianfirstname.getText(), tf_gaurdianlastname.getText(), tf_gaurdianemail.getText(), parsed[3], parsed[0], parsed[1], parsed[2], parsed[4]);
                    DBUtils.showAlert("Athlete edited succesfully!", "Athlete edited succesfully!", Alert.AlertType.CONFIRMATION);
                    DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null);
                } else {
                    DBUtils.showAlert("Error", "Please ensure skills are within the bounds", Alert.AlertType.ERROR);
                }
            }
        });
    }
    }
