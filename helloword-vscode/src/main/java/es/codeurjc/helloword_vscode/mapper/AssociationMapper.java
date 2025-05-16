package es.codeurjc.helloword_vscode.mapper;


import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.dto.AssociationDTO;

public class AssociationMapper {

    // Convert Association to AssociationDTO
    public static AssociationDTO toDTO(Association association) {
        if (association == null) return null;
        return new AssociationDTO(
            association.getId(),
            association.getName(),
            association.getDescription()
        );
    }

    // Convert AssociationDTO to Association
    public static Association toEntity(AssociationDTO dto) {
        if (dto == null) return null;
        Association association = new Association();
        association.setId(dto.getId());
        association.setName(dto.getName());
        association.setDescription(dto.getDescription());
        return association;
    }
}
