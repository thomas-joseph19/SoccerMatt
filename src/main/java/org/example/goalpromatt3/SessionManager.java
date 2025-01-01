package org.example.goalpromatt3;

import java.sql.*;
import java.util.ArrayList;

public class SessionManager {
    // Variables to hold session data
    public static String currentUsername;  // Stores the username of the current user
    public static String currentPassword;  // Stores the password of the current user
    public static int currentCoachID;
    public static String currentCoachEmail;
    public static ArrayList<Exercise> exercises = new ArrayList<Exercise>(); // 2D ArrayList to store name and difficulty
    public static ArrayList<Schedule> currentCoachSchedule = new ArrayList<Schedule>();  // Stores the current coach's schedule of practices and games
    public static ArrayList<Athlete> athletes = new ArrayList<Athlete>(); // Stores athletes of the current coach

    // Stores the CoachID of the current user
    private SessionManager() {
    }

    public static void startSession(String username, String password, int coachID) {
        currentUsername = username;
        currentPassword = password;
        currentCoachID = coachID;
        currentCoachSchedule = loadSchedule(coachID);
        currentCoachEmail = getCoachEmailByID(coachID);
        athletes = loadAthletesForCoach(coachID);// Load athletes when session starts

    }

    // Method to retrieve the coach's email from the users table given the coach's ID
    public static String getCoachEmailByID(int coachID) {
        // SQL query to retrieve the coach's email from the users table
        String query = "SELECT email FROM users WHERE user_id = ?";



        Connection connection = null;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        connection = DBUtils.DBconnection();
        try {
            // Establish connection to the database


            // Prepare the SQL statement with the coach's ID
            pstmt = connection.prepareStatement(query);
            pstmt.setInt(1, coachID);

            // Execute the query
            resultSet = pstmt.executeQuery();

            // Check if a result was returned
            if (resultSet.next()) {
                // Retrieve and return the email
                return resultSet.getString("email");
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Log the exception or handle it appropriately
        } finally {
            // Ensure resources are closed to avoid leaks
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
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

        // Return null if no email was found
        return null;
    }


    public static int getCurrentCoachID() {
        return currentCoachID;
    }


    public static ArrayList<Exercise> fetchCoachExercises(int coachID) {
        ArrayList<Exercise> exercises = new ArrayList<>(); // Initialize the list to hold Exercise objects
        String query = "SELECT exercise_name, difficulty, exercise_link FROM exercises WHERE coach_id = ?"; // Assuming 'exercise_link' exists in the database
        Connection connection = null;
        connection = DBUtils.DBconnection();
        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, coachID);  // Set the coach ID parameter
            ResultSet rs = ps.executeQuery();  // Execute the query

            // Iterate over the result set
            while (rs.next()) {
                String exerciseName = rs.getString("exercise_name"); // Get exercise name
                int difficulty = rs.getInt("difficulty"); // Get difficulty
                String link = rs.getString("exercise_link"); // Get exercise link (assuming it's available in the DB)

                // Create a new Exercise object and add it to the list
                Exercise exercise = new Exercise(exerciseName, difficulty, link);
                exercises.add(exercise);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle SQL exceptions
        }

        return exercises;  // Return the list of Exercise objects
    }

    public static ArrayList<Schedule> loadSchedule(int CoachID) {
        ArrayList<Schedule> schedule = new ArrayList<Schedule>();
        // Updated query to also retrieve startTime and endTime
        String query = "SELECT date, start_time, end_time, practice, game FROM schedules WHERE coach_id = ?";
        Connection connection = null;
        connection = DBUtils.DBconnection();
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {

            pstmt.setInt(1, CoachID); // Use the passed-in CoachID
            ResultSet resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                // Retrieve date, start_time, end_time, practice, and game from the result set
                String date = resultSet.getString("date");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                boolean practice = resultSet.getBoolean("practice");
                boolean game = resultSet.getBoolean("game");

                // Add each schedule to the list
                Schedule schedule1 = new Schedule(CoachID, date, startTime, endTime, practice, game);
                schedule.add(schedule1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedule; // Return the list of schedules with the start and end times
    }

    // New method to load athletes for a given Coach ID
    public static ArrayList<Athlete> loadAthletesForCoach(int coachID) {
        ArrayList<Athlete> athletesList = new ArrayList<>();
        String query = "SELECT firstname, lastname, overall, parentfirstname, parentlastname, parentemail, parentphone, athlete_id, athlete_type FROM combined_athletes WHERE coach_id = ?";
        Connection connection = null;
        connection = DBUtils.DBconnection();
        try (PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, coachID); // Set the coach ID parameter
            ResultSet rs = ps.executeQuery();  // Execute the query

            // Iterate over the result set
            while (rs.next()) {
                String firstName = rs.getString("firstname");
                String lastName = rs.getString("lastname");
                int skill = rs.getInt("overall");
                String parentFirstName = rs.getString("parentfirstname");
                String parentLastName = rs.getString("parentlastname");
                String email = rs.getString("parentemail");
                String phoneNumber = rs.getString("parentphone");
                int athleteID = rs.getInt("athlete_id"); // Retrieve athlete ID
                String athleteType = rs.getString("athlete_type"); // Retrieve athlete type

                // Create a new Athlete object and add it to the list
                Athlete athlete = new Athlete(firstName, lastName, skill, parentFirstName, parentLastName, email, phoneNumber, coachID, athleteID, athleteType);
                athletesList.add(athlete);
            }

        } catch (SQLException e) {
            e.printStackTrace();  // Handle SQL exceptions
        }

        return athletesList;  // Return the list of Athlete objects
    }

    // Gets individual ID from either outfielder or goalie table
    public static int getSpecificID(String firstName, String lastName) {
        Connection connection = null;
        PreparedStatement pstmtGoalie = null;
        PreparedStatement pstmtOutfielder = null;
        ResultSet resultSet = null;
        int athleteId = -1; // Default value if no ID is found

        try {
            connection = DBUtils.DBconnection();

            // Query to check the goalies table
            String queryGoalie = "SELECT goalie_ID FROM goalie WHERE firstname = ? AND lastname = ?";
            pstmtGoalie = connection.prepareStatement(queryGoalie);
            pstmtGoalie.setString(1, firstName);
            pstmtGoalie.setString(2, lastName);
            resultSet = pstmtGoalie.executeQuery();

            // If the athlete is found in the goalies table, return their goalie_id
            if (resultSet.next()) {
                athleteId = resultSet.getInt("goalie_id");
                return athleteId; // Return immediately if found
            }

            // Query to check the outfielders table
            String queryOutfielder = "SELECT outfielder_ID FROM outfielder WHERE firstname = ? AND lastname = ?";
            pstmtOutfielder = connection.prepareStatement(queryOutfielder);
            pstmtOutfielder.setString(1, firstName);
            pstmtOutfielder.setString(2, lastName);
            resultSet = pstmtOutfielder.executeQuery();

            // If the athlete is found in the outfielders table, return their outfielder_id
            if (resultSet.next()) {
                athleteId = resultSet.getInt("outfielder_id");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources to prevent memory leaks
            try {
                if (resultSet != null) resultSet.close();
                if (pstmtGoalie != null) pstmtGoalie.close();
                if (pstmtOutfielder != null) pstmtOutfielder.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return athleteId;
    }
}
