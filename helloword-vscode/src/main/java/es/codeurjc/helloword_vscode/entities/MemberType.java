package es.codeurjc.helloword_vscode.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;

@Entity
public class MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private UtilisateurEntity Utilisateurentity;

    @ManyToOne
    @JoinColumn(name = "idAssociation")
    private Association association;

    // Constructeur par défaut
    public MemberType() {}

    // Constructeur avec paramètres
    public MemberType(String name, UtilisateurEntity Utilisateurentity, Association association) {
        this.name = name;
        this.Utilisateurentity = Utilisateurentity;
        this.association = association;
    }

    // Getters et Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UtilisateurEntity getUtilisateurEntity() {
        return Utilisateurentity;
    }

    public void setUtilisateurEntity(UtilisateurEntity Utilisateurentity) {
        this.Utilisateurentity = Utilisateurentity;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
