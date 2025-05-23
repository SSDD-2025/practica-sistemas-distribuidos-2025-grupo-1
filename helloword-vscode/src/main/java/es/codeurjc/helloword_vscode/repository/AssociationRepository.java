package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.Association;

/**
 This interface extends JpaRepository to provide CRUD operations for the Association entity
**/
public interface AssociationRepository extends JpaRepository<Association, Long> {
}
