package es.codeurjc.helloword_vscode.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.dto.MemberDTO;
import es.codeurjc.helloword_vscode.mapper.MemberMapper;
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.repository.MemberRepository;

/*
 * This service class provides methods to perform various operations on Member entities,
 * such as saving, retrieving, and deleting users. It implements UserDetailsService to load user-specific 
 * data
*/
@Service
public class MemberService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberTypeService memberTypeService;

    @Autowired
    @Lazy
    private MinuteService minuteService;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MemberMapper memberMapper;

    public void save(MemberDTO dto) {
        Member member = memberMapper.toEntity(dto);
        memberRepository.save(member);
    }

    public Optional<MemberDTO> findByName(String name) {
        return memberRepository.findByName(name).map(memberMapper::toDTO);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByName(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        List<GrantedAuthority> roles = member.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        return new org.springframework.security.core.userdetails.User(member.getName(), member.getPwd(), roles);
    }

    public Optional<MemberDTO> findById(long id) {
        return memberRepository.findById(id).map(memberMapper::toDTO);
    }

    public Optional<Member> findEntityById(long id) {
        return memberRepository.findById(id);
    }

    public List<MemberDTO> findAll() {
        return memberRepository.findAll().stream().map(memberMapper::toDTO).toList();
    }

    @Transactional
    public void deleteById(long id) throws IOException {
        Optional<Member> optUser = memberRepository.findById(id);
        if (optUser.isPresent()) {
            Member user = optUser.get();

            List<MemberType> memberTypes = memberTypeService.findByMember(user);
            for (MemberType memberType : memberTypes) {
                memberTypeService.delete(memberType);
            }

            List<Minute> minutes = minuteService.findAllByParticipantsContains(user);
            for (Minute minute : minutes) {
                minute.getParticipants().remove(user);
                minuteService.save(minute);
            }

            memberRepository.delete(user);
        }
    }

    public void updateUser(String username, String name, String surname, String password) {
        Optional<Member> optUser = memberRepository.findByName(username);
        if (optUser.isPresent()) {
            Member user = optUser.get();

            if (!user.getName().equals(name) && memberRepository.findByName(name).isPresent()) {
                throw new IllegalArgumentException("This username already exists");
            }

            user.setName(name);
            user.setSurname(surname);
            if (password != null && !password.isBlank()) {
                user.setPwd(passwordEncoder.encode(password));
            }
            memberRepository.save(user);
        }
    }

    public void createUser(String name, String surname, String password) {
        if (memberRepository.findByName(name).isPresent()) {
            throw new IllegalArgumentException("This username already exists");
        }
        Member user = new Member(name, surname, passwordEncoder.encode(password), "USER");
        memberRepository.save(user);
    }
}
