package sv.sinai.client.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    private Long id;
    private String username;
    private String name;
    private String dui;
    
    @JsonProperty("role")
    private Role role;  

    // Constructor
    public User() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    // Propiedad para obtener el id del role
    @JsonProperty("role")
    public Integer getRoleId() {
        return role != null ? role.getId() : null;
    }

    // Propiedad para establecer el id del role
    @JsonProperty("role")
    public void setRoleId(Integer roleId) {
        this.role = Role.fromId(roleId);
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }
}