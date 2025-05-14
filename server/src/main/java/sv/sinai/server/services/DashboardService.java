package sv.sinai.server.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.dto.DashboardDataDTO;
import sv.sinai.server.repositories.IBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.repositories.IProductRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {
    private final IBatchRepository batchRepository;
    private final IMovementRepository movementRepository;
    private final IProductRepository productRepository;

    @Autowired
    public DashboardService(IBatchRepository batchRepository, 
                          IMovementRepository movementRepository,
                          IProductRepository productRepository) {
        this.batchRepository = batchRepository;
        this.movementRepository = movementRepository;
        this.productRepository = productRepository;
    }

    public Map<String, Object> getAvailableProducts() {
        Long count = productRepository.count();
        return Map.of("count", count);
    }

    public Map<String, Object> getPendingMovements() {
        // Status 1 = Pendiente
        Long count = movementRepository.countByStatus(1);
        return Map.of("count", count);
    }

    public Map<String, Object> getRecentActivities() {
        // Obtener movimientos de los últimos 7 días
        Instant sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        List<Map<String, Object>> activities = movementRepository.findRecentActivities(sevenDaysAgo);
        return Map.of("activities", activities);
    }

    public Map<String, Object> getExpiringBatches() {
        // Obtener lotes que vencen en los próximos 30 días
        Instant thirtyDaysFromNow = Instant.now().plus(30, ChronoUnit.DAYS);
        List<Map<String, Object>> batches = batchRepository.findExpiringBatches(thirtyDaysFromNow);
        return Map.of("batches", batches);
    }
} 