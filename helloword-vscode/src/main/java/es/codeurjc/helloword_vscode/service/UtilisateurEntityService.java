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

import es.codeurjc.helloword_vscode.entities.Association;
import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

@Service
public class UtilisateurEntityService implements UserDetailsService {

    @Autowired
	private UtilisateurEntityRepository utilisateursEntityRepository;

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

}
