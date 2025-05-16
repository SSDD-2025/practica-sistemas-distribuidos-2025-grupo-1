package es.codeurjc.helloword_vscode.mapper;

import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.dto.MemberDTO;

public class MemberMapper {

    // Convert Member to MemberDTO
    public static MemberDTO toDTO(Member member) {
        if (member == null) return null;
        return new MemberDTO(
            member.getId(),
            member.getName(),
            member.getSurname(),
            member.getRoles()
        );
    }

    // Convert MemberDTO to Member
    public static Member toEntity(MemberDTO dto) {
        if (dto == null) return null;
        Member member = new Member();
        member.setId(dto.getId());
        member.setName(dto.getName());
        member.setSurname(dto.getSurname());
        member.setRoles(dto.getRoles());
        return member;
    }
}
