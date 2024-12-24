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

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import static org.example.goalpromatt3.SessionManager.currentCoachID;
import static org.example.goalpromatt3.SessionManager.fetchCoachExercises;

public class ExercisesController implements Initializable {

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
    private Button button_addExercise, button_sort, button_remove;

    @FXML
    private TableView<Exercise> exercisesTable;
    @FXML
    private TableColumn<Exercise, String> nameColumn;
    @FXML
    private TableColumn<Exercise, String> linkColumn;
    @FXML
    private TableColumn<Exercise, Integer> difficultyColumn;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Gives action to all buttons
        button_logout.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "hello-view.fxml", "Log-In", null));
        button_athletes.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "athletes.fxml", "Athletes", null));
        button_exercises.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "exercises.fxml", "Exercises", null));
        button_home.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "Logged-In.fxml", "Welcome", null));
        button_schedules.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "schedule.fxml", "Schedule", null));

        button_addExercise.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "addExercise.fxml", "Add Exercise", null));
        button_remove.setOnAction(actionEvent -> DBUtils.changeScene(actionEvent, "removeexercise.fxml", "Remove Exercise", null));
        // Fetch the list of exercises
        ArrayList<Exercise> exercises = fetchCoachExercises(currentCoachID);
        // Convert ArrayList to ObservableList
        ObservableList<Exercise> exerciseList = FXCollections.observableArrayList(exercises);
        button_sort.setOnAction(actionEvent -> DBUtils.getSortedExercisesBySkillLevel(exerciseList));


        // Set up columns to show name, link, and difficulty
        nameColumn.setCellValueFactory(cellData -> new javafx.beans.value.ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cellData.getValue().getName();  // Get name from Exercise object
            }
        });

        linkColumn.setCellValueFactory(cellData -> new javafx.beans.value.ObservableValueBase<String>() {
            @Override
            public String getValue() {
                return cellData.getValue().getLink();  // Get link from Exercise object
            }
        });

        difficultyColumn.setCellValueFactory(cellData -> new javafx.beans.value.ObservableValueBase<Integer>() {
            @Override
            public Integer getValue() {
                return cellData.getValue().getDifficulty();  // Get difficulty from Exercise object
            }
        });

        // Set the table items (this will update the table with the list of exercises)
        exercisesTable.setItems(exerciseList);
    }

}
