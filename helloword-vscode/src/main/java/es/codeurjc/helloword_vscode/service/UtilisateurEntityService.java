package es.codeurjc.helloword_vscode.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.codeurjc.helloword_vscode.model.MemberType;
import es.codeurjc.helloword_vscode.model.Minute;
import es.codeurjc.helloword_vscode.model.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;

/*
 * This service class provides methods to perform various operations on UtilisateurEntity entities,
 * such as saving, retrieving, and deleting users. It implements UserDetailsService to load user-specific 
 * data
*/
@Service
public class UtilisateurEntityService implements UserDetailsService {

    // Autowired repositories for database interactions //

    @Autowired
	private UtilisateurEntityRepository utilisateursEntityRepository;

	@Autowired
	private MemberTypeRepository memberTypeRepository;

	@Autowired
	private MinuteRepository minuteRepository;


	/* Save user */
    public void save(UtilisateurEntity utilisateurEntity) {
		utilisateursEntityRepository.save(utilisateurEntity);
	}


	/* Find user by their name */
	public Optional<UtilisateurEntity> findByName(String name) {
		return utilisateursEntityRepository.findByName(name);
	}


	/* Load data of user by finding them by their username */
    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Retrieve the user by username
		UtilisateurEntity utilisateur = utilisateursEntityRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Map the user's roles to granted authorities
		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : utilisateur.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		// Return a UserDetails object containing the user's data
		return new org.springframework.security.core.userdetails.User(utilisateur.getName(), 
				utilisateur.getPwd(), roles);
	}


	/* Find user by ID */
	public Optional<UtilisateurEntity> findById(long id) {
		return utilisateursEntityRepository.findById(id);
	}


	/* Find all users */
	public List<UtilisateurEntity> findAll() {
		return utilisateursEntityRepository.findAll();
	}


	/* Delete user by ID */
	@Transactional
	public void deleteById(long id) {
		// Retrieve user by ID
		Optional<UtilisateurEntity> optUser = utilisateursEntityRepository.findById(id);
		if (optUser.isPresent()) {
			UtilisateurEntity user = optUser.get();

			// 1. Delete member type in association
			List<MemberType> memberTypes = memberTypeRepository.findByUtilisateurEntity(user);
			for (MemberType memberType : memberTypes) {
				memberTypeRepository.delete(memberType);
			}

			// 2. Delete participation to meetings
			List<Minute> minutes = minuteRepository.findAllByParticipantsContains(user);
			for (Minute minute : minutes) {
				minute.getParticipants().remove(user);
				minuteRepository.save(minute); // important !
			}

			// 3. Delete user
			utilisateursEntityRepository.delete(user);
		}
	}
}
