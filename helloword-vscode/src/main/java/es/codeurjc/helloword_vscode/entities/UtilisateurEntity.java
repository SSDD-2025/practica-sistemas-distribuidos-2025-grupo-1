package es.codeurjc.helloword_vscode.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;

@Entity // Marks the class as a JPA entity, to be mapped to a table in the database
public class UtilisateurEntity {

    @Id // Specifies the primary key field for the UtilisateurEntity
    @GeneratedValue(strategy = GenerationType.AUTO) // Automatically generates the value for the id field
    long id;

    // Name and surname of the user
    private String name;
    private String surname;
    
    // User's password (it might be hashed for security purposes)
    private String pwd;

    // List of roles assigned to the user, using an element collection (e.g., "Admin", "Member", etc.)
    @ElementCollection(fetch = FetchType.EAGER) // Elements are eagerly fetched when the entity is loaded
    private List<String> roles;

    // One-to-many relationship with the MemberType entity
    // This indicates that a user can have multiple member types (roles) in different associations
    @OneToMany(mappedBy = "Utilisateurentity") 
    private List<MemberType> memberTypes;

    // Default constructor required by JPA for entity initialization
    public UtilisateurEntity() {}

    // Constructor to initialize a user entity with specific values for name, surname, password, and roles
    public UtilisateurEntity(String name, String surname, String pwd, String... roles) {
        this.name = name;
        this.surname = surname;
        this.pwd = pwd;
        this.roles = List.of(roles);
    }

    // Getters and Setters for all attributes
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    // Getter and Setter for the roles assigned to the user
    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
