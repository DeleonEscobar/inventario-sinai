package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.sinai.server.entities.MovementBatch;

import java.util.List;
import java.util.Optional;

public interface IMovementBatchRepository extends JpaRepository<MovementBatch, Integer> {
    Optional<List<MovementBatch>> findAllByMovementId(Integer movementId);
    Optional<List<MovementBatch>> findAllByBatchId(Integer batchId);
    Optional<MovementBatch> findByBatchId(Integer batchId);
    Optional<MovementBatch> findByMovementId(Integer movementId);
    Optional<List<MovementBatch>> findAllByBatchIdAndMovementId(Integer batchId, Integer movementId);
}
