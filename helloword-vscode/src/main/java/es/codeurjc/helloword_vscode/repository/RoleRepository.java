package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
