package sv.sinai.server.entities.dto;

import java.time.Instant;

public class WarehouseDTO {
    Integer id;
    String name;
    UserDTO contactUser;
    Integer status;
    String location;
    Instant createdAt;
    Instant updatedAt;

    public WarehouseDTO(Integer id, String name, UserDTO contactUser, Integer status, String location, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.name = name;
        this.contactUser = contactUser;
        this.status = status;
        this.location = location;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public UserDTO getContactUser() {
        return contactUser;
    }

    public void setContactUser(UserDTO contactUser) {
        this.contactUser = contactUser;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
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
}
