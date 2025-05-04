package sv.sinai.server.entities.beans;

import jakarta.validation.constraints.NotNull;
import sv.sinai.server.entities.Batch;
import sv.sinai.server.entities.Client;
import sv.sinai.server.entities.User;
import sv.sinai.server.entities.dto.UserDTO;

import java.time.Instant;
import java.util.List;

public class MovementRequest {
    @NotNull
    private String notes;

    @NotNull
    private Integer type;

    @NotNull
    private Integer status;

    @NotNull
    private Client client;

    @NotNull
    private User responsibleUser;

    @NotNull
    private User createdByUser;

    private Instant createdAt;

    @NotNull
    private List<Integer> batches;

    public MovementRequest(String notes, Integer type, Integer status, Client client, User responsibleUser, User createdByUser, Instant createdAt, List<Integer> batches) {
        this.notes = notes;
        this.type = type;
        this.status = status;
        this.client = client;
        this.responsibleUser = responsibleUser;
        this.createdByUser = createdByUser;
        this.createdAt = createdAt;
        this.batches = batches;
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

    public List<Integer> getBatches() {
        return batches;
    }

    public void setBatches(List<Integer> batches) {
        this.batches = batches;
    }
}
