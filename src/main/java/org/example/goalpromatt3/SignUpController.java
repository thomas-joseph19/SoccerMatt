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
//initializable once again allows for interaction with on-screen widgets
public class SignUpController implements Initializable {
    //All the widgets from the class
    @FXML
    private TextField tf_firstname;
    @FXML
    private TextField tf_lastname;
    @FXML
    private TextField tf_createusername;
    @FXML
    private TextField tf_createpassword;
    @FXML
    private TextField tf_email;


    @FXML
    private Button button_signup;
    @FXML
    private Button button_gotologin;



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //sets action for go to login button
        button_gotologin.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "hello-view.fxml", "Login", null));

        button_signup.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {

                //Checks if input is empty
                if (!tf_createusername.getText().trim().isEmpty() && !tf_createpassword.getText().trim().isEmpty() && !tf_email.getText().trim().isEmpty() && !tf_firstname.getText().trim().isEmpty() && !tf_lastname.getText().trim().isEmpty()) {
                    if(DBUtils.isValidEmail(tf_email.getText())) {
                        DBUtils.signUpUser(actionEvent, tf_createusername.getText(), tf_createpassword.getText(), tf_email.getText(), tf_firstname.getText(), tf_lastname.getText());
                    } else {
                        DBUtils.showAlert("Invalid Email", "Please enter a valid email", Alert.AlertType.ERROR);

                    }
                } else {
                    DBUtils.showAlert("Error", "Please fill in all the information", Alert.AlertType.ERROR);
                }
            }
        });
    }
}
