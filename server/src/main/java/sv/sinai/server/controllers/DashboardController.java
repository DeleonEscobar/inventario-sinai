package sv.sinai.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.sinai.server.services.DashboardService;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard") // http://localhost:8081/api/dashboard
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardService dashboardService;

    @Autowired
    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
        logger.info("DashboardController initialized");
    }

    // Obtener productos disponibles
    @GetMapping("/available-products")
    public ResponseEntity<Map<String, Object>> getAvailableProducts() {
        logger.info("GET request received for /api/dashboard/available-products");
        return ResponseEntity.ok(dashboardService.getAvailableProducts());
    }

    // Obtener movimientos pendientes
    @GetMapping("/pending-movements")
    public ResponseEntity<Map<String, Object>> getPendingMovements() {
        return ResponseEntity.ok(dashboardService.getPendingMovements());
    }

    // Obtener actividades recientes
    @GetMapping("/recent-activities")
    public ResponseEntity<Map<String, Object>> getRecentActivities() {
        return ResponseEntity.ok(dashboardService.getRecentActivities());
    }

    @GetMapping("/expiring-batches")
    public ResponseEntity<Map<String, Object>> getExpiringBatches() {
        return ResponseEntity.ok(dashboardService.getExpiringBatches());
    }

} 