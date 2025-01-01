package org.example.goalpromatt3;


import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


import java.io.IOException;
import java.sql.*;
import java.util.Properties;

import static org.example.goalpromatt3.Athlete.findAthleteInList;
import static org.example.goalpromatt3.SessionManager.*;
import static org.example.goalpromatt3.athletefinderController.thefirstname;
import static org.example.goalpromatt3.athletefinderController.thelastname;


//THIS IS THE CLASS IN CHARGE OF ALL THE ACTIONS TAKING . Does all communication with database
public class DBUtils {

    private static String currentUsername;
    private static String currentPassword;


    public DBUtils(String currentUsername, String currentPassword) {
        this.currentUsername = currentUsername;
        this.currentPassword = currentPassword;
    }

    //This method is in charge of all the scene changes in the program. It takes multiple parameters in
    public static void changeScene(ActionEvent event, String fxmlfile, String title, String username) {
        //Making a root object of type parent
        Parent root = null;

        if (username != null) {
            //fxml loader loads the scene
            try {
                //Switches from Logged in page to login page
                FXMLLoader loader = new FXMLLoader(DBUtils.class.getResource(fxmlfile));
                //Turning FXML file into objects that can be displayed in the scene
                root = loader.load();
                LoggedInController loggedInController = loader.getController();

            } catch (IOException e) {
                e.printStackTrace();
            }
            //Switches from login page to sign up page
        } else {
            try {
                root = FXMLLoader.load(DBUtils.class.getResource(fxmlfile));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //double cast
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(new Scene(root, 600, 600));
        stage.show();

    }



    //LOG IN METHODS/EDIT USER
    //This method will sign-up the user and input their information into the SQL database
    public static void signUpUser(ActionEvent event, String username, String password, String email, String firstname, String lastname) {
        // Initialize variables for database connection and query handling
        Connection connection = null;
        PreparedStatement psInsert = null;
        PreparedStatement psCheckUserExists = null;
        ResultSet resultSet = null;

        try {
            connection = DBconnection();

            // Check if the username already exists
            psCheckUserExists = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
            psCheckUserExists.setString(1, username);
            resultSet = psCheckUserExists.executeQuery();

            if (resultSet.isBeforeFirst()) {
                // If the username exists, show an error message
                System.out.println("User already exists");
                showAlert("Error", "This username has been taken", Alert.AlertType.ERROR);
            } else {
                // Insert new user details into the database
                psInsert = connection.prepareStatement(
                        "INSERT INTO users (firstname, lastname, username, password, email) VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS // Enable retrieval of the new CoachID
                );
                psInsert.setString(1, firstname);
                psInsert.setString(2, lastname);
                psInsert.setString(3, username);
                psInsert.setString(4, password);
                psInsert.setString(5, email);
                psInsert.executeUpdate();

                // Retrieve the newly generated CoachID for session tracking
                ResultSet generatedKeys = psInsert.getGeneratedKeys();
                int coachID = -1;
                if (generatedKeys.next()) {
                    coachID = generatedKeys.getInt(1);
                }

                // Start a session for the new user
                SessionManager.startSession(username, password, coachID);

                // Change scene to the logged-in page
                changeScene(event, "Logged-In.fxml", "Welcome!", username);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //closes database
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (psCheckUserExists != null) {
                try {
                    psCheckUserExists.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }
    //Log in the user from the log in page
    public static void logInUser(ActionEvent event, String username, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Establish a connection to the database
            connection = DBconnection();  // Use the returned connection

            // Prepare SQL statement to retrieve the password and CoachID associated with the entered username
            preparedStatement = connection.prepareStatement("SELECT password, user_id FROM users WHERE username = ?");
            preparedStatement.setString(1, username);  // Set the username in the query
            resultSet = preparedStatement.executeQuery();  // Execute the query and get the result set

            // Check if any records were returned
            if (!resultSet.isBeforeFirst()) {  // No records indicate invalid username
                showAlert("Error", "Provided Credentials are incorrect", Alert.AlertType.ERROR);
            } else {
                // If the username exists, loop through the result set
                while (resultSet.next()) {
                    // Retrieve the stored password and CoachID for this username
                    String retrievedPassword = resultSet.getString("password");
                    int coachID = resultSet.getInt("user_id");

                    // Check if the provided password matches the retrieved password
                    if (retrievedPassword.equals(password)) {
                        // If passwords match, start a new session
                        SessionManager.startSession(username, password, coachID);

                        // Redirect the user to the "Logged-In.fxml" page with a welcome message
                        changeScene(event, "Logged-In.fxml", "Welcome!", username);
                    } else {
                        // If passwords don't match, display an error alert
                        showAlert("Error", "Password is incorrect", Alert.AlertType.ERROR);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //Edits user
    public static boolean editProfile(int coachID, String newFirstName, String newLastName, String newEmail) {
        String query = "UPDATE users SET firstname = COALESCE(?, firstname), " +
                "lastname = COALESCE(?, lastname), email = COALESCE(?, email) " +
                "WHERE user_id = ?";
        Connection connection = null;
        connection = DBconnection();

        try (PreparedStatement ps = connection.prepareStatement(query)) {

            // Set parameters, replacing null values with NULL in SQL
            ps.setString(1, (newFirstName != null && !newFirstName.isEmpty()) ? newFirstName : null);
            ps.setString(2, (newLastName != null && !newLastName.isEmpty()) ? newLastName : null);
            ps.setString(3, (newEmail != null && !newEmail.isEmpty()) ? newEmail : null);
            ps.setInt(4, coachID);

            // Execute update
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0; // Return true if at least one row was updated
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // Return false in case of error
        }
    }


    //ADD/EDIT AN ATHLETE METHODS
    //opens the window to add an athlete to the athlete table
    public static void addGoaliebutton(ActionEvent event, String firstname, String lastname, String parentfirtname, String parentlastname, String email, int phonenumber, int catching, int punting, int reflexes, int age) {
        // Initialize database connection and prepared statements
        Connection connection = null;
        String sql = "INSERT INTO goalie (Coach_ID, reflexes, catching, punting, overall, firstname, lastname, age, parentfirstname, parentlastname, parentemail, parentphone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ResultSet resultSet = null;
        PreparedStatement psInsert = null;

        //this gets the overall score (for sorting purposes)
        int overall = (reflexes + catching + punting) / 3;

        // Retrieve CoachID from the active session
        int CoachID = SessionManager.getCurrentCoachID();  // Using the session manager to fetch the active CoachID

        if (CoachID == -1) {
            // If no valid session is active, show an error message
            showAlert("Error", "You must be logged in to add an athlete", Alert.AlertType.ERROR);
        } else {
            connection =  DBconnection();
            try {
                // Connect to the database

                // Prepare SQL statement to insert athlete data
                psInsert = connection.prepareStatement(sql);
                psInsert.setInt(1, CoachID);
                psInsert.setInt(2, reflexes);
                psInsert.setInt(3, catching);
                psInsert.setInt(4, punting);
                psInsert.setInt(5, overall);
                psInsert.setString(6, firstname);
                psInsert.setString(7, lastname);
                psInsert.setInt(8, age);
                psInsert.setString(9, parentfirtname);
                psInsert.setString(10, parentlastname);
                psInsert.setString(11, email);
                psInsert.setInt(12, phonenumber);

                // Execute the insert query
                psInsert.executeUpdate();

                // Confirm successful addition
                System.out.println("Goalie added successfully.");
                showAlert("Success", "Goalie addedd successfully!", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                e.printStackTrace();
                // Show error message if insertion fails
                showAlert("Error", "An error occured while adding the athlete", Alert.AlertType.ERROR);

            } finally {
                // Close resources to prevent leaks
                try {
                    if (resultSet != null) resultSet.close();
                    if (psInsert != null) psInsert.close();
                    if (connection != null) connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }


        }
    }
    public static void addAthletebutton(ActionEvent event, String firstname, String lastname, String parentfirtname, String parentlastname, String email, int phonenumber, int shooting, int passing, int dribbling, int age) {
        // Initialize database connection and prepared statements
        Connection connection = null;
        String sql = "INSERT INTO outfielder (Coach_ID, Passing, Shooting, dribbling, overall, firstname, lastname, age, parentfirstname, parentlastname, parentemail, parentphone) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        ResultSet resultSet = null;
        PreparedStatement psInsert = null;

        //sets the overall socre for sorting purposes
        int overall = (passing + dribbling + shooting) / 3;


        // Retrieve CoachID from the active session
        int CoachID = SessionManager.getCurrentCoachID();  // Using the session manager to fetch the active CoachID

        if (CoachID == -1) {
            // If no valid session is active, show an error message
            showAlert("Error", "An error occured while adding the athlete", Alert.AlertType.ERROR);

        } else {
            connection = DBconnection();
            try{

                // Prepare SQL statement to insert athlete data
                psInsert = connection.prepareStatement(sql);
                psInsert.setInt(1, CoachID);
                psInsert.setInt(2, passing);
                psInsert.setInt(3, shooting);
                psInsert.setInt(4, dribbling);
                psInsert.setInt(5, overall);
                psInsert.setString(6, firstname);
                psInsert.setString(7, lastname);
                psInsert.setInt(8, age);
                psInsert.setString(9, parentfirtname);
                psInsert.setString(10, parentlastname);
                psInsert.setString(11, email);
                psInsert.setInt(12, phonenumber);

                // Execute the insert query
                psInsert.executeUpdate();

                // Confirm successful addition
                System.out.println("Athlete added successfully.");
                showAlert("Success", "Athlete addedd successfully!", Alert.AlertType.INFORMATION);

            } catch (SQLException e) {
                e.printStackTrace();
                // Show error message if insertion fails
                showAlert("Error", "An error occured while adding the athlete", Alert.AlertType.ERROR);


            } finally {
                // Close resources to prevent leaks
                try {
                    if (resultSet != null) resultSet.close();
                    if (psInsert != null) psInsert.close();
                    if (connection != null) connection.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                }


            }


        }
    }
    //This is the method to find an athlete so editing is possible
    public static void findAthlete(ActionEvent event, int coachID, String firstname, String lastname) {
        // Load all athletes for the given coach
        ArrayList<Athlete> athletesList = loadAthletesForCoach(coachID);

        // Perform search (we'll use linear search in this example, but binary search is also an option)
        Athlete foundAthlete = findAthleteInList(athletesList, firstname, lastname);

        if (foundAthlete != null) {
            // Athlete found, perform any action you want, e.g., navigate to the edit screen
            DBUtils.changeScene(event, "editAthlete.fxml", "Edit Athlete", null);
        } else {
            // If athlete is not found, show an alert
            System.out.println("Student doesn't exist");
            showAlert("Error", "Student doesn't exist", Alert.AlertType.ERROR);
        }
    }
    //Edits athletes
    public static void editPlayer(ActionEvent event, int athleteID, String firstname, String lastname, String parentfirtname, String parentlastname, String email, /* Using INTEGER so the data can be checked if null */Integer phonenumber, Integer skill1, Integer skill2, Integer skill3, Integer age) {
        Connection connection = null;
        // Retrieve CoachID from the active session
        int CoachID = SessionManager.getCurrentCoachID();  // Using the session manager to fetch the active CoachID

        if (CoachID == -1) {
            // If no valid session is active, show an error message
            DBUtils.showAlert("Error", "You must be logged in to add an athlete", Alert.AlertType.ERROR);


        } else {
            connection = DBconnection();
            //checks if player is a goalie, so the correct skills can be edited
            if(isGoalie(thefirstname, thelastname)){
                DBUtils.editGoalie(event, athleteID, firstname, lastname, parentfirtname, parentlastname, email, phonenumber, skill1, skill2, skill3, age);
            } else if(!isGoalie(thefirstname, thelastname)) {
                DBUtils.editOutfielder(event, athleteID, firstname, lastname, parentfirtname, parentlastname, email, phonenumber, skill1, skill2, skill3, age);


            }

        }
    }
    // Method to remove athlete from the database by first and last name
    public static void removeAthleteByName(String firstName, String lastName) {
        // SQL queries to delete athlete from goalie or outfielder table
        if(doesAthleteExist(currentCoachID, firstName, lastName)) {
            String deleteGoalieQuery = "DELETE FROM goalie WHERE firstName = ? AND lastName = ?";
            String deleteOutfielderQuery = "DELETE FROM outfielder WHERE firstName = ? AND lastName = ?";
            PreparedStatement pstmt = null;
            // Establish connection and execute the SQL delete queries
            Connection connection = null;

            connection = DBconnection();
            try{

                // First, try to delete from the goalie table
                pstmt  = connection.prepareStatement(deleteGoalieQuery);
                pstmt.setString(1, firstName);
                pstmt.setString(2, lastName);
                int rowsAffected = pstmt.executeUpdate();

                // If no rows were affected, the athlete may be in the outfielder table, so try deleting there
                if (rowsAffected == 0) {
                    pstmt = connection.prepareStatement(deleteOutfielderQuery);
                    pstmt.setString(1, firstName);
                    pstmt.setString(2, lastName);
                    pstmt.executeUpdate();  // Delete from outfielder table
                    DBUtils.showAlert("Success", "Athlete " + firstName + " " + lastName + " removed successfully.", Alert.AlertType.INFORMATION);

                }

            } catch (SQLException e) {
                e.printStackTrace();  // Log the exception or handle it appropriately
            } finally {
                // Ensure the PreparedStatement and connection are closed to avoid resource leaks
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();  // Handle closing exceptions
                }
            }
        }

        else {
            showAlert("Error", "Athlete doesn't exist", Alert.AlertType.ERROR);
        }
    }
    //This is the "editGoalie" method, to be used in the editAthlete method for ease of simplicity
    public static void editGoalie(ActionEvent event, int athleteId, String newFirstName, String newLastName, String newParentFirstName, String newParentLastName, String newEmail, Integer newPhoneNumber, /* Using INTEGER so the data can be checked if null */ Integer newReflexes, Integer newCatching, Integer newPunting, Integer newAge) {

        Connection connection = null;
        String selectQuery = "SELECT firstname, lastname, age, parentfirstname, parentlastname, parentemail, parentphone, reflexes, catching, punting, overall FROM goalie WHERE goalie_ID = ?";

        PreparedStatement psUpdate = null;
        ResultSet resultSet = null;
        PreparedStatement psQuery = null;
        int overall = (newCatching + newReflexes + newPunting)/3;
        int specificID = SessionManager.getSpecificID(thefirstname, thelastname);
        System.out.println("Specific ID: " + specificID);
        connection = DBconnection();

        try {
            // First, retrieve the current values for the goalie


            psQuery = connection.prepareStatement(selectQuery);
            psQuery.setInt(1, specificID);
            resultSet = psQuery.executeQuery();

            // Check if the goalie with the given ID exists
            if (resultSet.next()) {
                // If a new value is provided, use it; otherwise, keep the current value
                String firstName = (newFirstName != null && !newFirstName.isEmpty()) ? newFirstName : resultSet.getString("firstname");
                String lastName = (newLastName != null && !newLastName.isEmpty()) ? newLastName : resultSet.getString("lastname");
                String parentFirstName = (newParentFirstName != null && !newParentFirstName.isEmpty()) ? newParentFirstName : resultSet.getString("parentfirstname");
                String parentLastName = (newParentLastName != null && !newParentLastName.isEmpty()) ? newParentLastName : resultSet.getString("parentlastname");
                String email = (newEmail != null && !newEmail.isEmpty()) ? newEmail : resultSet.getString("parentemail");
                int phoneNumber = (newPhoneNumber != null) ? newPhoneNumber : resultSet.getInt("parentphone");
                int reflexes = (newReflexes != null) ? newReflexes : resultSet.getInt("reflexes");
                int catching = (newCatching != null) ? newCatching : resultSet.getInt("catching");
                int punting = (newPunting != null) ? newPunting : resultSet.getInt("punting");
                int age = (newAge != null) ? newAge : resultSet.getInt("age");

                // Update the goalie information with either new or unchanged values
                String updateQuery = "UPDATE goalie SET firstname = ?, lastname = ?, age = ?, parentfirstname = ?, parentlastname = ?, parentemail = ?, parentphone = ?, reflexes = ?, catching = ?, punting = ?, overall = ? WHERE goalie_ID = ?";
                psUpdate = connection.prepareStatement(updateQuery);
                psUpdate.setString(1, firstName);
                psUpdate.setString(2, lastName);
                psUpdate.setInt(3, age);
                psUpdate.setString(4, parentFirstName);
                psUpdate.setString(5, parentLastName);
                psUpdate.setString(6, email);
                psUpdate.setInt(7, phoneNumber);
                psUpdate.setInt(8, reflexes);
                psUpdate.setInt(9, catching);
                psUpdate.setInt(10, punting);
                psUpdate.setInt(11, overall);
                psUpdate.setInt(12, specificID);

                // Execute the update
                psUpdate.executeUpdate();

            } else {
                System.out.println("Goalie with the specified athlete_id does not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close all database resources
            try {
                if (resultSet != null) resultSet.close();
                if (psQuery != null) psQuery.close();
                if (psUpdate != null) psUpdate.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //Method to edit an athlete
    public static void editOutfielder(ActionEvent event, int athleteId, String newFirstName, String newLastName, String newParentFirstName, String newParentLastName, String newEmail, Integer newPhoneNumber, Integer newShooting, Integer newDribbling, Integer newPassing, Integer newAge) {

        Connection connection = null;
        String selectQuery= "SELECT firstname, lastname, age, parentfirstname, parentlastname, parentemail, parentphone, shooting, dribbling, passing, overall FROM outfielder WHERE outfielder_ID = ?";
        PreparedStatement psUpdate = null;
        PreparedStatement psQuery = null;
        ResultSet resultSet = null;
        int overall = (newShooting + newPassing + newDribbling)/3;
        int specificID = SessionManager.getSpecificID(thefirstname, thelastname);
        System.out.println("Specific ID: " + specificID);
        connection = DBconnection();

        try{


            psQuery = connection.prepareStatement(selectQuery);
            // First, retrieve the current values for the goalie
            psQuery.setInt(1, specificID);
            resultSet = psQuery.executeQuery();

            // Check if the goalie with the given ID exists
            if (resultSet.next()) {
                // If a new value is provided, use it; otherwise, keep the current value
                String firstName = (newFirstName != null && !newFirstName.isEmpty()) ? newFirstName : resultSet.getString("firstname");
                String lastName = (newLastName != null && !newLastName.isEmpty()) ? newLastName : resultSet.getString("lastname");
                String parentFirstName = (newParentFirstName != null && !newParentFirstName.isEmpty()) ? newParentFirstName : resultSet.getString("parentfirstname");
                String parentLastName = (newParentLastName != null && !newParentLastName.isEmpty()) ? newParentLastName : resultSet.getString("parentlastname");
                String email = (newEmail != null && !newEmail.isEmpty()) ? newEmail : resultSet.getString("parentemail");
                int phoneNumber = (newPhoneNumber != null) ? newPhoneNumber : resultSet.getInt("parentphone");
                int shooting = (newShooting != null) ? newShooting : resultSet.getInt("shooting");
                int dribbling = (newDribbling != null) ? newDribbling : resultSet.getInt("dribbling");
                int passing = (newPassing != null) ? newPassing : resultSet.getInt("passing");
                int age = (newAge != null) ? newAge : resultSet.getInt("age");

                // Update the outfielder information with either new or unchanged values
                String updateQuery = "UPDATE outfielder SET firstname = ?, lastname = ?, age = ?, parentfirstname = ?, parentlastname = ?, parentemail = ?, parentphone = ?, shooting = ?, dribbling = ?, passing = ?, overall = ? WHERE outfielder_ID = ?";
                psUpdate = connection.prepareStatement(updateQuery);
                psUpdate.setString(1, firstName);
                psUpdate.setString(2, lastName);
                psUpdate.setInt(3, age);
                psUpdate.setString(4, parentFirstName);
                psUpdate.setString(5, parentLastName);
                psUpdate.setString(6, email);
                psUpdate.setInt(7, phoneNumber);
                psUpdate.setInt(8, shooting);
                psUpdate.setInt(9, dribbling);
                psUpdate.setInt(10, passing);
                psUpdate.setInt(11, overall);
                psUpdate.setInt(12, specificID);

                // Execute the update
                psUpdate.executeUpdate();

            } else {
                System.out.println("Goalie with the specified athlete_id does not exist.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close all database resources
            try {
                if (resultSet != null) resultSet.close();
                if (psQuery != null) psQuery.close();
                if (psUpdate != null) psUpdate.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // Method to retrieve the athlete ID based on first and last name
    public static Integer getAthleteID(String firstName, String lastName){
        String query = "SELECT athlete_id FROM combined_athletes WHERE firstname = ? AND lastname = ?";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Integer athleteId = null;
        connection = DBconnection();

        try{

            // SQL query to retrieve athlete_id based on first and last nam
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Check if an athlete was found and retrieve the athlete_id
            if (resultSet.next()) {
                athleteId = resultSet.getInt("athlete_id");
            } else {
                System.out.println("No athlete found with the given first and last name.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close all database resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return athleteId;
    }
    //Gets the coach_ID so the foreign key is properly set up in the SQL database
// Method to get Coach_ID and store current user's credentials
    public static int getCoachID(String username, String password) {
        String sql = "SELECT Coach_ID FROM users WHERE username = ? AND password = ?";
        Connection connection = null;
        ResultSet resultSet = null;
        PreparedStatement psQuery = null;
        int coachID = -1;
        connection = DBconnection();
        try{


            psQuery = connection.prepareStatement(sql);
            psQuery.setString(1, username);
            psQuery.setString(2, password);

            resultSet = psQuery.executeQuery();

            if (resultSet.next()) {
                coachID = resultSet.getInt("Coach_ID");
                // Store the username and password after a successful login
                currentUsername = username;
                currentPassword = password;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (psQuery != null) psQuery.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return coachID;
    }
    // Method to check if an athlete is a goalie, given their first and last name
    public static boolean isGoalie(String firstname, String lastname) {
        String sql = "SELECT athlete_type FROM combined_athletes WHERE firstname = ? AND lastname = ?";
        ResultSet resultSet = null;
        boolean isGoalie = false;
        Connection connection = null;
        PreparedStatement psQuery = null;
        connection = DBconnection();
        try{
            // Establish a connection to the database


            // Query to check the athlete type based on first and last name
            psQuery = connection.prepareStatement(sql);
            psQuery.setString(1, firstname);
            psQuery.setString(2, lastname);

            resultSet = psQuery.executeQuery();

            // Check if the query returned a result and if the athlete type is 'goalie'
            if (resultSet.next()) {
                String athleteType = resultSet.getString("athlete_type");
                isGoalie = athleteType.equalsIgnoreCase("goalie");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close all database resources
            try {
                if (resultSet != null) resultSet.close();
                if (psQuery != null) psQuery.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isGoalie;
    }
    //Checks to see if an athlete exists
    public static boolean doesAthleteExist(int coachID, String firstName, String lastName) {
        // Load athletes for the given coach ID
        ArrayList<Athlete> athletes = loadAthletesForCoach(coachID);

        // Check if any athlete matches the given first and last name
        for (Athlete athlete : athletes) {
            if (athlete.getFirstName().equalsIgnoreCase(firstName) &&
                    athlete.getLastName().equalsIgnoreCase(lastName)) {
                return true; // Athlete found
            }
        }
        return false; // Athlete not found
    }




    //ALL METHODS RELATED TO EXERICSE
    //method to remove an exercise
    public static void removeExerciseByName(String exerciseName) {
        // SQL query to delete an exercise from the exercises table by its name
        String deleteExerciseQuery = "DELETE FROM exercises WHERE exercise_name = ?";
        // Establish connection and execute the SQL delete query
        Connection connection = null;
        PreparedStatement pstmt = null;
        connection = DBconnection();
        try{
            pstmt = connection.prepareStatement(deleteExerciseQuery);
            pstmt.setString(1, exerciseName);  // Set the exercise name parameter

            // Execute the delete statement
            int rowsAffected = pstmt.executeUpdate();

            // Show success message if the exercise was removed
            if (rowsAffected > 0) {
                showAlert("Success", "Exercise removed", Alert.AlertType.INFORMATION);

            } else {
                // Show warning message if no rows were affected (exercise not found)
                showAlert("Not Found", "Exercise Not Found", Alert.AlertType.WARNING);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Log the exception or handle it appropriately

            // Show error alert if there is an SQL exception
            showAlert("Error", "An error occured while trying to remove the exercise", Alert.AlertType.ERROR);

        } finally {
            // Ensure the PreparedStatement and connection are closed to avoid resource leaks
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();  // Handle closing exceptions
            }
        }
    }
    //This method is to add an exercise to the exerices table
    public static void addExercise(int coachId, String exerciseName, String exerciseLink, int difficulty) {
        String query = "INSERT INTO exercises (coach_id, exercise_name, exercise_link, difficulty) VALUES (?, ?, ?, ?)";
        String validationQuery = "SELECT user_id FROM users WHERE user_id = ?";
        Connection connection = null;
        connection = DBconnection();
        try (PreparedStatement validationPs = connection.prepareStatement(validationQuery)) {

            // Validate coachId
            validationPs.setInt(1, coachId);
            ResultSet rs = validationPs.executeQuery();
            if (!rs.next()) {
                System.out.println("Invalid coach ID: " + coachId);
                return; // Exit the method if the coachId is invalid
            }

            // Add exercise
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, coachId);
                ps.setString(2, exerciseName);
                ps.setString(3, exerciseLink);
                ps.setInt(4, difficulty);

                ps.executeUpdate();
                System.out.println("Exercise added successfully.");
                ArrayList<Exercise> e = SessionManager.fetchCoachExercises(currentCoachID);
                System.out.println(e);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //ALL METHODS RELATED TO THE SCHEDULE
    // Method to add a schedule to both the database and the currentCoachSchedule ArrayList
    public static void addSchedule(int coachID, String date, boolean isPractice, boolean isGame, String startTime, String endTime) {
        String query = "INSERT INTO schedules (coach_id, date, practice, game, start_time, end_time) VALUES (?, ?, ?, ?, ?, ?)";
        Connection connection = null;
        connection = DBconnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, coachID);
            pstmt.setString(2, date);
            pstmt.setBoolean(3, isPractice);
            pstmt.setBoolean(4, isGame);
            pstmt.setString(5, startTime);
            pstmt.setString(6, endTime);

            pstmt.executeUpdate();

            // After adding, reload the schedule to keep the in-memory list updated
            SessionManager.loadSchedule(coachID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static void removeScheduleFromDatabase(Schedule schedule) {
        String query = "DELETE FROM schedules WHERE coach_id = ? AND date = ?";
        Connection connection = null;

        connection = DBconnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, schedule.getCoachID());
            pstmt.setString(2, schedule.getDate());
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    //ALL METHODS RELATING TO SENDING EXERCISES
    //This receives the athlete's email
    public static String getAthleteEmail(int athleteId) throws SQLException {
        String email = null;
        String selectQuery = "SELECT parentemail FROM combined_athletes WHERE athlete_id = ?";
        Connection connection = null;

        connection = DBconnection();
        try (PreparedStatement ps = connection.prepareStatement(selectQuery)) {

            ps.setInt(1, athleteId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                email = rs.getString("parentemail");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return email;
    }
    // Method to send an email to an athlete with an exercise
    public static void sendExerciseEmail(int coachID, int athleteId, String exerciseName, String message) throws SQLException {
        // Get the athlete's email
        System.out.println("Processing athlete ID: " + athleteId);

        String athleteEmail = getAthleteEmail(athleteId);
        if (athleteEmail == null) {
            System.out.println("No athlete found with the given ID.");
            return;
        }

        // Fetch the coach's exercises
        List<Exercise> exercises = SessionManager.fetchCoachExercises(coachID);

        // Find the exercise with the matching name
        Exercise targetExercise = null;
        for (Exercise exercise : exercises) {
            if (exercise.getName().equals(exerciseName)) {
                targetExercise = exercise;
                break;
            }
        }

        // If the exercise wasn't found, print an error and return
        if (targetExercise == null) {
            System.out.println("Exercise not found.");
            return;
        }

        // Retrieve the exercise's difficulty and link
        String link = targetExercise.getLink();
        int difficulty = targetExercise.getDifficulty();

        // Setup email details
        String subject = "New Exercise Recommendation: " + exerciseName;
        String emailMessage = "Dear Athlete,\n\n"
                + "We have a new exercise for you to work on:\n"
                + "Exercise: " + exerciseName + "\n"
                + "Link: " + link + "\n"
                + "Difficulty: " + difficulty + "\n\n"
                + "Message: " + message + "\n\n"
                + "Best regards,\nYour Coaching Team";

        // Email configurations
        String fromEmail = SessionManager.getCoachEmailByID(coachID); // Your email address
        String password = "rixp qmqs ehty zvuo";     // Your email password

        // Set the SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com"); // SMTP host
        properties.put("mail.smtp.port", "587");  // SMTP port (587 for TLS)
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        // Create a session and authenticate the sender
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        // Create the message
        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(athleteEmail));
            msg.setSubject(subject);
            msg.setText(emailMessage);

            // Send the email
            Transport.send(msg);
            System.out.println("Email sent successfully to " + athleteEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Failed to send email.");
        }
    }




    //ALL SORTING METHODS
    //Sorting Algorithms
    //Sorts athletes by skill level
    public static void selectionSortBySkill(ObservableList<Athlete> athletes) {
        int n = athletes.size();

        // Perform selection sort on the list of athletes
        for (int i = 0; i < n - 1; i++) {
            // Assume the current index is the minimum
            int minIndex = i;

            // Find the smallest skill level in the remaining unsorted portion
            for (int j = i + 1; j < n; j++) {
                if (athletes.get(j).getSkill() < athletes.get(minIndex).getSkill()) {
                    minIndex = j;
                }
            }

            // Swap the found minimum skill element with the element at the current index
            if (minIndex != i) {
                Athlete temp = athletes.get(i);
                athletes.set(i, athletes.get(minIndex));
                athletes.set(minIndex, temp);
            }
        }
    }
    public static void bubbleSortByLastName(ObservableList<Athlete> athleteList) {
        int n = athleteList.size();

        // Bubble Sort algorithm
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                // Compare the last names
                if (athleteList.get(j).getLastName().compareTo(athleteList.get(j + 1).getLastName()) > 0) {
                    // Swap if they are in the wrong order
                    Athlete temp = athleteList.get(j);
                    athleteList.set(j, athleteList.get(j + 1));
                    athleteList.set(j + 1, temp);
                }
            }
        }
    }
    //Sort exercises by skill level:
    // Method to merge two sorted halves of the list
    private static void merge(ObservableList<Exercise> exercises, int left, int mid, int right) {
        // Create temporary lists for the left and right halves
        ArrayList<Exercise> leftList = new ArrayList<>(exercises.subList(left, mid + 1));
        ArrayList<Exercise> rightList = new ArrayList<>(exercises.subList(mid + 1, right + 1));

        // Merge the temporary lists back into the original list
        int i = 0, j = 0, k = left;

        while (i < leftList.size() && j < rightList.size()) {
            // Compare by difficulty (skill level) and add the lower value first
            if (leftList.get(i).getDifficulty() <= rightList.get(j).getDifficulty()) {
                exercises.set(k++, leftList.get(i++));
            } else {
                exercises.set(k++, rightList.get(j++));
            }
        }

        // Add the remaining elements of the left or right list, if any
        while (i < leftList.size()) {
            exercises.set(k++, leftList.get(i++));
        }
        while (j < rightList.size()) {
            exercises.set(k++, rightList.get(j++));
        }
    }
    // Method to perform merge sort on the exercises list
    public static void mergeSortExercisesBySkillLevel(ObservableList<Exercise> exercises, int left, int right) {
        if (left < right) {
            int mid = left + (right - left) / 2;

            // Recursively sort the two halves
            mergeSortExercisesBySkillLevel(exercises, left, mid);
            mergeSortExercisesBySkillLevel(exercises, mid + 1, right);

            // Merge the sorted halves
            merge(exercises, left, mid, right);
        }
    }
    // Method to get the sorted exercises list
    public static ObservableList<Exercise> getSortedExercisesBySkillLevel(ObservableList<Exercise> exercises) {
        // Perform merge sort on the entire list
        mergeSortExercisesBySkillLevel(exercises, 0, exercises.size() - 1);
        return exercises; // Return the sorted list
    }


    //VALIDATION EMAILS + MISC
    //Validates if a given email is actually an email
    public static boolean isValidEmail(String email) {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailPattern);
    }
    //Validates if skill is within skillbounds
    public static boolean isWithinSkills(Integer skill1, Integer skill2, Integer skill3) {
        Integer[] array = {skill1, skill2, skill3};
        for (int i = 0; i < 3; i++) {
            if(array[i] == null){
                array[i] = 15;
            }
            if (array[i] < 0 || array[i] > 100){
                return false;
            }
        }

        return true;

    }
    //Error
    public static void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public static Connection DBconnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost/goalproschema", "root", "GuitarandCelloguy7083");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}






