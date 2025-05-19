package es.codeurjc.helloword_vscode.dto;

import java.util.List;

import es.codeurjc.helloword_vscode.model.Association;
import es.codeurjc.helloword_vscode.model.Member;

public record MinuteDTO (
     Long id,
     String date,
     List<Member> participants,
     String content,
     Double duration,
     Association association
){}
