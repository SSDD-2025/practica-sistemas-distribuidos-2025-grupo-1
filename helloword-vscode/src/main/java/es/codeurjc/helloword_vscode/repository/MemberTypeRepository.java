package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;

import java.util.List;

/**
 This interface extends JpaRepository to provide CRUD operations for the MemberType entity
**/
public interface MemberTypeRepository extends JpaRepository<MemberType, Long> {
    /* Find MemberType entities by the associated UtilisateurEntity */
    List<MemberType> findByUtilisateurEntity(UtilisateurEntity utilisateurEntity);
}
