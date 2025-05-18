package es.codeurjc.helloword_vscode.dto;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Member;

public record MemberTypeDTO(Long id, String name, Member member, Association association) {}
