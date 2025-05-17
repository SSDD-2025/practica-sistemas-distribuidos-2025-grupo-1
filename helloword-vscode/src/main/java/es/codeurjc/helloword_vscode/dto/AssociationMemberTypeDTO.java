package es.codeurjc.helloword_vscode.dto;

import es.codeurjc.helloword_vscode.model.Association;

/**
    This class is a Data Transfer Object (DTO) used to transfer data between software application subsystems.
    It encapsulates the association and the type of membership a user has within that association.
    It is particularly useful for transferring association membership information to the frontend or other services.
**/
public class AssociationMemberTypeDTO {
    private Association association;
    private String memberType;

    /* Constructor to initialize the DTO with association and member type */
    public AssociationMemberTypeDTO(Association association, String memberType) {
        this.association = association;
        this.memberType = memberType;
    }

    // Getters and Setters //
    public Association getAssociation() {
        return association;
    }

    public String getMemberType() {
        return memberType;
    }
}
