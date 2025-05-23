package sv.sinai.server.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.services.BatchService;
import sv.sinai.server.utils.exceptions.ResourceNotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/batches") // http://localhost:8080/api/batches
public class BatchController {
    private final BatchService batchService;

    @Autowired
    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    // Get all batches
    @GetMapping
    public List<Batch> getAllBatches() {
        return batchService.getAllBatches();
    }

    // Get all batches by product id
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Batch>> getAllBatchesByProductId(@PathVariable Integer productId) {
        List<Batch> batches = batchService.getAllBatchesByProductId(productId);

        if (batches.isEmpty()) {
            throw new ResourceNotFoundException("No batches found for product with id '" + productId + "'");
        }

        return ResponseEntity.ok(batches);
    }

    // Get batch by id
    @GetMapping("/{id}")
    public ResponseEntity<Batch> getBatchById(@PathVariable Integer id) {
        return ResponseEntity.ok(batchService.getBatchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Batch with id '" + id + "' not found")));
    }

    // Get batch by serial number
    @GetMapping("/serial/{serialNumber}")
    public ResponseEntity<Batch> getBatchBySerialNumber(@PathVariable String serialNumber) {
        return ResponseEntity.ok(batchService.getBatchBySerialNumber(serialNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Batch with serial number '" + serialNumber + "' not found")));
    }

    // Create batch
    @PostMapping
    public ResponseEntity<Batch> createBatch(@RequestBody Batch batch) {
        return ResponseEntity.ok(batchService.createBatch(batch));
    }

    // Duplicate batch
    @PostMapping("/{id}/duplicate")
    public ResponseEntity<Batch> duplicateBatch(@PathVariable Integer id) {
        return ResponseEntity.ok(batchService.duplicateBatch(id));
    }

    // Update batch
    @PutMapping("/{id}")
    public ResponseEntity<Batch> updateBatch(@PathVariable Integer id, @Valid @RequestBody Batch batchDetails) {
        return ResponseEntity.ok(batchService.updateBatch(id, batchDetails)
                .orElseThrow(() -> new ResourceNotFoundException("Batch with id '" + id + "' not found")));
    }

    // Delete batch
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteBatch(@PathVariable Integer id) {
        if (batchService.getBatchById(id).isEmpty()) {
            throw new ResourceNotFoundException("Batch with id '" + id + "' not found");
        }

        batchService.deleteBatch(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Batch deleted successfully");
        return ResponseEntity.ok(response);
    }

    // Available batches
    @GetMapping("/available")
    public ResponseEntity<List<Batch>> getAvailableBatches() {
        return ResponseEntity.ok(batchService.getAvailableBatches());
    }

}
