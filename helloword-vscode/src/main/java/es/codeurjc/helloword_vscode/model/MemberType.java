package es.codeurjc.helloword_vscode.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // The type of membership (e.g., 'member', 'admin')
    private String name;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private UtilisateurEntity utilisateurEntity;

    @ManyToOne
    @JoinColumn(name = "idAssociation")
    private Association association;


    /* Default constructor */
    public MemberType() {}


    /**
     * Parameterized constructor to initialize the MemberType with name, user, and association.
     *
     * @param name The type of membership.
     * @param utilisateurEntity The user entity associated with this membership.
     * @param association The association to which the user belongs.
     */
    public MemberType(String name, UtilisateurEntity utilisateurEntity, Association association) {
        this.name = name;
        this.utilisateurEntity = utilisateurEntity;
        this.association = association;
    }


    // Getters and Setters //

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
        return utilisateurEntity;
    }

    public void setUtilisateurEntity(UtilisateurEntity utilisateurEntity) {
        this.utilisateurEntity = utilisateurEntity;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
