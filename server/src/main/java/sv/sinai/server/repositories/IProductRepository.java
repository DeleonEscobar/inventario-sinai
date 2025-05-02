package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.sinai.server.entities.Product;

import java.util.Optional;

public interface IProductRepository extends JpaRepository<Product, Integer> {
    Optional<Product> findByName(String name);
}
