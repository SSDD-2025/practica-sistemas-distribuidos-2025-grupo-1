package es.codeurjc.helloword_vscode.dto;

import java.util.List;
import java.util.Collection;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.helloword_vscode.model.MemberType;


@Mapper(componentModel = "spring")
public interface AssociationMemberTypeMapper {

    @Mapping(source = "association.id", target = "associationId")
    @Mapping(source = "association.name", target = "associationName")
    @Mapping(source = "name", target = "memberType")
    AssociationMemberTypeDTO toDTO(MemberType memberType);

    List<AssociationMemberTypeDTO> toDTOs(Collection<MemberType> memberTypes);

    MemberType toDomain(MemberTypeDTO dto);
}

