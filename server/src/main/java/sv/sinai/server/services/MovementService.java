package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;
import sv.sinai.server.repositories.IMovementRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MovementService {
    private final IMovementRepository movementRepository;

    @Autowired
    public MovementService(IMovementRepository movementRepository) {
        this.movementRepository = movementRepository;
    }

    // Get all movements
    public List<Movement> getAllMovements() {
        return movementRepository.findAll();
    }

    // Get all movements by type
    public Optional<List<Movement>> getMovementsByType(Integer type) {
        return movementRepository.findAllByType(type);
    }

    // Get all movements by client
    public Optional<List<Movement>> getMovementsByClient(Client client) {
        return movementRepository.findAllByClient(client);
    }

    // Get all movements by status
    public Optional<List<Movement>> getMovementsByStatus(Integer status) {
        return movementRepository.findAllByStatus(status);
    }

    // Get all movements by responsible user
    public Optional<List<Movement>> getMovementsByResponsibleUser(User user) {
        return movementRepository.findAllByResponsibleUser(user);
    }

    // Get movement by id
    public Optional<Movement> getMovementById(Integer id) {
        return movementRepository.findById(id);
    }

    // Create movement
    public Movement createMovement(Movement movement) {
        return movementRepository.save(movement);
    }

    // Update movement
    public Optional<Movement> updateMovement(Integer id, Movement movementDetails) {
        return movementRepository.findById(id)
                .map(movement -> {
                    movement.setNotes(movementDetails.getNotes());
                    movement.setType(movementDetails.getType());
                    movement.setStatus(movementDetails.getStatus());
                    movement.setClient(movementDetails.getClient());
                    movement.setResponsibleUser(movementDetails.getResponsibleUser());
                    movement.setUpdatedAt(Instant.now());
                    return movementRepository.save(movement);
                });
    }

    // Delete movement
    public void deleteMovement(Integer id) {
        movementRepository.deleteById(id);
    }
}
