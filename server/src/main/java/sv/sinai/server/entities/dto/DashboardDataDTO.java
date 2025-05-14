package sv.sinai.server.entities.dto;

import java.time.Instant;
import java.util.List;

public class DashboardDataDTO {
    private static class ActivityDTO {
        private String description;
        private Instant timestamp;

        public ActivityDTO(String description, Instant timestamp) {
            this.description = description;
            this.timestamp = timestamp;
        }

        public String getDescription() {
            return description;
        }

        public Instant getTimestamp() {
            return timestamp;
        }
    }

    private static class BatchExpiringDTO {
        private String productName;
        private String batchNumber;
        private Instant expirationDate;
        private Integer quantity;

        public BatchExpiringDTO(String productName, String batchNumber, Instant expirationDate, Integer quantity) {
            this.productName = productName;
            this.batchNumber = batchNumber;
            this.expirationDate = expirationDate;
            this.quantity = quantity;
        }

        public String getProductName() {
            return productName;
        }

        public String getBatchNumber() {
            return batchNumber;
        }

        public Instant getExpirationDate() {
            return expirationDate;
        }

        public Integer getQuantity() {
            return quantity;
        }
    }

    private Integer availableProducts;
    private Integer pendingMovements;
    private List<ActivityDTO> recentActivities;
    private List<BatchExpiringDTO> expiringBatches;

    public DashboardDataDTO(Integer availableProducts, Integer pendingMovements, 
                          List<ActivityDTO> recentActivities, List<BatchExpiringDTO> expiringBatches) {
        this.availableProducts = availableProducts;
        this.pendingMovements = pendingMovements;
        this.recentActivities = recentActivities;
        this.expiringBatches = expiringBatches;
    }

    public Integer getAvailableProducts() {
        return availableProducts;
    }

    public Integer getPendingMovements() {
        return pendingMovements;
    }

    public List<ActivityDTO> getRecentActivities() {
        return recentActivities;
    }

    public List<BatchExpiringDTO> getExpiringBatches() {
        return expiringBatches;
    }
} 