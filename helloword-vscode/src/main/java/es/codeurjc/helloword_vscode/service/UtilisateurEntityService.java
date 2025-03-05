package es.codeurjc.helloword_vscode.service;

// Importing necessary classes for Spring Security and repository management
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import es.codeurjc.helloword_vscode.entities.UtilisateurEntity;
import es.codeurjc.helloword_vscode.repository.UtilisateurEntityRepository;

@Service // Marks this class as a Spring service that provides user details for authentication
public class UtilisateurEntityService implements UserDetailsService {

    @Autowired // Automatically injects the UtilisateurEntityRepository to handle user data access
	private UtilisateurEntityRepository UtilisateursEntityRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Finds the user by username or throws an exception if the user doesn't exist
		UtilisateurEntity utilisateur = UtilisateursEntityRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		// Creates a list of roles for the user to use in authentication
		List<GrantedAuthority> roles = new ArrayList<>();
		for (String role : utilisateur.getRoles()) {
			// Adding "ROLE_" prefix to each role for Spring Security format
			roles.add(new SimpleGrantedAuthority("ROLE_" + role));
		}

		// Returns a new UserDetails object with the user's name, password, and roles
		return new org.springframework.security.core.userdetails.User(utilisateur.getName(), 
				utilisateur.getPwd(), roles);
	}
}
