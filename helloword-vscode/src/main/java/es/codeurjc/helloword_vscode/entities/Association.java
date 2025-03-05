package es.codeurjc.helloword_vscode.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;

@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    // One-to-many relationship with MemberType (roles within the association)
    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberType> memberTypes;
    
    // One-to-many relationship with Minute (meeting records)
    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Minute> minutes;    

    public Association() {}

    // Constructor to initialize the association with a name
    public Association(String name) {
        this.name = name;
        this.memberTypes = new ArrayList<>();
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

    public List<MemberType> getMemberTypes() {
        return memberTypes;
    }

    public void setMemberTypes(List<MemberType> memberTypes) {
        this.memberTypes = memberTypes;
    }

    public List<Minute> getMinutes() {
        return minutes;
    }

    public void setMinutes(List<Minute> minutes) {
        this.minutes = minutes;
    }

    // Retrieves a list of users who are members of the association
    public List<UtilisateurEntity> getMembers() {
        return memberTypes.stream()
                     .map(MemberType::getUtilisateurEntity)
                     .collect(Collectors.toList());
    }

    // Assigns a list of users as members by creating corresponding MemberType entities
    public void setMembers(List<UtilisateurEntity> members) {
        this.memberTypes = members.stream()
                            .map(utilisateurEntity -> {
                                MemberType memberType = new MemberType();
                                memberType.setUtilisateurEntity(utilisateurEntity);
                                memberType.setAssociation(this);
                                return memberType;
                            })
                            .collect(Collectors.toList());
    }
}
