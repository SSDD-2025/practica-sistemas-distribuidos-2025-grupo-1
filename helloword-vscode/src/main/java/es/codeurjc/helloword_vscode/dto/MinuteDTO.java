package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public record MinuteDTO (
     Long id,
     String date,
     List<Long> participantIds,
     String content,
     Double duration,
     Long associationId
){}
