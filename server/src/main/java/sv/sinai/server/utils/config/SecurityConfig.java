package sv.sinai.server.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import sv.sinai.server.utils.jwt.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Inyectar las dependencias necesarias
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authProvider;

    // Constructor para inicializar las dependencias necesarias
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter, AuthenticationProvider authProvider) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.authProvider = authProvider;
    }

    // Filtro de cadena principal donde las rutas están protegidas y se define la configuración adicional para el servidor
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.addAllowedOriginPattern("*"); // Permitir todos los orígenes (dominios) para acceder a la API
                    config.addAllowedMethod("*"); // Permitir todos los métodos (GET, POST, PUT, DELETE, etc.)
                    config.addAllowedHeader("*"); // Permitir todos los encabezados (Authorization, Content-Type, etc.)
                    config.setAllowCredentials(true); // Permitir credenciales (cookies, autenticación, etc.)
                    return config;
                }))
                .authorizeHttpRequests((auth) -> auth
                        // Proteger todas las rutas de la API
                        .requestMatchers("/api/batches/**").authenticated()
                        .requestMatchers("/api/clients/**").authenticated()
                        .requestMatchers("/api/movements/**").authenticated()
                        .requestMatchers("/api/products/**").authenticated()
                        .requestMatchers("/api/reports/**").authenticated()
                        .requestMatchers("/api/users/**").authenticated()
                        .requestMatchers("/api/warehouses/**").authenticated()
                        .anyRequest().permitAll() // Permitir todas las demás solicitudes
                )
                .sessionManagement((sessionManager) -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Deshabilitar sesiones predeterminadas para usar JWT
                .authenticationProvider(authProvider) // Definir el proveedor de autenticación
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Agregar el filtro JWT
                .csrf(AbstractHttpConfigurer::disable); // Deshabilitar CSRF (CSRF = Cross-Site Request Forgery)
        return http.build(); // Construir y devolver la cadena de seguridad
    }
}
