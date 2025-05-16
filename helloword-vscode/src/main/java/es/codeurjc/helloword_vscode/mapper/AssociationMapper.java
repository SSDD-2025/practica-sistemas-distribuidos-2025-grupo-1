package es.codeurjc.helloword_vscode.mapper;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.dto.AssociationDTO;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AssociationMapper {

    AssociationDTO toDTO(Association association);

    List<AssociationDTO> toDTOs(Collection<Association> associations);

    Association toDomain(AssociationDTO associationDTO);
}
