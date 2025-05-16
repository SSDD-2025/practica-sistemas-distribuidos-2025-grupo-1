package es.codeurjc.helloword_vscode.dto;

import java.util.List;

public record AssociationDTO (
    
     Long id,
     String name,
     Boolean hasImage,
     List<Long> memberIds,
     List<Long> minuteIds

   
){}
