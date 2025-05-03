package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.MovementBatch;
import sv.sinai.server.repositories.IMovementBatchRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class MovementBatchService {
    private final IMovementBatchRepository movementBatchRepository;

    @Autowired
    public MovementBatchService(IMovementBatchRepository movementBatchRepository) {
        this.movementBatchRepository = movementBatchRepository;
    }

    // Get all movement batches
    public List<MovementBatch> getAllMovementBatches() {
        return movementBatchRepository.findAll();
    }

    // Get movement batch by id
    public Optional<MovementBatch> getMovementBatchById(Integer id) {
        return movementBatchRepository.findById(id);
    }

    // Create movement batch
    public MovementBatch createMovementBatch(MovementBatch movementBatch) {
        return movementBatchRepository.save(movementBatch);
    }

    // Update movement batch
    public Optional<MovementBatch> updateMovementBatch(Integer id, MovementBatch movementBatchDetails) {
        return movementBatchRepository.findById(id)
                .map(movementBatch -> {
                    movementBatch.setMovement(movementBatchDetails.getMovement());
                    movementBatch.setBatch(movementBatchDetails.getBatch());
                    movementBatch.setUpdatedAt(Instant.now());
                    return movementBatchRepository.save(movementBatch);
                });
    }

    // Delete movement batch
    public void deleteMovementBatch(Integer id) {
        movementBatchRepository.deleteById(id);
    }
}
