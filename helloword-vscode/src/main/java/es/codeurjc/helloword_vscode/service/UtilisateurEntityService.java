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

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.MemberType;
import es.codeurjc.helloword_vscode.entities.Minute;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.AssociationRepository;
import es.codeurjc.helloword_vscode.repository.MemberTypeRepository;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;
import es.codeurjc.helloword_vscode.repository.MinuteRepository;


@Service
public class UtilisateurEntityService implements UserDetailsService {

    @Autowired
	private UtilisateurEntityRepository utilisateursEntityRepository;

	@Autowired
	private MemberTypeRepository memberTypeRepository;

	@Autowired
	private MinuteRepository minuteRepository;


    public void save(UtilisateurEntity utilisateurEntity) {
		utilisateursEntityRepository.save(utilisateurEntity);
	}

	public Optional<UtilisateurEntity> findByName(String name) {
		return utilisateursEntityRepository.findByName(name);
	}

    @Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UtilisateurEntity utilisateur = utilisateursEntityRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : utilisateur.getRoles()) {
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		return new org.springframework.security.core.userdetails.User(utilisateur.getName(), 
				utilisateur.getPwd(), roles);

	}

	public Optional<UtilisateurEntity> findById(long id) {
		return utilisateursEntityRepository.findById(id);
	}

	public List<UtilisateurEntity> findAll() {
		return utilisateursEntityRepository.findAll();
	}

	@Transactional
	public void deleteById(long id) {
		Optional<UtilisateurEntity> optUser = utilisateursEntityRepository.findById(id);
		if (optUser.isPresent()) {
			UtilisateurEntity user = optUser.get();

			// 1. Supprimer les rôles dans les associations
			List<MemberType> memberTypes = memberTypeRepository.findByUtilisateurEntity(user);
			for (MemberType memberType : memberTypes) {
				memberTypeRepository.delete(memberType);
			}

			// 2. Supprimer la participation aux réunions
			List<Minute> minutes = minuteRepository.findAllByParticipantsContains(user);
			for (Minute minute : minutes) {
				minute.getParticipants().remove(user);
				minuteRepository.save(minute); // important !
			}

			// 3. Supprimer l'utilisateur
			utilisateursEntityRepository.delete(user);
		}
	}


}
