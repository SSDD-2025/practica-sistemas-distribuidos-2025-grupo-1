package es.codeurjc.helloword_vscode.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class User {
    @Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;
    private String name;
    private String surname;
    private String pwd;
    private boolean connected;
    private boolean admin;

    @OneToMany(mappedBy = "user")
    private List<Role> roles;

    // Constructor
    public User(String name, String surname, String pwd, boolean connected, boolean admin) {
        this.name = name;
        this.surname = surname;
        this.pwd = pwd;
        this.connected = connected;
        this.admin = admin;
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

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
