package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.helloword_vscode.entities.MemberType;

public interface MemberTypeRepository extends JpaRepository<MemberType, Long> {
}
