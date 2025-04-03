package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    public UtilisateurEntityService userDetailService;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}

	@Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
    
        http
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/", "/css/**", "/images/**", "/association/*/image", "/association/*/createMinute").permitAll()
            .requestMatchers("/association/**", "/user/**", "/members", "/association/*/new_minute").permitAll()
            .requestMatchers("/profile").hasAnyRole("USER")
            .requestMatchers(
                "/admin",
                "/association/create",
                "/association/*/delete",
                "/association/*/deleteImage",
                "/editasso",
                "/editasso/**",
                "/createasso"
            ).hasRole("ADMIN")
            .anyRequest().authenticated()
        )    
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/loginerror")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .sessionFixation().newSession()  // Crée une nouvelle session après connexion
                .maximumSessions(1)  // Autorise seulement une session par utilisateur
                .expiredUrl("/login?expired")  // Redirige vers /login si la session expire
            );
    
        // Disable CSRF at the moment
        http.csrf(csrf -> csrf.disable());
    
        return http.build();
    }
    
}
