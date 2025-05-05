package sv.sinai.server.entities.dto.reports;

import java.math.BigDecimal;
import java.time.Instant;

public class BatchReportDTO {

    private String productName;
    private Integer quantity;
    private BigDecimal price;
    private Instant expirationDate;
    private String serialNumber;
    private BigDecimal amount;

    public BatchReportDTO(String productName, Integer quantity, BigDecimal price, Instant expirationDate, String serialNumber, BigDecimal amount) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.expirationDate = expirationDate;
        this.serialNumber = serialNumber;
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Instant getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Instant expirationDate) {
        this.expirationDate = expirationDate;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
