package sv.sinai.server.services.security;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.beans.AuthResponse;
import sv.sinai.server.entities.beans.LoginRequest;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.repositories.IUserRepository;

@Service
public class AuthService {
    // Inyectar dependencias necesarias
    private final IUserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // Constructor para inicializar las dependencias necesarias
    public AuthService(IUserRepository userRepository, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    // Metodo de servicio para autenticar al usuario y generar el token JWT
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        User userEntity = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtService.generateToken(userEntity);

        // Crear un objeto UserDTO a partir de la entidad User
        UserDTO user = new UserDTO(
                userEntity.getId(),
                userEntity.getUsername(),
                userEntity.getName(),
                userEntity.getDui(),
                userEntity.getRole(),
                userEntity.getCreatedAt(),
                userEntity.getUpdatedAt()
        );

        // Retornar un objeto AuthResponse con el token y el usuario
        return new AuthResponse(token, user);
    }
}
