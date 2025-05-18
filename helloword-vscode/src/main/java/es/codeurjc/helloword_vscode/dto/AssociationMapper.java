package es.codeurjc.helloword_vscode.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.helloword_vscode.model.Association;

@Mapper(componentModel = "spring")
public interface AssociationMapper {
    AssociationDTO toDTO(Association association);

    List<AssociationDTO> toDTOs(Collection<Association> associations);    

    @Mapping(target = "memberTypes", ignore = true)
    @Mapping(target = "minutes", ignore = true)
    @Mapping(target = "imageFile", ignore = true)
    Association toDomain(AssociationDTO associationDTO);
}
