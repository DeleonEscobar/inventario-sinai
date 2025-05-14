package sv.sinai.client.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;

import sv.sinai.client.models.User;

public class UserService {
    @Value("${api.baseURL}")
    private String BASE_URL;

    private final RestTemplate restTemplate;

    public UserService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    // Buscar usuarios y agregar el JWT de la sesión
    public List<User> getUsers(String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token); // Establecer el JWT en el encabezado

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<User[]> response = restTemplate.exchange(
                BASE_URL + "/users", HttpMethod.GET, entity, User[].class
        );

        return Arrays.asList(response.getBody());
    }
}
