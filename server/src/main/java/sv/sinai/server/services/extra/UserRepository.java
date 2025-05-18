package sv.sinai.server.services.extra;

import org.springframework.data.repository.Repository;
import sv.sinai.server.entities.User;

interface UserRepository extends Repository<User, Integer> {
}
