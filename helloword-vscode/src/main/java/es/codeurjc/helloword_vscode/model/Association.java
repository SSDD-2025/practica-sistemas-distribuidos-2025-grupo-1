package es.codeurjc.helloword_vscode.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import java.sql.Blob;

import jakarta.persistence.CascadeType;

@Entity
public class Association {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String name;

    @Lob
    private Blob imageFile;

    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberType> memberTypes;
    
    @OneToMany(mappedBy = "association", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Minute> minutes;    

    // Constructor
    public Association() {}

    public Association(String name, String imgAsso) {
        this.name = name;
        this.memberTypes = new ArrayList<>();
    }

    public Association(String name) {
        this.name = name;
        this.memberTypes = new ArrayList<>();
    }

    // Getters and Setters

    public Blob getImageFile() {
		return imageFile;
	}

	public void setImageFile(Blob imageFile) {
		this.imageFile = imageFile;
	}

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


    // Method to get members
    public List<UtilisateurEntity> getMembers() {
        return memberTypes.stream()
                     .map(MemberType::getUtilisateurEntity)
                     .collect(Collectors.toList());
    }

    // Method to define members
    public void setMembers(List<UtilisateurEntity> members) {
        // Assure that all users has a role in their association
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

