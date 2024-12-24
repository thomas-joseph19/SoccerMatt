package org.example.goalpromatt3;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class athletefinderController implements Initializable {
    @FXML
    private Button button_find;
    @FXML
    private TextField tf_firstname;
    @FXML
    private TextField tf_lastname;
    @FXML
    private Button button_back;

    public static String thefirstname;
    public static String thelastname;
    public static Integer theathleteID;
    public static boolean isGoalie;


    @Override


    public void initialize(URL url, ResourceBundle resourceBundle) {


        button_find.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int CoachID = SessionManager.getCurrentCoachID();  // Using the session manager to fetch the active CoachID
                DBUtils.findAthlete(actionEvent, CoachID, tf_firstname.getText(), tf_lastname.getText());
                thefirstname = tf_firstname.getText();
                thelastname = tf_lastname.getText();
                theathleteID = DBUtils.getAthleteID(thefirstname, thelastname);
                isGoalie = DBUtils.isGoalie(thefirstname, thelastname);
            }
        });

        button_back.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
    }
}
