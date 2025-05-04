package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.services.UserService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Get all users
    @GetMapping
    public List<UserDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    // Get all users by role
    @GetMapping("/role/{role}")
    public List<UserDTO> getAllUsersByRole(@PathVariable String role) {
        Integer roleId = switch (role.toLowerCase()) {
            case "manager", "managers" -> 1;
            case "keeper", "keepers" -> 2;
            default -> throw new ResourceNotFoundException("Role '" + role + "' not found");
        };

        return userService.getAllUsersByRole(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("No users found for role '" + role + "'"));
    }

    // Get user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Integer id) {
        return ResponseEntity.ok(userService.getUserById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id '" + id + "' not found")));
    }

    // Get user by username
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.getUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username '" + username + "' not found")));
    }

    // Get user by dui
    @GetMapping("/dui/{dui}")
    public ResponseEntity<UserDTO> getUserByDui(@PathVariable String dui) {
        return ResponseEntity.ok(userService.getUserByDui(dui)
                .orElseThrow(() -> new ResourceNotFoundException("User with dui '" + dui + "' not found")));
    }

    // Create user
    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    // Update user
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer id, @Valid @RequestBody User userDetails) {
        return ResponseEntity.ok(userService.updateUser(id, userDetails)
                .orElseThrow(() -> new ResourceNotFoundException("User with id '" + id + "' not found")));
    }

    // Delete user
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Integer id) {
        if (userService.getUserById(id).isEmpty()) {
            throw new ResourceNotFoundException("User with id '" + id + "' not found");
        }

        userService.deleteUser(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "User deleted successfully");
        return ResponseEntity.ok(response);
    }
}
