package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.MovementBatch;
import sv.sinai.server.entities.beans.MovementRequest;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.services.BatchService;
import sv.sinai.server.services.MovementBatchService;
import sv.sinai.server.services.MovementService;
import sv.sinai.server.services.UserService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/movements") // http://localhost:8081/api/movements
public class MovementController {
    private final MovementService movementService;
    private final UserService userService;
    private final BatchService batchService;
    private final MovementBatchService movementBatchService;

    @Autowired
    public MovementController(MovementService movementService, UserService userService, BatchService batchService, MovementBatchService movementBatchService) {
        this.movementService = movementService;
        this.userService = userService;
        this.batchService = batchService;
        this.movementBatchService = movementBatchService;
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
        UserDTO responsibleUser = userService.getUserById(movementRequest.getResponsibleUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Responsible user with id '" + movementRequest.getResponsibleUser().getId() + "' not found"));
        UserDTO createdByUser = userService.getUserById(movementRequest.getCreatedByUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Created by user with id '" + movementRequest.getCreatedByUser().getId() + "' not found"));

        if (Objects.equals(responsibleUser.getId(), createdByUser.getId())) {
            throw new ResourceNotFoundException("Responsible user and created by user cannot be the same");
        }

        if (createdByUser.getRole() != 1 || responsibleUser.getRole() != 2) {
            throw new ResourceNotFoundException("Created by user must be a manager and responsible user must be a warehouse keeper");
        }

        Movement movement = new Movement();
        movement.setNotes(movementRequest.getNotes());
        movement.setType(movementRequest.getType());
        movement.setStatus(movementRequest.getStatus());
        movement.setClient(movementRequest.getClient());
        movement.setResponsibleUser(movementRequest.getResponsibleUser());
        movement.setCreatedByUser(movementRequest.getCreatedByUser());
        movement.setCreatedAt(movementRequest.getCreatedAt());

        // Allow empty batches list - warehouse staff can add them later
        List<Integer> batches = movementRequest.getBatches();
        if (batches == null) {
            batches = List.of();
        }
        
        return ResponseEntity.ok(movementService.createMovement(movement, batches));
    }

    // Update movement
    @PutMapping("/{id}")
    public ResponseEntity<Movement> updateMovement(@PathVariable Integer id, @Valid @RequestBody Movement movementDTO) {
        return ResponseEntity.ok(movementService.updateMovement(id, movementDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Movement with id '" + id + "' not found")));
    }

    // Assign a batch to a movement
    @PostMapping("/{id}/assign-batches")
    public boolean assignBatchToMovement(@PathVariable Integer id, @RequestBody List<Integer> batchIds) {
        if (movementService.getMovementById(id).isEmpty()) {
            throw new ResourceNotFoundException("Movement with id '" + id + "' not found");
        }

        return movementBatchService.addMultipleBatchesToMovement(id, batchIds);
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

    // Remove a batch from a movement
    @DeleteMapping("/{id}/remove-batches")
    public boolean removeBatchFromMovement(@PathVariable Integer id, @RequestBody List<Integer> batchIds) {
        if (movementService.getMovementById(id).isEmpty()) {
            throw new ResourceNotFoundException("Movement with id '" + id + "' not found");
        }

        return movementBatchService.removeMultipleBatchesFromMovement(id, batchIds);
    }
}
