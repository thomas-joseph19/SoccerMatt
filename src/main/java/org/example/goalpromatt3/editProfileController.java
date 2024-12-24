package org.example.goalpromatt3;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.ResourceBundle;

public class editProfileController implements Initializable {

    @FXML
    private Button button_edit;

    @FXML
    private Button button_cancel;

    @FXML
    private TextField tf_firstname;

    @FXML
    private TextField tf_lastname;

    @FXML
    private TextField tf_email;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button_cancel.setOnAction(actionEvent ->
                DBUtils.changeScene(actionEvent, "Logged-In.fxml", "Logged In", null));

        button_edit.setOnAction(this::handleEditProfile);
    }

    private void handleEditProfile(ActionEvent event) {
        int coachID = SessionManager.getCurrentCoachID(); // Retrieve the current coach ID

        // Retrieve input values from text fields
        String newFirstName = tf_firstname.getText().trim().isEmpty() ? null : tf_firstname.getText().trim();
        String newLastName = tf_lastname.getText().trim().isEmpty() ? null : tf_lastname.getText().trim();
        String newEmail = tf_email.getText().trim().isEmpty() ? null : tf_email.getText().trim();

        // Call the DBUtils method to update the profile
        boolean success = DBUtils.editProfile(coachID, newFirstName, newLastName, newEmail);

        if (success) {
            // Show a success message and return to the dashboard
            if(DBUtils.isValidEmail(tf_email.getText())) {
                DBUtils.showAlert("Success", "Your profile has been updated", Alert.AlertType.INFORMATION);
                DBUtils.changeScene(event, "Logged-In.fxml", "Logged In", null);
            } else {
                DBUtils.showAlert("Invalid Email", "Please enter a valid email", Alert.AlertType.ERROR);

            }
        } else {
            // Show an error message
            DBUtils.showAlert("Error", "Update Failed", Alert.AlertType.ERROR);
        }
    }
}
