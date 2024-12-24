package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class removeAthleteController implements Initializable {
    @FXML
    private TextField tf_firstname;

    @FXML
    private TextField tf_lastname;

    @FXML
    private Button button_remove;

    @FXML
    private Button button_cancel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        button_remove.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                // Get the input values from the text fields
                String firstName = tf_firstname.getText();
                String lastName = tf_lastname.getText();

                // Check if both fields are filled
                if (firstName.isEmpty() || lastName.isEmpty()) {
                    // Show an error if any of the fields are empty
                    DBUtils.showAlert("Input Error", "Please fill in both First Name and Last Name", Alert.AlertType.ERROR);
                } else {
                    // Call the method to remove the athlete from the database
                    DBUtils.removeAthleteByName(firstName, lastName);
                    // Optionally, show a confirmation message if removal is successful
                    DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null);
                }
            }
        });

        button_cancel.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));


    }

}
