package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.Warehouse;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.entities.dto.WarehouseDTO;
import sv.sinai.server.services.UserService;
import sv.sinai.server.services.WarehouseService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/warehouses") // http://localhost:8080/api/warehouses
public class WarehouseController {
    private final WarehouseService warehouseService;
    private final UserService userService;

    @Autowired
    public WarehouseController(WarehouseService warehouseService, UserService userService) {
        this.warehouseService = warehouseService;
        this.userService = userService;
    }

    // Get all warehouses
    @GetMapping
    public List<WarehouseDTO> getAllWarehouses() {
        return warehouseService.getAllWarehouses();
    }

    // Get all warehouses by status
    @GetMapping("/status/{status}")
    public List<WarehouseDTO> getAllWarehousesByStatus(@PathVariable String status) {
        Integer warehouseId = switch(status.toLowerCase()) {
            case "active" -> 1;
            case "inactive" -> 0;
            default -> throw new ResourceNotFoundException("No warehouses found with type '" + status + "'");
        };
        return warehouseService.getAllByStatus(warehouseId);
    }

    // Get warehouse by id
    @GetMapping("/{id}")
    public ResponseEntity<WarehouseDTO> getWarehouseById(@PathVariable Integer id) {
        return ResponseEntity.ok(warehouseService.getWarehouseById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with id '" + id + "' not found")));
    }

    // Get warehouse by name
    @GetMapping("/name/{name}")
    public ResponseEntity<WarehouseDTO> getWarehouseByName(@PathVariable String name) {
        return ResponseEntity.ok(warehouseService.getWarehouseByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with name '" + name + "' not found")));
    }

    // Get warehouse by contact user
    @GetMapping("/contact/{userId}")
    public ResponseEntity<WarehouseDTO> getWarehouseByContactUserId(@PathVariable Integer userId) {
        return ResponseEntity.ok(warehouseService.getWarehouseByContactUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with contact user id '" + userId + "' not found")));
    }

    // Create warehouse
    @PostMapping
    public ResponseEntity<Warehouse> createWarehouse(@Valid @RequestBody Warehouse warehouse) {
        UserDTO contactUser = userService.getUserById(warehouse.getContactUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact user with id '" + warehouse.getContactUser().getId() + "' not found"));

        if (contactUser.getRole() != 1) {
            throw new ResourceNotFoundException("Contact user must be a manager");
        }
        return ResponseEntity.ok(warehouseService.createWarehouse(warehouse));
    }

    // Update warehouse
    @PutMapping("/{id}")
    public ResponseEntity<Warehouse> updateWarehouse(@PathVariable Integer id, @Valid @RequestBody Warehouse warehouseDetails) {
        UserDTO contactUser = userService.getUserById(warehouseDetails.getContactUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Contact user with id '" + warehouseDetails.getContactUser().getId() + "' not found"));

        if (contactUser.getRole() != 1) {
            throw new ResourceNotFoundException("Contact user must be a manager");
        }

        return ResponseEntity.ok(warehouseService.updateWarehouse(id, warehouseDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Warehouse with id '" + id + "' not found")));
    }

    // Delete warehouse
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteWarehouse(@PathVariable Integer id) {
        if (warehouseService.getWarehouseById(id).isEmpty()) {
            throw new ResourceNotFoundException("Warehouse with id '" + id + "' not found");
        }

        warehouseService.deleteWarehouse(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Warehouse deleted successfully");
        return ResponseEntity.ok(response);
    }
}
