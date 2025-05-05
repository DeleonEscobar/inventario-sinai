package sv.sinai.server.entities.dto.reports;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class MovementReportDTO {
    private Instant createdAt;
    private String notes;
    private String status;
    private String type;
    private String clientName;
    private String clientAddress;
    private String supervisorName;
    private String responsibleName;
    private List<BatchReportDTO> batches;
    private BigDecimal totalAmount;

    public MovementReportDTO() {
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getSupervisorName() {
        return supervisorName;
    }

    public void setSupervisorName(String supervisorName) {
        this.supervisorName = supervisorName;
    }

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public List<BatchReportDTO> getBatches() {
        return batches;
    }

    public void setBatches(List<BatchReportDTO> batches) {
        this.batches = batches;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }
}
