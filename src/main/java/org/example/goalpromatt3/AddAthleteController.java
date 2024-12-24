package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.w3c.dom.Text;

import javax.swing.*;
import java.net.URL;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.DBUtils.editOutfielder;
import static org.example.goalpromatt3.DBUtils.getCoachID;

//Initilizable to interact with widgets
public class AddAthleteController implements Initializable {

    //Widgets
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
    private TextField tf_shooting;
    @FXML
    private TextField tf_passing;
    @FXML
    private TextField tf_dribbling;
    @FXML
    private TextField tf_phonenumber;

    @FXML
    private Button button_cancel;
    @FXML
    private Button button_addathlete;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_addathlete.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {


                TextField[] textFields = {tf_shooting, tf_passing, tf_dribbling, tf_athleteage, tf_phonenumber};
                int[] parsedValues = new int[textFields.length];

                // Parse integer fields
                for (int i = 0; i < textFields.length; i++) {
                    try {
                        parsedValues[i] = Integer.parseInt(textFields[i].getText());
                    } catch (NumberFormatException e) {
                        parsedValues[i] = 0; // Default value if input is invalid or empty
                    }
                }


              //Later, when adding an athlete, pass `coachID` as the argument
                if(DBUtils.isValidEmail(tf_gaurdianemail.getText())) {
                    if (DBUtils.isWithinSkills(parsedValues[0], parsedValues[1], parsedValues[2])) {
                            DBUtils.addAthletebutton(actionEvent, tf_athletefirstname.getText(), tf_athletelastname.getText(), tf_gaurdianfirstname.getText(), tf_gaurdianlastname.getText(), tf_gaurdianemail.getText(), parsedValues[4], parsedValues[0], parsedValues[1], parsedValues[2], parsedValues[3]);
                            DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null);
                        } else {
                            DBUtils.showAlert("Error", "Please ensure skills are within the bounds", Alert.AlertType.ERROR);
                        }
                    } else {
                        DBUtils.showAlert("Invalid Email", "Please enter a valid email", Alert.AlertType.ERROR);
                    }
                }


        });

        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));

    }
}
