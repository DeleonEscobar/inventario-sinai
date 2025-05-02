package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.Client;

import java.util.Optional;

@Repository
public interface IClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByName(String name);
}
