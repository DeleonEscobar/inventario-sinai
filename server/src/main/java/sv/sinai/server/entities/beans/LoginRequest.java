package sv.sinai.server.entities.beans;

import jakarta.validation.constraints.NotNull;

public class LoginRequest {
    @NotNull(message = "El nombre de usuario no puede ser nulo")
    private String username;
    @NotNull(message = "La contraseña no puede ser nula")
    private String password;

    public LoginRequest() {
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
}
