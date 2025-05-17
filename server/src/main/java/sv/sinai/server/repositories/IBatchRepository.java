package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.Batch;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface IBatchRepository extends JpaRepository<Batch, Integer> {
    Optional<Batch> findBySerialNumber(String serialNumber);
    List<Batch> findAllByProductId(Integer productId);

    List<Batch> findTop5ByExpirationDateBetweenOrderByExpirationDateAsc(Instant start, Instant end);
}