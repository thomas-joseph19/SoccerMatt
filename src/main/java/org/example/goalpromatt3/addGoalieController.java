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

public class addGoalieController implements Initializable {
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
    private TextField tf_reflexes;
    @FXML
    private TextField tf_catching;
    @FXML
    private TextField tf_punting;
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
        int reflexesscore;
        try {
           reflexesscore = Integer.parseInt(tf_reflexes.getText());
        } catch (NumberFormatException e) {
            reflexesscore = 0; // Default value if input is invalid or empty
        }

        int phone;
        try {
            phone = Integer.parseInt(tf_phonenumber.getText());
        } catch (NumberFormatException e) {
            phone = 0; // Default value if input is invalid or empty
        }

        int catchingscore;
        try {
            catchingscore = Integer.parseInt(tf_catching.getText());
        } catch (NumberFormatException e) {
            catchingscore = 0; // Default value if input is invalid or empty
        }

        int puntingscore;
        try {
            puntingscore = Integer.parseInt(tf_punting.getText());
        } catch (NumberFormatException e) {
            puntingscore = 0; // Default value if input is invalid or empty
        }

        int age;
        try {
            age = Integer.parseInt(tf_athleteage.getText());
        } catch (NumberFormatException e) {
            age = 0; // Default value if input is invalid or empty
        }

        if(DBUtils.isValidEmail(tf_gaurdianemail.getText())) {
            if(DBUtils.isWithinSkills(reflexesscore, puntingscore, catchingscore)) {
                DBUtils.addGoaliebutton(actionEvent, tf_athletefirstname.getText(), tf_athletelastname.getText(), tf_gaurdianfirstname.getText(), tf_gaurdianlastname.getText(), tf_gaurdianemail.getText(), phone, reflexesscore, catchingscore, puntingscore, age);
                DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null);
            } else {
                DBUtils.showAlert("Error", "Please ensure skills are within the bounds", Alert.AlertType.ERROR);
            }
        } else {
            DBUtils.showAlert("Invalid Email", "Please enter a valid email", Alert.AlertType.ERROR);

        }

    }
});

        button_cancel.setOnAction(actionEvent ->  DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
    }
}
