package com.tesis.proyecto.proyectot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {  // Corregido de SecutiryConfig a SecurityConfig

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();  // Bean para PasswordEncoder
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilita CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/usuarios/login", "/usuarios/register", "/css/**", "/js/**").permitAll() // Rutas públicas
                        .anyRequest().authenticated() // Resto de rutas protegidas
                )
                .formLogin(form -> form
                        .loginPage("/usuarios/login")
                        .usernameParameter("correo")      // Indica el nombre del campo para el correo en el formulario
                        .passwordParameter("contrasennia") // Indica el nombre del campo para la contraseña
                        .defaultSuccessUrl("/proyectos", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/usuarios/login") // Redirige al login tras cerrar sesión
                        .permitAll()
                        .invalidateHttpSession(true)
                );

        return http.build(); // Construye el SecurityFilterChain
    }
}
