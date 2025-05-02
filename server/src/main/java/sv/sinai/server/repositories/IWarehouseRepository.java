package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.Warehouse;

import java.util.List;
import java.util.Optional;

public interface IWarehouseRepository extends JpaRepository<Warehouse, Integer> {
    Optional<Warehouse> findByName(String name);
    Optional<List<Warehouse>> findAllByContactUser(User user);
    Optional<List<Warehouse>> findAllByStatus(Integer status);
}
