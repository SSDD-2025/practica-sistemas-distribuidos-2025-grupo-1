package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.Association;

public interface AssociationRepository extends JpaRepository<Association, Long> {
}
