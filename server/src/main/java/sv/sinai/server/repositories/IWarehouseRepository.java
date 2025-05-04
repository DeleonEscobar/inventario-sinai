package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.Warehouse;

import java.util.List;
import java.util.Optional;

@Repository
public interface IWarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Optional<Warehouse> findByName(String name);
    Optional<Warehouse> findByContactUserId(Integer userId);
    List<Warehouse> findAllByStatus(Integer status);
}
