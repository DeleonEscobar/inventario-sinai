package sv.sinai.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Movement {
    private Long id;
    private String notes;
    private Integer type;
    private Integer status;
    private Client client;
    private User responsibleUser;
    private User createdByUser;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Batch> batches;

    public Movement() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public User getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(User responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public User getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(User createdByUser) {
        this.createdByUser = createdByUser;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }

    // Accesors
    public String getStatusName() {
        return switch (status) {
            case 1 -> "Pendiente";
            case 2 -> "En proceso";
            case 3 -> "Completado";
            case 4 -> "Cancelado";

            default -> "Desconocido";
        };
    }

    public String getStatusColorString() {
        return switch (status) {
            case 1 -> "bg-yellow-100 text-yellow-800";
            case 2 -> "bg-blue-100 text-blue-800";
            case 3 -> "bg-green-100 text-green-800";
            case 4 -> "bg-red-100 text-red-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }
    public String getTypeName() {
        return switch (type) {
            case 1 -> "Pedido";
            case 2 -> "Traslado";

            case 3 -> "Envío a cliente";
            case 4 -> "Entregado a cliente";
            case 5 -> "Devolución de cliente";

            default -> "Desconocido";
        };
    }

    public String getTypeColorString() {
        return switch (type) {
            case 1 -> "bg-green-100 text-green-800";
            case 2 -> "bg-blue-100 text-blue-800";
            case 3 -> "bg-yellow-100 text-yellow-800";
            case 4 -> "bg-red-100 text-red-800";
            case 5 -> "bg-purple-100 text-purple-800";
            default -> "bg-gray-100 text-gray-800";
        };
    }
}
