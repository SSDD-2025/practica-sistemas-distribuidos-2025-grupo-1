package es.codeurjc.helloword_vscode.entities.role;

public class Role {
    private int id;
    private String name;
    private int idUser;
    private int idAssociation;

    // Constructeur
    public Role(int id, String name, int idUser, int idAssociation) {
        this.id = id;
        this.name = name;
        this.idUser = idUser;
        this.idAssociation = idAssociation;
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

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdAssociation() {
        return idAssociation;
    }

    public void setIdAssociation(int idAssociation) {
        this.idAssociation = idAssociation;
    }
}
