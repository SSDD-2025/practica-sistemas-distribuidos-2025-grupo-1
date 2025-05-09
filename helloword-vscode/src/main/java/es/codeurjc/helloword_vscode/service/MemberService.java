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
import es.codeurjc.helloword_vscode.model.Member;
import es.codeurjc.helloword_vscode.repository.MemberRepository;

/*
 * This service class provides methods to perform various operations on Member entities,
 * such as saving, retrieving, and deleting users. It implements UserDetailsService to load user-specific 
 * data
*/
@Service
public class MemberService implements UserDetailsService {

    // Autowired repositories for database interactions //

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

	/* Save user */
    public void save(Member member) {
		memberRepository.save(member);
	}


	/* Find user by their name */
	public Optional<Member> findByName(String name) {
		return memberRepository.findByName(name);
	}


	/* Load data of user by finding them by their username */
    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Retrieve the user by username
		Member member = memberRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Map the user's roles to granted authorities
		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : member.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		// Return a UserDetails object containing the user's data
		return new org.springframework.security.core.userdetails.User(member.getName(), 
				member.getPwd(), roles);
	}


	/* Find user by ID */
	public Optional<Member> findById(long id) {
		return memberRepository.findById(id);
	}


	/* Find all users */
	public List<Member> findAll() {
		return memberRepository.findAll();
	}


	/* Delete user by ID */
	@Transactional
	public void deleteById(long id) throws IOException {
		// Retrieve user by ID
		Optional<Member> optUser = memberRepository.findById(id);
		if (optUser.isPresent()) {
			Member user = optUser.get();

			// 1. Delete member type in association
			List<MemberType> memberTypes = memberTypeService.findByMember(user);
			for (MemberType memberType : memberTypes) {
				memberTypeService.delete(memberType);
			}

			// 2. Delete participation to meetings
			List<Minute> minutes = minuteService.findAllByParticipantsContains(user);
			for (Minute minute : minutes) {
				minute.getParticipants().remove(user);
				minuteService.save(minute);
			}

			// 3. Delete user
			memberRepository.delete(user);
		}
	}


	/* Update user */
    public void updateUser(String username, String name, String surname, String password) {
        Optional<Member> optUser = findByName(username);
        if (optUser.isPresent()) {
            Member user = optUser.get();

            if (!user.getName().equals(name) && findByName(name).isPresent()) {
                throw new IllegalArgumentException("This username already exists");
            }

            user.setName(name);
            user.setSurname(surname);
            if (password != null && !password.isBlank()) {
                user.setPwd(passwordEncoder.encode(password));
            }
            save(user);
        }
    }


	/* Create user */
	public void createUser(String name, String surname, String password) {
        if (findByName(name).isPresent()) {
            throw new IllegalArgumentException("This username already exists");
        }
        Member user = new Member(name, surname, passwordEncoder.encode(password), "USER");
        save(user);
    }
}
