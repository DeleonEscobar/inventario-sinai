package sv.sinai.server.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @Column(name = "Id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @Column(name = "Name")
    private String name;

    @Size(max = 255)
    @Column(name = "DUI")
    private String dui;

    @Column(name = "Role")
    private Integer role;

    @Column(name = "CreatedAt")
    private Instant createdAt;

    @Column(name = "UpdatedAt")
    private Instant updatedAt;

    @Size(max = 50)
    @NotNull
    @Column(name = "Username", nullable = false, length = 50)
    private String username;

    @Size(max = 255)
    @NotNull
    @Column(name = "Password", nullable = false)
    private String password;

    // Getters and Setters ----------------------------------------------------------------------

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

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public Integer getRole() {
        return role;
    }

    public void setRole(Integer role) {
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    // Sobrecarga de métodos para la autenticación con UserDetails ------------------------------------------------
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role != null) {
            return switch (role) {
                case 1 -> List.of(new SimpleGrantedAuthority("MANAGER"));
                case 2 -> List.of(new SimpleGrantedAuthority("KEEPER"));
                default -> List.of(new SimpleGrantedAuthority("USER"));
            };
        }
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}