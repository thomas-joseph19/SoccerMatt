package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;
//Initlizise class allows you to interact with widgets from FXML
public class LoggedInController implements Initializable {
    //Links the widgets from the logged-in screen to this controller class
    @FXML
    private Button button_logout;
    @FXML
    private Button button_athletes;
    @FXML
    private Button button_schedules;
    @FXML
    private Button button_home;
    @FXML
    private Button button_exercises;
    @FXML
    private Button button_send, button_edit;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Gives action to logout button on homescreen to change scenes
        button_logout.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "hello-view.fxml", "Log-In", null));
        button_athletes.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
        button_exercises.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
        button_home.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "Logged-In.fxml", "Welcome", null));
        button_schedules.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));

            button_send.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "sendExercises.fxml", "Send Exercises", null));
            button_edit.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "editprofile.fxml", "Edit Profile", null));
    }

}
