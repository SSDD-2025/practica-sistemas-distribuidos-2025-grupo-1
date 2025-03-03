package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.Minute;

public interface MinuteRepository extends JpaRepository<Minute, Long> {
}
