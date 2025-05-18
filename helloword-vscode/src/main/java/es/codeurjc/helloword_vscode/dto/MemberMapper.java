package es.codeurjc.helloword_vscode.dto;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

import es.codeurjc.helloword_vscode.model.Member;

@Mapper(componentModel = "spring")
public interface MemberMapper {
    MemberDTO toDTO(Member member);
    List<MemberDTO> toDTOs(Collection<Member> members);
    Member toDomain(MemberDTO dto);
}

