package es.codeurjc.helloword_vscode.entities.Meeting;

import java.util.List;
import es.codeurjc.helloword_vscode.entities.user.User;

public class Meeting {
    private int id;
    private String date;
    private List<User> participants;
    private String content;
    private double duration;

    // Constructeur
    public Meeting(int id, String date, List<User> participants, String content, double duration) {
        this.id = id;
        this.date = date;
        this.participants = participants;
        this.content = content;
        this.duration = duration;
    }

    // Getters and Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<User> getParticipants() {
        return participants;
    }

    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }
}
