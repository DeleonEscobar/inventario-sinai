package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.Client;
import sv.sinai.server.services.ClientService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/clients") // http://localhost:8080/api/clients
public class ClientController {
    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Get all clients
    @GetMapping
    public List<Client> getAllClients() {
        return clientService.getAllClients();
    }
    // Get client by id
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(@PathVariable Integer id) {
        return ResponseEntity.ok(clientService.getClientById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id '" + id + "' not found")));
    }

    // Get client by name
    @GetMapping("/name/{name}")
    public ResponseEntity<Client> getClientByName(@PathVariable String name) {
        return ResponseEntity.ok(clientService.getClientByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Client with name '" + name + "' not found")));
    }

    // Create client
    @PostMapping
    public ResponseEntity<Client> createClient(@Valid @RequestBody Client client) {
        return ResponseEntity.ok(clientService.createClient(client));
    }

    // Update client
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(@PathVariable Integer id, @Valid @RequestBody Client clientDetails) {
        return ResponseEntity.ok(clientService.updateClient(id, clientDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Client with id '" + id + "' not found")));
    }

    // Delete client
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteClient(@PathVariable Integer id) {
        if (clientService.getClientById(id).isEmpty()) {
            throw new ResourceNotFoundException("Client with id '" + id + "' not found");
        }

        clientService.deleteClient(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Client deleted successfully");
        return ResponseEntity.ok(response);
    }
}
