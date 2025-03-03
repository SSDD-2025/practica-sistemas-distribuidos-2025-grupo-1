package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
