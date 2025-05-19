package es.codeurjc.helloword_vscode.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.hibernate.engine.jdbc.BlobProxy;
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
import es.codeurjc.helloword_vscode.ResourceNotFoundException;
import es.codeurjc.helloword_vscode.dto.AssociationDTO;
import es.codeurjc.helloword_vscode.dto.AssociationMemberTypeDTO;
import es.codeurjc.helloword_vscode.dto.MemberDTO;
import es.codeurjc.helloword_vscode.dto.MemberMapper;
import es.codeurjc.helloword_vscode.dto.MemberTypeMapper;
import es.codeurjc.helloword_vscode.dto.NewMemberRequestDTO;
import es.codeurjc.helloword_vscode.model.Association;
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

	@Autowired
	private MemberTypeMapper memberTypeMapper;

	@Autowired
	private MemberMapper memberMapper;

	/* Save user */
    public void save(Member member) {
		memberRepository.save(member);
	}

	/* Create User */
	public MemberDTO createMember(NewMemberRequestDTO memberDTO) {
		try {
			MemberDTO existingMember = findByNameDTO(memberDTO.name());
			if (existingMember != null) {
				throw new IllegalArgumentException("This username already exists");
			}
		} catch (ResourceNotFoundException e) {
			// If no member is found, continue with member creation
		}

		Member member = new Member(
			memberDTO.name(),
			memberDTO.surname(),
			passwordEncoder.encode(memberDTO.password()),
			"USER"
		);

		save(member);

		return toDTO(member);
	}



	/* Find user by their name */
	public Optional<Member> findByName(String name) {
		return memberRepository.findByName(name);
	}

	public MemberDTO findByNameDTO(String name) {
		return toDTO(
			memberRepository.findByName(name)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + name))
		);
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
	public void updateUserDTO(String currentUsername, NewMemberRequestDTO dto) {
		Member member = findByName(currentUsername).orElseThrow();

		if (!member.getName().equals(dto.name()) && findByName(dto.name()).isPresent()) {
			throw new IllegalArgumentException("This username already exists");
		}

		member.setName(dto.name());
		member.setSurname(dto.surname());

		if (dto.password() != null && !dto.password().isBlank()) {
			member.setPwd(passwordEncoder.encode(dto.password()));
		}

		save(member);
	}


	/* Delete user */
	public void delete(Member member) throws IOException {
		// Retrieve user by ID
		Member user = member;

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
	


	public List<AssociationMemberTypeDTO> getAssociationRoles(Member member) {
		return memberTypeMapper.toDTOs(member.getMemberTypes());
	}


	/* Find all the user's minutes */
	public List<Minute> getUserMinutes(Member member) {
		return member.getMinutes();
	}

	/* Convert entity to DTO */
	private MemberDTO toDTO(Member member) {
		return memberMapper.toDTO(member);
	}

	/* Converted an association set to DTOs */
	private Collection<MemberDTO> toDTOs(Collection<Member> members) {
		return memberMapper.toDTOs(members);
	}

	/* Converted a DTO to entity */
	private Member toDomain(MemberDTO memberDTO){
		return memberMapper.toDomain(memberDTO);
	}
}
