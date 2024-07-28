package com.projetosapatos.demo.config;

import java.beans.Customizer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.sendRedirect("/login");
        };
    }

    @Bean
    public PasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, auth) -> {
            String role = auth.getAuthorities().stream()
                    .findFirst()
                    .map(grantedAuthority -> grantedAuthority.getAuthority())
                    .orElse("ROLE_USER");

            if (role.equals("ROLE_ADMIN")) {
                response.sendRedirect("/admin");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http
            .authorizeHttpRequests(auth -> {
                auth.requestMatchers("/").hasAnyRole("USER", "ADMIN");
                auth.requestMatchers("/index").hasAnyRole("USER", "ADMIN");
                auth.requestMatchers("/registrar").permitAll();
                auth.requestMatchers(HttpMethod.POST, "/login").permitAll();
                auth.requestMatchers("/admin").hasRole("ADMIN");
                auth.requestMatchers("/cadastroPage").hasRole("ADMIN");
                auth.requestMatchers("/salvar").hasRole("ADMIN");
                auth.requestMatchers("/editPage/{id}").hasRole("ADMIN");
                auth.requestMatchers("/deletar/{id}").hasRole("ADMIN");
                auth.requestMatchers("/adicionarCarrinho").hasRole("USER");
                auth.requestMatchers("/verCarrinho").hasRole("USER");
                auth.requestMatchers("/removerItem").hasRole("USER");
                auth.requestMatchers("/finalizarCompra").hasRole("USER");
                auth.anyRequest().permitAll();
            })
            .formLogin(login -> login
                .loginPage("/login")
                .permitAll()
                .successHandler(authenticationSuccessHandler())
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessHandler(logoutSuccessHandler())
                .permitAll()
            )
            .build();
    }
}
