package sv.sinai.server.services.extra;

import org.springframework.stereotype.Component;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.Warehouse;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.entities.dto.WarehouseDTO;

import java.util.List;

@Component
public class DTOConverter {
    public UserDTO userToDTO(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getName(),
                user.getDui(),
                user.getRole(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    public WarehouseDTO warehouseToDTO(Warehouse warehouse, UserDTO user) {
        return new WarehouseDTO(
                warehouse.getId(),
                warehouse.getName(),
                user,
                warehouse.getStatus(),
                warehouse.getLocation(),
                warehouse.getCreatedAt(),
                warehouse.getUpdatedAt()
        );
    }

    public MovementDTO movementToDTO(Movement movement, UserDTO responsibleUser, UserDTO createdByUser, List<Batch> batches) {
        return new MovementDTO(
                movement.getId(),
                movement.getNotes(),
                movement.getType(),
                movement.getStatus(),
                movement.getClient(),
                responsibleUser,
                createdByUser,
                movement.getCreatedAt(),
                movement.getUpdatedAt(),
                batches
        );
    }
}
