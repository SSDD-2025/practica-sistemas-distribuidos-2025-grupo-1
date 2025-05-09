package es.codeurjc.helloword_vscode.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class MemberType {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    // The type of membership (e.g., 'member', 'admin')
    private String name;

    @ManyToOne
    @JoinColumn(name = "idUtilisateur")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "idAssociation")
    private Association association;


    /* Default constructor */
    public MemberType() {}


    /**
     * Parameterized constructor to initialize the MemberType with name, user, and association.
     *
     * @param name The type of membership.
     * @param member The user entity associated with this membership.
     * @param association The association to which the user belongs.
     */
    public MemberType(String name, Member member, Association association) {
        this.name = name;
        this.member = member;
        this.association = association;
    }


    // Getters and Setters //

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

    public Member getMember() {
        return member;
    }

    public void setMember(Member member) {
        this.member = member;
    }

    public Association getAssociation() {
        return association;
    }

    public void setAssociation(Association association) {
        this.association = association;
    }
}
