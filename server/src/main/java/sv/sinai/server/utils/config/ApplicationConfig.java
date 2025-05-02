package sv.sinai.server.utils.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import sv.sinai.server.repositories.IUserRepository;

@Configuration
public class ApplicationConfig {

    @Autowired
    private IUserRepository userRepository;

    // Definir el bean de authentication manager que se utilizará en el proceso de autenticación
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager(); // Return default authentication manager
    }

    // Definir el bean de authentication provider que se utilizará en el proceso de autenticación
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Crear un nuevo DaoAuthenticationProvider que se utilizará como proveedor de autenticación
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService()); // Especificar los detalles del usuario que se utilizarán en el proceso de autenticación
        authenticationProvider.setPasswordEncoder(passwordEncoder()); // También especificar el codificador de contraseñas que se utilizará en el proceso de autenticación
        return authenticationProvider; // Retornar el proveedor de autenticación
    }

    // Definir los detalles del usuario que se utilizarán en el proceso de autenticación
    @Bean
    protected UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado")); // Retornar los datos del servicio del usuario encontrándolo por su username en el repositorio
    }

    // Definir el codificador de contraseñas que se utilizará en el proceso de autenticación
    @Bean
    protected PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Retornar un nuevo objeto BCryptPasswordEncoder que se utilizará como codificador de contraseñas
    }
}
