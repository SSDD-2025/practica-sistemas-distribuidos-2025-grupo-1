package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;

import java.util.List;

/**
 This interface extends JpaRepository to provide CRUD operations for the Minute entity
**/
public interface MinuteRepository extends JpaRepository<Minute, Long> {
    /* Find all Minute entities that contain the specified participant */
    List<Minute> findAllByParticipantsContains(UtilisateurEntity participant);
}
