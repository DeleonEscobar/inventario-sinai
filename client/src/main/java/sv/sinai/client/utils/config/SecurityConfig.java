package sv.sinai.client.utils.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

import sv.sinai.client.utils.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // Inyectar el filtro de autenticación JWT
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Inicializar el filtro de autenticación JWT
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de seguridad
        http
                .csrf(AbstractHttpConfigurer::disable) // Deshabilitar CSRF para simplificar la configuración
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/","/login", "/css/**", "/js/**", "/img/**", "/main.css").permitAll() // Permitir acceso a las rutas de login y recursos
                        .requestMatchers("/dashboard/admin/**").hasAuthority("ACCESS_ADMIN")
                        .requestMatchers("/dashboard/products/**").hasAuthority("ACCESS_ADMIN")
                        .requestMatchers("/dashboard/batches/**").hasAuthority("ACCESS_ADMIN")
                        .anyRequest().authenticated() // Requerir autenticación para cualquier otra ruta
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .permitAll()
                        .logoutSuccessUrl("/login?logout") // Redirect to the login page after logout
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            if (request.getSession().getAttribute("user") != null) {
                                // Usuario tiene sesión pero no está autenticado con Spring Security
                                request.getSession().invalidate();
                            }
                            response.sendRedirect("/login");
                        })
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .expiredUrl("/login?expired")
                );

        return http.build();
    }
}