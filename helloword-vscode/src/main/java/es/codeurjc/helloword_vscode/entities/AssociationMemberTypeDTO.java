package es.codeurjc.helloword_vscode.entities;

public class AssociationMemberTypeDTO {
    private Association association;
    private String memberType;

    public AssociationMemberTypeDTO(Association association, String memberType) {
        this.association = association;
        this.memberType = memberType;
    }

    public Association getAssociation() {
        return association;
    }

    public String getMemberType() {
        return memberType;
    }
}
