package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public class MemberDTO {
    private long id;
    private String name;
    private String surname;
    private List<String> roles;

    public MemberDTO() {}

    public MemberDTO(long id, String name, String surname, List<String> roles) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.roles = roles;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }
}
