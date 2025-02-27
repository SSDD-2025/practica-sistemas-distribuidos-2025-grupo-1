package es.codeurjc.helloword_vscode.entities.association;

import java.util.List;
import es.codeurjc.helloword_vscode.entities.user.User;

public class Association {
    private int id;
    private String name;
    private List<User> members;

    // Constructeur
    public Association(int id, String name, List<User> members) {
        this.id = id;
        this.name = name;
        this.members = members;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }
}
