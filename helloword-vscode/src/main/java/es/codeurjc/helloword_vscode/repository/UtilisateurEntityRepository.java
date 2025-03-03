package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

public interface UtilisateurEntityRepository extends JpaRepository<UtilisateurEntity, Long> {
}
