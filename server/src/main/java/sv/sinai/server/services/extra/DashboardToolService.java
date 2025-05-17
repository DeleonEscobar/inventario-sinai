package sv.sinai.server.services.extra;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Movement;
import sv.sinai.server.entities.beans.DashboardTools;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.repositories.IBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.repositories.IProductRepository;

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

    @Autowired
    public DashboardToolService(IProductRepository productRepository, IMovementRepository movementRepository, IBatchRepository batchRepository, DTOConverter dtoConverter) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.batchRepository = batchRepository;
        this.dtoConverter = dtoConverter;
    }

    // Obtiene los datos para el dashboard del jefe
    public DashboardTools getDashboardBossTools(Integer bossId) {
        int totalProducts = (int) productRepository.count();
        int totalMovements = movementRepository.countMovementByStatus(1);
        List<Batch> soonToExpireBatches = batchRepository.findTop5ByExpirationDateBetweenOrderByExpirationDateAsc(
                Instant.now(),
                ZonedDateTime.now(ZoneId.systemDefault()).plusMonths(1).toInstant()
        );
        List<MovementDTO> recentMovements = convertToDTO(
                movementRepository.findTop5ByCreatedByUserIdAndStatusInOrderByUpdatedAtDesc(bossId, List.of(2, 3)));

        return new DashboardTools(
                totalProducts,
                totalMovements,
                soonToExpireBatches,
                recentMovements
        );
    }

    // Metodo auxuliar para convertir los movimientos a DTO
    private List<MovementDTO> convertToDTO(List<Movement> movements) {
        List<MovementDTO> dtos = new ArrayList<>();

        for (Movement movement : movements) {
            UserDTO responsibleUserDTO = dtoConverter.userToDTO(movement.getResponsibleUser());
            UserDTO createdByUserDTO = dtoConverter.userToDTO(movement.getCreatedByUser());

            dtos.add(dtoConverter.movementToDTO(movement, responsibleUserDTO, createdByUserDTO, null));
        }

        return dtos;
    }
}
