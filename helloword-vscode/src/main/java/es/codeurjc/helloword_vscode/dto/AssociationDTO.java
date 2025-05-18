package es.codeurjc.helloword_vscode.dto;

import java.sql.Blob;
import java.util.List;

import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;

public record AssociationDTO (
    Long id,
    String name,
    boolean image,
    Blob imageFile,
    List<MemberType> memberTypes,
    List<Minute> minutes
){}
