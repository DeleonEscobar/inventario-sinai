package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.beans.MovementRequest;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.services.MovementService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movements") // http://localhost:8080/api/movements
public class MovementController {
    private final MovementService movementService;

    @Autowired
    public MovementController(MovementService movementService) {
        this.movementService = movementService;
    }

    // Get all movements
    @GetMapping
    public List<MovementDTO> getAllMovements() {
        return movementService.getAllMovements();
    }

    // Get all movements by type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<MovementDTO>> getMovementsByType(@PathVariable String type) {
        Integer typeId = switch (type.toLowerCase()) {
            case "inbound" -> 1;
            case "shipping" -> 2;
            default -> throw new ResourceNotFoundException("Movement type '" + type + "' not found");
        };
        return ResponseEntity.ok(movementService.getMovementsByType(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("No movements found with type '" + type + "'")));
    }

    // Get all movements by client
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<MovementDTO>> getMovementsByClient(@PathVariable Integer clientId) {
        return ResponseEntity.ok(movementService.getMovementsByClient(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("No movements found for client with id '" + clientId + "'")));
    }

    // Get all movements by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<MovementDTO>> getMovementsByStatus(@PathVariable String status) {
        Integer statusId = switch (status.toLowerCase()) {
            case "requested" -> 1;
            case "completed" -> 2;
            case "cancelled" -> 3;
            default -> throw new ResourceNotFoundException("Movement status '" + status + "' not found");
        };
        return ResponseEntity.ok(movementService.getMovementsByStatus(statusId)
                .orElseThrow(() -> new ResourceNotFoundException("No movements found with status '" + status + "'")));
    }

    // Get all movements by responsible user
    @GetMapping("/responsible/{userId}")
    public ResponseEntity<List<MovementDTO>> getMovementsByResponsibleUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(movementService.getMovementsByResponsibleUser(userId)
                .orElseThrow(() -> new ResourceNotFoundException("No movements found for responsible user with id '" + userId + "'")));
    }

    // Get movements by id
    @GetMapping("/{id}")
    public ResponseEntity<MovementDTO> getMovementById(@PathVariable Integer id) {
        return ResponseEntity.ok(movementService.getMovementById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movement with id '" + id + "' not found")));
    }

    // Create movement
    @PostMapping
    public ResponseEntity<Movement> createMovement(@Valid @RequestBody MovementRequest movementRequest) {
        Movement movement = new Movement();
        movement.setNotes(movementRequest.getNotes());
        movement.setType(movementRequest.getType());
        movement.setStatus(movementRequest.getStatus());
        movement.setClient(movementRequest.getClient());
        movement.setResponsibleUser(movementRequest.getUser());
        movement.setCreatedAt(movementRequest.getCreatedAt());

        return ResponseEntity.ok(movementService.createMovement(movement, movementRequest.getBatches()));
    }

    // Update movement
    @PutMapping("/{id}")
    public ResponseEntity<Movement> updateMovement(@PathVariable Integer id, @Valid @RequestBody Movement movementDTO) {
        return ResponseEntity.ok(movementService.updateMovement(id, movementDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Movement with id '" + id + "' not found")));
    }

    // Delete movement
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteMovement(@PathVariable Integer id) {
        if (movementService.getMovementById(id).isEmpty()) {
            throw new ResourceNotFoundException("Movement with id '" + id + "' not found");
        }

        movementService.deleteMovement(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Movement and their batch relations deleted successfully");
        return ResponseEntity.ok(response);
    }
}
