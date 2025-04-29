package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.UtilisateurEntity;

import java.util.Optional;

/**
 This interface extends JpaRepository to provide CRUD operations for the UtilidateurEntity entity
**/
public interface UtilisateurEntityRepository extends JpaRepository<UtilisateurEntity, Long> {
    /* Find an UtilisateurEntity by their name */
    Optional<UtilisateurEntity> findByName(String name);
}
