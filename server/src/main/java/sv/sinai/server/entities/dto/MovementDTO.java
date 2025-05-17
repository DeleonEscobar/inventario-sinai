package sv.sinai.server.entities.dto;

import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Client;

import java.time.Instant;
import java.util.List;

public class MovementDTO {
    private Integer id;
    private String notes;
    private Integer type;
    private Integer status;
    private Client client;
    private UserDTO responsibleUser;
    private UserDTO createdByUser;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Batch> batches;

    public MovementDTO(Integer id, String notes, Integer type, Integer status, Client client, UserDTO responsibleUser, UserDTO createdByUser, Instant createdAt, Instant updatedAt, List<Batch> batches) {
        this.id = id;
        this.notes = notes;
        this.type = type;
        this.status = status;
        this.client = client;
        this.responsibleUser = responsibleUser;
        this.createdByUser = createdByUser;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.batches = batches;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public UserDTO getResponsibleUser() {
        return responsibleUser;
    }

    public void setResponsibleUser(UserDTO responsibleUser) {
        this.responsibleUser = responsibleUser;
    }

    public UserDTO getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(UserDTO createdByUser) {
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
}
