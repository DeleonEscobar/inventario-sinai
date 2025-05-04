package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.MovementBatch;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMovementBatchRepository extends JpaRepository<MovementBatch, Integer> {
    Optional<List<MovementBatch>> findAllByMovementId(Integer movementId);
    Optional<List<MovementBatch>> findAllByBatchId(Integer batchId);
    Optional<MovementBatch> findByBatchId(Integer batchId);
    Optional<List<MovementBatch>> findByMovementId(Integer movementId);
    Optional<List<MovementBatch>> findAllByBatchIdAndMovementId(Integer batchId, Integer movementId);
}
