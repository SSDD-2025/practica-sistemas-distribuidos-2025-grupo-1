package es.codeurjc.helloword_vscode.service;

import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.dto.MemberDTO;
import es.codeurjc.helloword_vscode.mapper.MemberMapper;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.repository.MemberRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MemberService /*implements UserDetailsService*/ {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberMapper memberMapper;

    public void save(MemberDTO dto) {
        Member member = memberMapper.toDomain(dto);
        memberRepository.save(member);
    }

    public Optional<MemberDTO> findById(long id) {
        return memberRepository.findById(id).map(memberMapper::toDTO);
    }

    public List<MemberDTO> findAll() {
        return memberMapper.toDTOs(memberRepository.findAll());
    }

    public void deleteById(long id) {
        if (!memberRepository.existsById(id)) {
            throw new ResourceNotFoundException("Member not found with id: " + id);
        }
        memberRepository.deleteById(id);
    }

    // Pour les opérations internes (ex : addUserToAssociation)
    public Optional<Member> findEntityById(long id) {
        return memberRepository.findById(id);
    }


    /* 
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Charge l'utilisateur (Member) par nom d'utilisateur (email ou login)
        // Ex :
        Member member = repository.findByEmail(username)
            .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé : " + username));

        return new org.springframework.security.core.userdetails.User(
            member.getEmail(),           // username
            member.getPassword(),       // hashed password
            List.of(new SimpleGrantedAuthority("ROLE_USER")) // ou autre logique de rôle
        );
    }
        */
}
