package org.example.goalpromatt3;

public class Exercise {
    private String name;
    private int difficulty;
    private String link;

    public Exercise(String name, int difficulty, String link) {
        this.name = name;
        this.difficulty = difficulty;
        this.link = link;
    }

    // Getters and toString for displaying information
    public String getName() {
        return name;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return name + " (Difficulty: " + difficulty + ")";
    }
}

