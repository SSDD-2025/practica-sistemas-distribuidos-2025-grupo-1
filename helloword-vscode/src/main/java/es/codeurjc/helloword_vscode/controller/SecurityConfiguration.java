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
                // public page
                .requestMatchers(
                    "/images/**",
                    "/search/**", 
                    "/members",
                    "/css/**",
                    "/",
                    "/association/*", 
                    "/user/*", 
                    "/new_minute.html", 
                    "/profile/create", 
                    "/association/*/image",
                    "/association/*/new_minute",
                    "/login/create",
                    "/login.html"
                    ).permitAll()

                .requestMatchers("/profile", "/profile/edit", "/edit_profile.html", "/profile/delete", "/association/*/createMinute").hasAnyRole("USER")
                .requestMatchers("/profile/delete", "/confirm_delete.html", "/profile/edit", "/edit_profile.html", "/association/*/createMinute", "/profile/delete/confirm").authenticated()
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
                .sessionFixation().newSession()  // Create new session after login
                .maximumSessions(1)  // Authorize only one session per user
                .expiredUrl("/login?expired")  // Redirect on login if the session died
            );
    
        // Disable CSRF at the moment
        http.csrf(csrf -> csrf.disable());
    
        return http.build();
    }
    
}
