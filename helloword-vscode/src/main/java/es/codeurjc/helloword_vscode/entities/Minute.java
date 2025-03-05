package es.codeurjc.helloword_vscode.entities;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;

@Entity // Marks the class as a JPA entity, which will be mapped to a table in the database
public class Minute {

    @Id // Specifies the primary key field for the Minute entity
    @GeneratedValue(strategy = GenerationType.AUTO) // Automatically generates the value for the id field
    private long id;

    // The date of the meeting
    private String date;

    // Many-to-many relationship with UtilisateurEntity (users/participants of the meeting)
    // This means many users can attend multiple meetings, and each meeting can have multiple users
    @ManyToMany
    @JoinTable(
        name = "minute_participants", // The name of the join table that links minutes and participants
        joinColumns = @JoinColumn(name = "minute_id"), // The column that refers to the minute entity
        inverseJoinColumns = @JoinColumn(name = "Utilisateur_id") // The column that refers to the participant (UtilisateurEntity)
    )
    private List<UtilisateurEntity> participants;

    // Content of the meeting (the minutes written during the meeting)
    private String content;

    // Duration of the meeting in hours (e.g., 1.5 hours)
    private double duration;

    // Many-to-one relationship with the Association entity (the association that organized the meeting)
    @ManyToOne
    @JoinColumn(name = "idAssociation") // Foreign key column linking to the Association entity
    private Association association;

    // Constructor to initialize the Minute entity with specific values for its attributes
    public Minute(String date, List<UtilisateurEntity> participants, String content, double duration, Association association) {
        this.date = date;
        this.participants = participants;
        this.content = content;
        this.duration = duration;
        this.association = association;
    }

    // Default constructor required by JPA for entity initialization
    public Minute() {}

    // Getters and Setters for all attributes
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

    public List<UtilisateurEntity> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UtilisateurEntity> participants) {
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
