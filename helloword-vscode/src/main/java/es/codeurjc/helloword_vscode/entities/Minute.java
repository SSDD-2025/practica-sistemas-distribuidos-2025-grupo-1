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
    private List<UtilisateurEntity> participants;

    private String content;
    private double duration;

    @ManyToOne
    @JoinColumn(name = "idAssociation")
    private Association association;

    // Constructeur
    public Minute(String date, List<UtilisateurEntity> participants, String content, double duration, Association association) {
        this.date = date;
        this.participants = participants;
        this.content = content;
        this.duration = duration;
        this.association = association;
    }

    // Constructeur par défaut
    public Minute() {}

    // Getters et Setters
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
