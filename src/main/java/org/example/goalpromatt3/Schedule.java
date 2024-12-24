package org.example.goalpromatt3;

import static org.example.goalpromatt3.SessionManager.currentCoachID;

public class Schedule {
    private int coachID;        // New field for coachID
    private String date;        // Date of the schedule
    private String startTime;   // Start time of the event
    private String endTime;     // End time of the event
    private boolean practice;   // True if it's a practice
    private boolean game;       // True if it's a game

    // Updated constructor to include coachID
    public Schedule(int coachID, String date, String startTime, String endTime, boolean practice, boolean game) {
        this.coachID = coachID;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.practice = practice;
        this.game = game;
    }

    // Getters for all fields
    public int getCoachID() {
        return coachID;
    }

    public String getDate() {
        return date;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public boolean isPractice() {
        return practice;
    }

    public boolean isGame() {
        return game;
    }
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Schedule for Coach ID: ").append(coachID).append("\n");
        sb.append("Date: ").append(date).append("\n");
        sb.append("Start Time: ").append(startTime).append("\n");
        sb.append("End Time: ").append(endTime).append("\n");
        sb.append("Type: ").append(practice ? "Practice" : game ? "Game" : "Unknown");
        return sb.toString();
    }
}
