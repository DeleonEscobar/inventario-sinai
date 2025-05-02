package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.sinai.server.entities.Batch;

import java.util.List;
import java.util.Optional;

public interface IBatchRepository extends JpaRepository<Batch, Integer> {
    Optional<Batch> findBySerialNumber(String serialNumber);
    Optional<List<Batch>> findAllByProductId(Integer productId);
}
