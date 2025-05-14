package sv.sinai.server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface IMovementRepository extends JpaRepository<Movement, Integer> {
    List<Movement> findAllByType(Integer type);
    List<Movement> findAllByClientId(Integer clientId);
    List<Movement> findAllByStatus(Integer status);
    List<Movement> findAllByResponsibleUserId(Integer userId);
    Long countByStatus(Integer status);

    @Query(value = """
            SELECT new map(
                m.id as id,
                m.notes as description,
                m.createdAt as timestamp,
                m.type as type,
                m.status as status,
                CONCAT(u.name, ' (', u.username, ')') as responsibleUser
            )
            FROM Movement m
            JOIN User u ON m.responsibleUser.id = u.id
            WHERE m.createdAt >= :date
            ORDER BY m.createdAt DESC
            """)
    List<Map<String, Object>> findRecentActivities(@Param("date") Instant date);
}
