package sv.sinai.server.services.extra;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.dto.reports.MovementReportDTO;
import sv.sinai.server.repositories.IMovementBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.services.MovementService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {
    private final MovementService movementService;
    private final ResourceLoader resourceLoader;

    public ReportService(MovementService movementService, ResourceLoader resourceLoader) {
        this.movementService = movementService;
        this.resourceLoader = resourceLoader;
    }

    // Generate a report for a specific movement
    public byte[] generateMovementPdf(Integer movementId) {
        try (InputStream jrxml = resourceLoader.getResource("classpath:reports/movements_report.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(jrxml);

            MovementReportDTO mv = movementService.getMovementReportById(movementId);
            Map<String,Object> params = new HashMap<>();
            params.put("CREATED_AT", mv.getCreatedAt());
            params.put("STATUS", mv.getStatus());
            params.put("NOTES", mv.getNotes());
            params.put("TYPE", mv.getType());
            params.put("CLIENT", mv.getClientName());
            params.put("CLIENT_ADDRESS", mv.getClientAddress());
            params.put("SUPERVISOR", mv.getSupervisorName());
            params.put("RESPONSIBLE", mv.getResponsibleName());
            params.put("TOTAL_AMOUNT", mv.getTotalAmount());
            params.put("LOGO", resourceLoader.getResource("classpath:img/logo.png").getInputStream());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(mv.getBatches());
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generating report", e);
        }
    }
}
