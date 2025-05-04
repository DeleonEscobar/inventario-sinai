package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.*;
import sv.sinai.server.entities.dto.MovementDTO;
import sv.sinai.server.entities.dto.UserDTO;
import sv.sinai.server.repositories.IMovementBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.services.extra.DTOConverter;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovementService {
    private final IMovementRepository movementRepository;
    private final IMovementBatchRepository movementBatchRepository;
    private final DTOConverter dtoConverter;

    @Autowired
    public MovementService(IMovementRepository movementRepository, IMovementBatchRepository movementBatchRepository, DTOConverter dtoConverter) {
        this.movementRepository = movementRepository;
        this.movementBatchRepository = movementBatchRepository;
        this.dtoConverter = dtoConverter;
    }

    // Get all movements
    public List<MovementDTO> getAllMovements() {
        List<Movement> movements = movementRepository.findAll();
        return movements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // Get all movements by type
    public Optional<List<MovementDTO>> getMovementsByType(Integer type) {
        List<Movement> movements = movementRepository.findAllByType(type);
        return appendBatches(movements);
    }

    // Get all movements by client
    public Optional<List<MovementDTO>> getMovementsByClient(Integer clientId) {
        List<Movement> movements = movementRepository.findAllByClientId(clientId);
        return appendBatches(movements);
    }

    // Get all movements by status
    public Optional<List<MovementDTO>> getMovementsByStatus(Integer status) {
        List<Movement> movements = movementRepository.findAllByStatus(status);
        return appendBatches(movements);
    }

    // Get all movements by responsible user
    public Optional<List<MovementDTO>> getMovementsByResponsibleUser(Integer userId) {
        List<Movement> movements = movementRepository.findAllByResponsibleUserId(userId);
        return appendBatches(movements);
    }

    // Get movement by id
    public Optional<MovementDTO> getMovementById(Integer id) {
        return movementRepository.findById(id).map(this::convertToDTO);
    }

    // Create movement
    public Movement createMovement(Movement movement, List<Integer> batches) {
        Movement savedMovement = movementRepository.save(movement);

        // Crear el registro de lotes pertenecientes al movimiento
        for (Integer batchId : batches) {
            MovementBatch movementBatch = new MovementBatch();
            movementBatch.setMovement(savedMovement);

            Batch batch = new Batch();
            batch.setId(batchId);
            movementBatch.setBatch(batch);

            movementBatchRepository.save(movementBatch);
        }
        return savedMovement;
    }

    // Update movement
    public Optional<Movement> updateMovement(Integer id, Movement movementDetails) {
        return movementRepository.findById(id)
                .map(movement -> {
                    movement.setNotes(movementDetails.getNotes());
                    movement.setType(movementDetails.getType());
                    movement.setStatus(movementDetails.getStatus());
                    movement.setClient(movementDetails.getClient());
                    movement.setResponsibleUser(movementDetails.getResponsibleUser());
                    return movementRepository.save(movement);
                });
    }

    // Delete movement
    public void deleteMovement(Integer id) {
        // Primero eliminamos los lotes asociados al movimiento
        List<MovementBatch> movementBatches = movementBatchRepository.findByMovementId(id).orElseThrow();
        movementBatchRepository.deleteAll(movementBatches);
        // Luego eliminamos el movimiento
        movementRepository.deleteById(id);
    }

    // Metodos auxiliares para establecer las relaciones entre movimientos y lotes
    private Optional<List<MovementDTO>> appendBatches(List<Movement> movements) {
        List<MovementDTO> movementDTOs = movements.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return Optional.of(movementDTOs);
    }

    private MovementDTO convertToDTO(Movement movement) {
        List<MovementBatch> movementBatches = movementBatchRepository.findByMovementId(movement.getId()).orElseThrow();
        List<Batch> batches = movementBatches.stream()
                .map(MovementBatch::getBatch)
                .collect(Collectors.toList());

        UserDTO responsibleUserDTO = dtoConverter.userToDTO(movement.getResponsibleUser());
        UserDTO createdByUserDTO = dtoConverter.userToDTO(movement.getCreatedByUser());

        return dtoConverter.movementToDTO(movement, responsibleUserDTO, createdByUserDTO, batches);
    }
}
