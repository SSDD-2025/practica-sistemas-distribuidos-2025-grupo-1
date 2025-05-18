package es.codeurjc.helloword_vscode.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import es.codeurjc.helloword_vscode.service.MemberService;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    // Service to retrieve user details
    @Autowired
    public MemberService userDetailService;

    
    /* Bean for password encoding */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}


    /* Bean for authentication provider */
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

		authProvider.setUserDetailsService(userDetailService);
		authProvider.setPasswordEncoder(passwordEncoder());

		return authProvider;
	}


    /* Bean for security filter chain */
	@Bean
    // Configure method with http object for security
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Set the authentication provider
        http.authenticationProvider(authenticationProvider());
    
        // Configure authorization rules
        http
            .authorizeHttpRequests(authorize -> authorize
                // Public pages
                .requestMatchers(
                    "/images/**",
                    "/search/**", 
                    "/members",
                    "/css/**",
                    "/",
                    "/association/*", 
                    "/user/**", 
                    "/profile/create", 
                    "/login/create",
                    "/association/*/image"
                ).permitAll()

                 // Pages accessible to users with role "USER"
                .requestMatchers(
                    "/profile", 
                    "/association/*/join",
                    "/profile/update",
                    "/profile/edit", 
                    "/profile/delete/confirm",
                    "/profile/delete", 
                    "/association/*/createMinute",
                    "/association/*/new_minute",
                    "/association/*/leave",
                    "/association/*/changeRole"
                ).hasAnyRole("USER")

                // Pages accessible to users with role "ADMIN"
                .requestMatchers(
                    "/association/create",
                    "/association/*/delete",
                    "/association/*/deleteImage",
                    "/editasso",
                    "/editasso/**",
                    "/createasso",
                    "/profile/*/delete",
                    "/minute/*/asso/*/edit",
                    "/editminute",
                    "/minute/*/asso/*/delete"
                ).hasAnyRole("ADMIN")

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Configure form login
            .formLogin(formLogin -> formLogin
                .loginPage("/login") // Custom login page
                .failureUrl("/loginerror") // Redirect on login failure
                .defaultSuccessUrl("/") // Redirect on login success
                .permitAll() // Allow all users to access the login page
            )

            // Configure logout
            .logout(logout -> logout
                .logoutUrl("/logout") // URL to trigger logout
                .logoutSuccessUrl("/") // Redirect on logout success
                .invalidateHttpSession(true) // Invalidate the HTTP session
                .deleteCookies("JSESSIONID") // Delete the JSESSIONID cookie
                .permitAll() // Allow all users to access the logout functionality
            )

            // Configure session management
            .sessionManagement(session -> session
                .sessionFixation().newSession()  // Create new session after login
                .maximumSessions(1)  // Authorize only one session per user
                .expiredUrl("/login?expired")  // Redirect on login if the session died
            );
    
        // Disable CSRF at the moment
        // http.csrf(csrf -> csrf.disable());
    
        // Build and return the security filter chain
        return http.build();
    }
    
}
