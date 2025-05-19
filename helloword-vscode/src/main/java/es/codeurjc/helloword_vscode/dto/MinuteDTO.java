package es.codeurjc.helloword_vscode.dto;

import java.util.List;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Member;

public record MinuteDTO (
     Long id,
     String date,
     List<MemberDTO> participants,
     String content,
     Double duration,
     AssociationBasicDTO association
) {}


