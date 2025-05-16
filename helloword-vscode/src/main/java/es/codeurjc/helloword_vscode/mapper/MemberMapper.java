package es.codeurjc.helloword_vscode.mapper;

import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.dto.MemberDTO;

import java.util.Collection;
import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberDTO toDTO(Member member);

    List<MemberDTO> toDTOs(Collection<Member> members);

    Member toDomain(MemberDTO memberDTO);
}
