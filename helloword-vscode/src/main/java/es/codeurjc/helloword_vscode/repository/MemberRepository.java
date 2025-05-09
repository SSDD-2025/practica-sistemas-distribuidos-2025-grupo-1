package es.codeurjc.helloword_vscode.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.helloword_vscode.model.Member;

import java.util.Optional;

/**
 This interface extends JpaRepository to provide CRUD operations for the UtilidateurEntity entity
**/
public interface MemberRepository extends JpaRepository<Member, Long> {
    /* Find an Member by their name */
    Optional<Member> findByName(String name);
}
