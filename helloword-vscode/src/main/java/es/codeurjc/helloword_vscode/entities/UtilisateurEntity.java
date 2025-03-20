package es.codeurjc.helloword_vscode.entities;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class UtilisateurEntity {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
    private String name;
    private String surname;
    private String pwd;

    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;

    @OneToMany(mappedBy = "Utilisateurentity")
    private List<MemberType> memberTypes;

    // Constructor
    public UtilisateurEntity() {}

    public UtilisateurEntity(String name, String surname, String pwd, String... roles) {
        this.name = name;
        this.surname = surname;
        this.pwd = pwd;
        this.roles = List.of(roles);
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

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}
}
