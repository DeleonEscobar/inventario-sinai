package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.repositories.IBatchRepository;
import sv.sinai.server.repositories.IMovementBatchRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BatchService {
    private final IBatchRepository batchRepository;
    private final IMovementBatchRepository movementBatchRepository;

    @Autowired
    public BatchService(IBatchRepository batchRepository, IMovementBatchRepository movementBatchRepository) {
        this.batchRepository = batchRepository;
        this.movementBatchRepository = movementBatchRepository;
    }

    // Get all batches
    public List<Batch> getAllBatches() {
        return batchRepository.findAll();
    }

    // Get all batches by id
    public List<Batch> getAllBatchesById(List<Integer> ids) {
        return batchRepository.findAllById(ids);
    }

    // Get batch by id
    public Optional<Batch> getBatchById(Integer id) {
        return batchRepository.findById(id);
    }

    // Get batch by serial number
    public Optional<Batch> getBatchBySerialNumber(String serialNumber) {
        return batchRepository.findBySerialNumber(serialNumber);
    }

    // Get all batches by product id

    public List<Batch> getAllBatchesByProductId(Integer productId) {
        return batchRepository.findAllByProductId(productId);
    }

    // Create batch
    public Batch createBatch(Batch batch) {
        return batchRepository.save(batch);
    }

    // Duplicate batch
    public Batch duplicateBatch(Integer id) {
        return batchRepository.findById(id)
                .map(batch -> {
                    Batch newBatch = new Batch();
                    newBatch.setSerialNumber(batch.getSerialNumber());
                    newBatch.setProduct(batch.getProduct());
                    newBatch.setAmount(batch.getAmount());
                    newBatch.setExpirationDate(batch.getExpirationDate());
                    newBatch.setPrice(batch.getPrice());
                    newBatch.setCreatedAt(null);
                    newBatch.setUpdatedAt(null);
                    return batchRepository.save(newBatch);
                })
                .orElse(null);
    }

    // Update batch
    public Optional<Batch> updateBatch(Integer id, Batch batchDetails) {
        return batchRepository.findById(id)
                .map(batch -> {
                    batch.setSerialNumber(batchDetails.getSerialNumber());
                    batch.setProduct(batchDetails.getProduct());
                    batch.setAmount(batchDetails.getAmount());
                    batch.setExpirationDate(batchDetails.getExpirationDate());
                    batch.setSerialNumber(batchDetails.getSerialNumber());
                    batch.setPrice(batchDetails.getPrice());
                    return batchRepository.save(batch);
                });
    }

    // Delete batch
    public void deleteBatch(Integer id) {
        // Eliminar las relaciones de movimiento-lote
        movementBatchRepository.deleteByBatchId(id);

        // Eliminar el lote
        batchRepository.deleteById(id);
    }

    // Get available batches
    public List<Batch> getAvailableBatches() {
        return batchRepository.findAllByMovementIdIsNull();
    }
}
