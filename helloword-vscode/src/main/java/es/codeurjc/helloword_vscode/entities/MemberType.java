package es.codeurjc.helloword_vscode.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;

@Entity // Marks the class as a JPA entity, to be mapped to a table in the database
public class MemberType {

    @Id // Specifies the primary key field
    @GeneratedValue(strategy = GenerationType.AUTO) // Auto-generates the id value
    private long id;

    // The name of the member type (e.g., "President", "Secretary")
    private String name;

    // Many-to-one relationship with UtilisateurEntity (user entity)
    // This means a member type is associated with one user, but multiple member types can exist for different users
    @ManyToOne(cascade = CascadeType.REMOVE) // Cascade remove action ensures that when a member type is removed, the associated user is also deleted if no longer in use
    @JoinColumn(name = "idUtilisateur") // Foreign key linking to the 'UtilisateurEntity' table
    private UtilisateurEntity utilisateurEntity;

    // Many-to-one relationship with Association entity
    // This means a member type is linked to one association, but an association can have many member types
    @ManyToOne(cascade = CascadeType.REMOVE) // Cascade remove action ensures that when a member type is removed, the association's relation is also updated
    @JoinColumn(name = "idAssociation") // Foreign key linking to the 'Association' table
    private Association association;

    // Default constructor required by JPA for entity initialization
    public MemberType() {}

    // Constructor to initialize a member type with its name, associated user, and association
    public MemberType(String name, UtilisateurEntity utilisateurEntity, Association association) {
        this.name = name;
        this.utilisateurEntity = utilisateurEntity;
        this.association = association;
    }

    // Getters and Setters
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

    // Getter and Setter for the associated user
    public UtilisateurEntity getUtilisateurEntity() {
        return utilisateurEntity;
    }

    public void setUtilisateurEntity(UtilisateurEntity utilisateurEntity) {
        this.utilisateurEntity = utilisateurEntity;
    }

    // Getter and Setter for the associated association
    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
