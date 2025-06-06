package sv.sinai.server.entities.beans;

import sv.sinai.server.entities.dto.UserDTO;

public class AuthResponse {
    private UserDTO user;
    private String token;

    public AuthResponse(String token, UserDTO user) {
        this.token = token;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}
