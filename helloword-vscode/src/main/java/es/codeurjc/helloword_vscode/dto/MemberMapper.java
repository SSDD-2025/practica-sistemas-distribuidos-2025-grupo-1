package es.codeurjc.helloword_vscode.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import es.codeurjc.helloword_vscode.model.Member;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "memberTypes", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "associations", ignore = true)
    @Mapping(target = "associationsWithRoles", ignore = true)
    @Mapping(target = "minutes", ignore = true)
    Member toDomain(MemberDTO dto);

    MemberDTO toDTO(Member member);

    List<MemberDTO> toDTOs(Collection<Member> members);
}


