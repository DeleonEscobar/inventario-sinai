package sv.sinai.server.entities.dto;

import sv.sinai.server.entities.Batch;

import java.time.Instant;
import java.util.List;

public class MovementDTO {
    private Integer id;
    private String name;
    private Integer type;
    private Integer status;
    private UserDTO user;
    private Instant createdAt;
    private Instant updatedAt;
    private List<Batch> batches;

    public MovementDTO(Integer id, String name, Integer type, Integer status, UserDTO user, Instant createdAt, Instant updatedAt, List<Batch> batches) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.user = user;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
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
