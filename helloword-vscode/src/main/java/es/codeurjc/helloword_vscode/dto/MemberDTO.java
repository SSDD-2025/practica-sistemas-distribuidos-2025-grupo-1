package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public record MemberDTO (
     Long id,
     String name,
     String surname,
     List<String> roles
){}
