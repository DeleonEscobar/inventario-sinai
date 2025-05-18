package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface IUserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByUsername(String username);
    Optional<User> findByDui(String dui);
    Optional<List<User>> findAllByRole(Integer role);

    List<User> findTop3ByRoleOrderByUpdatedAt(Integer role);
}
