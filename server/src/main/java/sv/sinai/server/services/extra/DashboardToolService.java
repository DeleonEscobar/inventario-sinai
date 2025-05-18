package sv.sinai.server.services.extra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.beans.DashboardTools;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.repositories.IBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.repositories.IProductRepository;
import sv.sinai.server.repositories.IUserRepository;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardToolService {
    private final IProductRepository productRepository;
    private final IMovementRepository movementRepository;
    private final IBatchRepository batchRepository;
    private final DTOConverter dtoConverter;
    private final IUserRepository userRepository;

    @Autowired
    public DashboardToolService(IProductRepository productRepository, IMovementRepository movementRepository, IBatchRepository batchRepository, DTOConverter dtoConverter, IUserRepository userRepository) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.batchRepository = batchRepository;
        this.dtoConverter = dtoConverter;
        this.userRepository = userRepository;
    }

    // Obtiene los datos para el dashboard del jefe
    public DashboardTools getDashboardBossTools(Integer bossId) {
        int totalProducts = (int) productRepository.count();
        int totalMovements = movementRepository.countMovementByStatus(1);
        List<Batch> soonToExpireBatches = batchRepository.findTop5ByExpirationDateBetweenOrderByExpirationDateAsc(
                Instant.now(),
                ZonedDateTime.now(ZoneId.systemDefault()).plusMonths(1).toInstant()
        );
        List<MovementDTO> recentMovements = convertMovementsToDTO(
                movementRepository.findTop5ByCreatedByUserIdAndStatusInOrderByUpdatedAtDesc(bossId, List.of(2, 3)));
        List<UserDTO> recentUsers = convertUsersToDTO(userRepository.findTop3ByRoleOrderByUpdatedAt(2));

        return new DashboardTools(
                totalProducts,
                totalMovements,
                soonToExpireBatches,
                recentMovements,
                recentUsers
        );
    }

    // Metodo auxuliar para convertir los movimientos a DTO
    private List<MovementDTO> convertMovementsToDTO(List<Movement> movements) {
        List<MovementDTO> dtos = new ArrayList<>();

        for (Movement movement : movements) {
            UserDTO responsibleUserDTO = dtoConverter.userToDTO(movement.getResponsibleUser());
            UserDTO createdByUserDTO = dtoConverter.userToDTO(movement.getCreatedByUser());

            dtos.add(dtoConverter.movementToDTO(movement, responsibleUserDTO, createdByUserDTO, null));
        }

        return dtos;
    }

    // Metodo auxiliar para convertir una lista de usuarios a DTO
    private List<UserDTO> convertUsersToDTO(List<User> users) {
        List<UserDTO> dtos = new ArrayList<>();

        for (User user : users) {
            dtos.add(dtoConverter.userToDTO(user));
        }

        return dtos;
    }
}
