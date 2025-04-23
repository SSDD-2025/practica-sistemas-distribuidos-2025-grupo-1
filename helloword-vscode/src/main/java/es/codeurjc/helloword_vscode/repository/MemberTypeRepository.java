package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.MemberType;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;

import java.util.List;

public interface MemberTypeRepository extends JpaRepository<MemberType, Long> {
    List<MemberType> findByUtilisateurEntity(UtilisateurEntity utilisateurEntity);
}
