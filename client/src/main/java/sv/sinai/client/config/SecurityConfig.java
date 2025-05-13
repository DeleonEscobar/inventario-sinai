package sv.sinai.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import sv.sinai.client.utils.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de seguridad
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para simplificar la configuración
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/register").permitAll() // Permitir acceso a las rutas de login y registro
                        .anyRequest().authenticated() // Requerir autenticación para cualquier otra ruta
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL de logout
                        .logoutSuccessUrl("/login") // Redirigir a la página de login después de cerrar sesión
                );

        return http.build();
    }

}
