package org.example.goalpromatt3;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ResourceBundle;
//initiliizable allows us to interact with the FXML widgets
public class HelloController implements Initializable {
    //widgets
    @FXML
    private TextField tf_username;
    @FXML
    private TextField tf_password;

    @FXML
    private Button button_login;
    @FXML
    private Button button_gotosignup;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //gives login button an action when clicked
        button_login.setOnAction(actionEvent -> DBUtils.logInUser(actionEvent, tf_username.getText(), tf_password.getText()));
        button_gotosignup.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "Sign-Up.fxml", "Sign Up!", null));





            }
        }


