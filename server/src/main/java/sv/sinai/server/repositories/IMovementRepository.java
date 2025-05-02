package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;

import java.util.List;
import java.util.Optional;

public interface IMovementRepository extends JpaRepository<Movement, Integer> {
    Optional<List<Movement>> findAllByType(Integer type);
    Optional<List<Movement>> findAllByClient(Client client);
    Optional<List<Movement>> findAllByStatus(Integer status);
    Optional<List<Movement>> findAllByResponsibleUser(User user);
}
