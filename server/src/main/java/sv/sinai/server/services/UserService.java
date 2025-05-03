package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.repositories.IUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final IUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(IUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Get all users
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getDui(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()))
                .collect(Collectors.toList());
    }

    // Get all users by role
    public Optional<List<UserDTO>> getAllUsersByRole(Integer role) {
        return userRepository.findAllByRole(role)
                .map(users -> users.stream()
                        .map(user -> new UserDTO(
                                user.getId(),
                                user.getUsername(),
                                user.getName(),
                                user.getDui(),
                                user.getRole(),
                                user.getCreatedAt(),
                                user.getUpdatedAt()))
                        .collect(Collectors.toList()));
    }

    // Get user by id
    public Optional<UserDTO> getUserById(Integer id) {
        return userRepository.findById(id)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getDui(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()));
    }

    // Get user by username
    public Optional<UserDTO> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getDui(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()));
    }

    // Get user by dui
    public Optional<UserDTO> getUserByDui(String dui) {
        return userRepository.findByDui(dui)
                .map(user -> new UserDTO(
                        user.getId(),
                        user.getUsername(),
                        user.getName(),
                        user.getDui(),
                        user.getRole(),
                        user.getCreatedAt(),
                        user.getUpdatedAt()));
    }

    // Create user
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Update user
    public Optional<User> updateUser(Integer id, User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setUsername(userDetails.getUsername());
                    user.setName(userDetails.getName());
                    user.setDui(userDetails.getDui());
                    user.setRole(userDetails.getRole());
                    user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    return userRepository.save(user);
                });
    }

    // Delete user
    public void deleteUser(Integer id) {
        userRepository.deleteById(id);
    }
}
