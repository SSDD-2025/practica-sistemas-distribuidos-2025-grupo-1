package es.codeurjc.helloword_vscode.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToMany(mappedBy = "association")
    private List<Role> roles;

    @OneToMany(mappedBy = "association")
    private List<Minute> minutes;

    public Association() {}

    // Constructor
    public Association(String name) {
        this.name = name;
        this.roles = new ArrayList<>();
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

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    public List<Minute> getMinutes() {
        return minutes;
    }

    public void setMinutes(List<Minute> minutes) {
        this.minutes = minutes;
    }

    // Méthode pour obtenir les membres
    public List<User> getMembers() {
        return roles.stream()
                     .map(Role::getUser)
                     .collect(Collectors.toList());
    }

    // Méthode pour définir les membres
    public void setMembers(List<User> members) {
        // Assurez-vous que chaque utilisateur a un rôle associé à cette association
        this.roles = members.stream()
                            .map(user -> {
                                Role role = new Role();
                                role.setUser(user);
                                role.setAssociation(this);
                                return role;
                            })
                            .collect(Collectors.toList());
    }
}

