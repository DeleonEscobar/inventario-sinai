package sv.sinai.server.repositories;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Modifying
    @Transactional
    @Query("DELETE FROM MovementBatch mb WHERE mb.movement.id = :movementId AND mb.batch.id = :batchId")
    int deleteByMovementIdAndBatchId(@Param("movementId") Integer movementId, @Param("batchId") Integer batchId);

    @Query("""
        SELECT mb
        FROM MovementBatch mb
        JOIN FETCH mb.batch b
        JOIN FETCH b.product p
        WHERE mb.movement.id = :movementId
    """)
    Optional<List<MovementBatch>> findAllByMovementIdWithDetails(@Param("movementId") Integer movementId);

    @Modifying
    @Transactional
    @Query("DELETE FROM MovementBatch mb WHERE mb.batch.id = :batchId")
    void deleteByBatchId(@Param("batchId") Integer batchId);
}
