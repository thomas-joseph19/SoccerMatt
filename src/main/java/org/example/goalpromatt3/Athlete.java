package org.example.goalpromatt3;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.Comparator;

import static org.example.goalpromatt3.SessionManager.currentCoachID;

public class Athlete {

    private String firstName;
    private String lastName;
    private int skill;
    private String parentFirstName;
    private String parentLastName;
    private String email;
    private String phoneNumber;
    private int coachID;
    private int athleteID;
    private String athleteType; // "goalie" or "outfielder"

    // Constructor
    public Athlete(String firstName, String lastName, int skill, String parentFirstName, String parentLastName,
                   String email, String phoneNumber, int coachID, int athleteID, String athleteType) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.skill = skill;
        this.parentFirstName = parentFirstName;
        this.parentLastName = parentLastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.coachID = coachID;
        this.athleteID = athleteID;
        this.athleteType = athleteType; // Set athleteType as String ("goalie" or "outfielder")
    }

    // Getters and setters
    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getSkill() {
        return skill;
    }

    public String getParentFirstName() {
        return parentFirstName;
    }

    public String getParentLastName() {
        return parentLastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getCoachID() {
        return coachID;
    }

    public int getAthleteID() {
        return athleteID;
    }

    public String getAthleteType() {
        return athleteType;
    } // Returns "goalie" or "outfielder"


    //binary search method
    public static Athlete findAthleteInList(ArrayList<Athlete> athletesList, String firstname, String lastname) {
        // Sort the list before performing binary search (optional if already sorted)
        ObservableList<Athlete> athletes = FXCollections.observableArrayList(SessionManager.loadAthletesForCoach(currentCoachID));

        DBUtils.bubbleSortByLastName(athletes);

        // Perform binary search on the sorted list
        int index = binarySearch(athletesList, firstname, lastname);
        if (index >= 0) {
            return athletesList.get(index);  // Return the found athlete
        }
        return null;  // Return null if not found
    }

    // Binary search implementation to find athlete
    public static int binarySearch(ArrayList<Athlete> athletesList, String firstname, String lastname) {
        int low = 0;
        int high = athletesList.size() - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            Athlete midAthlete = athletesList.get(mid);

            // Compare last names first, then first names if last names are the same
            int compareLastName = lastname.compareTo(midAthlete.getLastName());
            int compareFirstName = firstname.compareTo(midAthlete.getFirstName());

            if (compareLastName == 0 && compareFirstName == 0) {
                return mid;  // Return the index if both first and last names match
            }

            if (compareLastName < 0 || (compareLastName == 0 && compareFirstName < 0)) {
                high = mid - 1;  // Search in the left half if last name is smaller or last names match but first name is smaller
            } else {
                low = mid + 1;  // Search in the right half
            }
        }

        return -1;  // Return -1 if the athlete is not found
    }
}
