package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Movement;
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

    // Add multiple batches to a single movement
    public boolean addMultipleBatchesToMovement(Integer movementId, List<Integer> batchIds) {
        try {
            for (Integer batchId : batchIds) {
                Movement movement = new Movement();
                movement.setId(movementId);

                Batch batch = new Batch();
                batch.setId(batchId);

                MovementBatch movementBatch = new MovementBatch();
                movementBatch.setMovement(movement);
                movementBatch.setBatch(batch);
                movementBatchRepository.save(movementBatch);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Update movement batch
    public Optional<MovementBatch> updateMovementBatch(Integer id, MovementBatch movementBatchDetails) {
        return movementBatchRepository.findById(id)
                .map(movementBatch -> {
                    movementBatch.setMovement(movementBatchDetails.getMovement());
                    movementBatch.setBatch(movementBatchDetails.getBatch());
                    return movementBatchRepository.save(movementBatch);
                });
    }

    // Delete movement batch
    public void deleteMovementBatch(Integer id) {
        movementBatchRepository.deleteById(id);
    }

    // Remove multiple batches from a single movement
    public boolean removeMultipleBatchesFromMovement(Integer movementId, List<Integer> batchIds) {
        try {
            for (Integer batchId : batchIds) {
                movementBatchRepository.deleteByMovementIdAndBatchId(movementId, batchId);
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
