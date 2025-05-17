package sv.sinai.server.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.sinai.server.entities.beans.DashboardTools;
import sv.sinai.server.services.DashboardService;
import sv.sinai.server.services.extra.DashboardToolService;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard") // http://localhost:8081/api/dashboard
public class DashboardController {
    private static final Logger logger = LoggerFactory.getLogger(DashboardController.class);
    private final DashboardToolService dashboardToolService;

    @Autowired
    public DashboardController(DashboardToolService dashboardToolService) {
        this.dashboardToolService = dashboardToolService;
    }

    // Obtener resumen del dashboard
    @GetMapping("/boss/{id}")
    public ResponseEntity<DashboardTools> getManagerDashboardTools(@PathVariable Integer id) {
        return ResponseEntity.ok(dashboardToolService.getDashboardBossTools(id));
    }

} 