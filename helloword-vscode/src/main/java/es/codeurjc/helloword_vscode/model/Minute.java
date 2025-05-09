package es.codeurjc.helloword_vscode.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

@Entity
public class Minute {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String date;

    @ManyToMany
    @JoinTable(
        name = "minute_participants",
        joinColumns = @JoinColumn(name = "minute_id"),
        inverseJoinColumns = @JoinColumn(name = "Utilisateur_id")
    )
    private List<Member> participants;

    private String content;
    private double duration;

    @ManyToOne
    @JoinColumn(name = "id_association", nullable = false)
    private Association association;

    /**
     * Parameterized constructor to initialize the Minute with date, participants, content, duration, and association.
     *
     * @param date The date of the minute.
     * @param participants The list of participants in the minute.
     * @param content The content of the minute.
     * @param duration The duration of the minute.
     * @param association The association to which the minute belongs.
     */
    public Minute(String date, List<Member> participants, String content, double duration, Association association) {
        this.date = date;
        this.participants = participants;
        this.content = content;
        this.duration = duration;
        this.association = association;
    }

    /* Default constructor */
    public Minute() {}


    // Getters and Setters //
    
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Member> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Member> participants) {
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

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
