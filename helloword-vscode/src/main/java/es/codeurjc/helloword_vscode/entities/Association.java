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

@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @OneToMany(mappedBy = "association")
    private List<MemberType> memberTypes;

    @OneToMany(mappedBy = "association", fetch = FetchType.LAZY)
    private List<Minute> minutes;

    public Association() {}

    // Constructor
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

    // Méthode pour obtenir les membres
    public List<UtilisateurEntity> getMembers() {
        return memberTypes.stream()
                     .map(MemberType::getUtilisateurEntity)
                     .collect(Collectors.toList());
    }

    // Méthode pour définir les membres
    public void setMembers(List<UtilisateurEntity> members) {
        // Assurez-vous que chaque utilisateur a un rôle associé à cette association
        this.memberTypes = members.stream()
                            .map(Utilisateurentity -> {
                                MemberType memberType = new MemberType();
                                memberType.setUtilisateurEntity(Utilisateurentity);
                                memberType.setAssociation(this);
                                return memberType;
                            })
                            .collect(Collectors.toList());
    }
}

