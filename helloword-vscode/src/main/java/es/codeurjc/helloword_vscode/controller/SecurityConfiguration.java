package es.codeurjc.helloword_vscode.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import es.codeurjc.helloword_vscode.service.UtilisateurEntityService;

// Marks this class as a Spring Security configuration
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // Injects the custom user details service
    @Autowired
    public UtilisateurEntityService userDetailService;

    // Bean for encoding passwords using BCrypt hashing algorithm
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Configures authentication using DAO authentication provider
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    // Defines security rules for handling HTTP requests
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authenticationProvider(authenticationProvider());
    
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public pages accessible to everyone
                .requestMatchers("/", "/css/**", "/members", "/search/**").permitAll()
                .requestMatchers("/association/**").permitAll()
                
                // Pages restricted to specific roles
                .requestMatchers("/profile").hasAnyRole("USER")
                .requestMatchers("/admin", "/association/create").hasAnyRole("ADMIN")
            )
            // Configures login settings
            .formLogin(formLogin -> formLogin
                .loginPage("/login")
                .failureUrl("/loginerror")
                .defaultSuccessUrl("/")
                .permitAll()
            )
            // Configures logout settings
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            // Configures session management
            .sessionManagement(session -> session
                .sessionFixation().newSession()  // Creates a new session after login
                .maximumSessions(1)  // Limits to one active session per user
                .expiredUrl("/login?expired")  // Redirects to login when session expires
            );
    
        // Disables CSRF protection (can be enabled if needed)
        http.csrf(csrf -> csrf.disable());
    
        return http.build();
    }
}
