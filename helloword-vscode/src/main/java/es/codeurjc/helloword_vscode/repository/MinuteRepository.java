package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;

import java.util.List;

public interface MinuteRepository extends JpaRepository<Minute, Long> {
    List<Minute> findAllByParticipantsContains(UtilisateurEntity participant);
}
