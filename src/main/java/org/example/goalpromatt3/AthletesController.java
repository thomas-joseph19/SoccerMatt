package org.example.goalpromatt3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.SessionManager.*;

//Initializable allows for interactability with the widgets
public class AthletesController implements Initializable {
    @FXML
    private TableView<Athlete> athletesTable;

    @FXML
    private TableColumn<Athlete, String> firstNameColumn;

    @FXML
    private TableColumn<Athlete, String> lastNameColumn;

    @FXML
    private TableColumn<Athlete, Integer> skillColumn;

    @FXML
    private TableColumn<Athlete, String> parentFirstNameColumn;

    @FXML
    private TableColumn<Athlete, String> parentLastNameColumn;

    @FXML
    private TableColumn<Athlete, String> emailColumn;

    @FXML
    private TableColumn<Athlete, String> phoneNumberColumn;

    @FXML
    private Button button_addathlete, button_editathlete, button_logout, button_home, button_exercises, button_schedules, button_athletes, button_skillsort, button_namesort, button_remove;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {




        //Gives action to all buttons
        button_logout.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "hello-view.fxml", "Log-In", null));
        button_athletes.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
        button_exercises.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
        button_home.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "Logged-In.fxml", "Welcome", null));
        button_schedules.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));
        button_remove.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "removeathlete.fxml", "Remove Athlete", null));
        button_addathlete.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "isGoalie.fxml", "Goalie", null));

        button_editathlete.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletefinder.fxml", "Find Athlete", null));
        ObservableList<Athlete> athleteslist = FXCollections.observableArrayList(SessionManager.loadAthletesForCoach(currentCoachID));

        button_skillsort.setOnAction(actionEvent -> DBUtils.selectionSortBySkill(athleteslist)); // sorts by skill
        button_namesort.setOnAction(actionEvent -> DBUtils.bubbleSortByLastName(athleteslist)); // sorts by lastname

        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        skillColumn.setCellValueFactory(new PropertyValueFactory<>("skill"));
        parentFirstNameColumn.setCellValueFactory(new PropertyValueFactory<>("parentFirstName"));
        parentLastNameColumn.setCellValueFactory(new PropertyValueFactory<>("parentLastName"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));

        // Set columns to be sortable (this is the default behavior, but explicitly enabling for clarity)
        skillColumn.setSortable(true);
        firstNameColumn.setSortable(true);
        lastNameColumn.setSortable(true);
        parentFirstNameColumn.setSortable(true);
        parentLastNameColumn.setSortable(true);
        emailColumn.setSortable(true);
        phoneNumberColumn.setSortable(true);

        // Load athlete data for the logged-in coach
        // Sort athletes by skill using selection sort


        // Set the sorted list into the table
        athletesTable.setItems(athleteslist);

    }


}
