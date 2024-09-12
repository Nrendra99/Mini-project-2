package org.user.app.security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.user.app.config.CustomAuthenticationSuccessHandler;
import org.user.app.config.CustomUserDetailsService;




@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(authorizeRequests ->
        authorizeRequests
                .requestMatchers("/", "/patients/getform", "/patients/register", "/doctors/getform", "/doctors/register", "/login").permitAll()
                .requestMatchers("/medications/**").authenticated() // Allow any authenticated user
                .requestMatchers("/doctors/**").hasRole("DOCTOR")
                .requestMatchers("/patients/**", "/appointments/**").hasRole("PATIENT")
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/**").hasRole("ADMIN")  // Allow admin to access all URLs
            
                )
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .successHandler(new CustomAuthenticationSuccessHandler())
                                .permitAll()
                )
                .logout(logout ->
                        logout 
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")                       
                        .permitAll()
                );

        return http.build();
    }
    
}