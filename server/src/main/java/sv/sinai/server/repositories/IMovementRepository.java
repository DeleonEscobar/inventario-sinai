package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface IMovementRepository extends JpaRepository<Movement, Integer> {
    List<Movement> findAllByType(Integer type);
    List<Movement> findAllByClientId(Integer clientId);
    List<Movement> findAllByStatus(Integer status);
    List<Movement> findAllByResponsibleUserId(Integer userId);
}
