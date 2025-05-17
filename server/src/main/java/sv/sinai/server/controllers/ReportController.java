package sv.sinai.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sv.sinai.server.services.extra.ReportService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports") // http://localhost:8080/api/reports
public class ReportController {
    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping(value = "/movement/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getMovementReportById(@PathVariable Integer id) {
        byte[] pdf = reportService.generateMovementPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=movement-" + id + ".pdf")
                .body(pdf);
    }

    @GetMapping(value = "/batches", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getAllBatchesReport() {
        byte[] pdf = reportService.generateAllBatchesPdf();
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=batches-" + timestamp + ".pdf")
                .body(pdf);
    }

    @GetMapping(value = "/batches/{ids}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> getBatchesByIdsReport(@PathVariable String ids) {
        List<Integer> batchIds = Arrays.stream(ids.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        byte[] pdf = reportService.generateBatchesByIdsPdf(batchIds);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss"));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=selected-batches-" + timestamp + ".pdf")
                .body(pdf);
    }
}
