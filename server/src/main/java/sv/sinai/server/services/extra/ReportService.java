package sv.sinai.server.services.extra;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.dto.reports.MovementReportDTO;
import sv.sinai.server.repositories.IMovementBatchRepository;
import sv.sinai.server.repositories.IMovementRepository;
import sv.sinai.server.services.BatchService;
import sv.sinai.server.services.MovementService;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final MovementService movementService;
    private final ResourceLoader resourceLoader;
    private final BatchService batchService;

    public ReportService(MovementService movementService, ResourceLoader resourceLoader, BatchService batchService) {
        this.movementService = movementService;
        this.resourceLoader = resourceLoader;
        this.batchService = batchService;
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

    // Generate a report for all batches
    public byte[] generateAllBatchesPdf() {
        try (InputStream jrxml = resourceLoader.getResource("classpath:reports/batches_report.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(jrxml);

            List<Batch> batches = batchService.getAllBatches();

            Map<String,Object> params = new HashMap<>();
            params.put("REPORT_TITLE", "Reporte de Todos los Lotes");
            params.put("FILTER_DESCRIPTION", "Mostrando todos los lotes registrados");
            params.put("LOGO", resourceLoader.getResource("classpath:img/logo.png").getInputStream());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(batches);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generating batch report", e);
        }
    }

    // Generate a report for a specific batch
    public byte[] generateBatchesByIdsPdf(List<Integer> batchesId) {
        try (InputStream jrxml = resourceLoader.getResource("classpath:reports/batches_report.jrxml").getInputStream()) {
            JasperReport report = JasperCompileManager.compileReport(jrxml);

            List<Batch> batches = batchService.getAllBatchesById(batchesId);

            Map<String,Object> params = new HashMap<>();
            params.put("REPORT_TITLE", "Reporte de Lotes Seleccionados");
            params.put("FILTER_DESCRIPTION", "Mostrando lotes seleccionados");
            params.put("LOGO", resourceLoader.getResource("classpath:img/logo.png").getInputStream());

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(batches);
            JasperPrint jasperPrint = JasperFillManager.fillReport(report, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            throw new RuntimeException("Error generating batch report", e);
        }
    }
}
