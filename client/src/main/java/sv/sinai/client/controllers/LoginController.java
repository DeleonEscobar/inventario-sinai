package sv.sinai.client.controllers;

import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sv.sinai.client.models.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    // Inyección de dependencias ===============================================
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    // Variables de entorno ====================================================
    @Value("${api.baseURL}")
    private String BASE_URL;

    // Métodos =================================================================
    @GetMapping("/login")
    public String loginForm(HttpSession session, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof User) {

            User user = (User) authentication.getPrincipal();
            // Si el usuario ya está autenticado, redirigir según su rol
            // Redirigir según el rol
            return switch (user.getRole().getDisplayName().toUpperCase()) {
                case "ADMIN" -> "redirect:/dashboard/admin/index";
                case "EMPLOYEE" -> "redirect:/dashboard/employee/index";
                default -> "redirect:/dashboard";
            };
        }

        // Si no hay sesión, mostrar la página de login
        return "auth/login";
    }

    /**
     * POST request para autenticar al usuario
     * @param username El nombre de usuario del usuario
     * @param password La contraseña del usuario
     * @param session  El objeto de sesión
     * @param model    El objeto de modelo
     * @return La URL de redirección basada en el rol del usuario
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        String loginUrl = BASE_URL + "/auth/login"; // La URL del endpoint de login

        // Establecer el encabezado de tipo de contenido a application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Crear el cuerpo de la solicitud con el nombre de usuario y la contraseña
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", username);
        requestBody.put("password", password);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(loginUrl, request, Map.class); // Enviar la solicitud POST
            Map<String, Object> responseBody = response.getBody(); // Obtener el cuerpo de la respuesta

            if (responseBody != null && responseBody.containsKey("token")) { // Revisar si el cuerpo de la respuesta contiene un token
                String token = (String) responseBody.get("token"); // Obtener el token del cuerpo de la respuesta
                Map<String, Object> userData = (Map<String, Object>) responseBody.get("user"); // Obtener los datos del usuario del cuerpo de la respuesta

                User user = objectMapper.convertValue(userData, User.class); // Convertir los datos del usuario a un objeto User

                // Guardar el token y el objeto de usuario en la sesión
                session.setAttribute("token", token);
                session.setAttribute("user", user);

                // Crear un token de autenticación con el objeto de usuario y el rol
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        Collections.singletonList(() -> "ACCESS_" + user.getRole().getDisplayName().toUpperCase())
                );

                // Establecer el contexto de seguridad con el token de autenticación
                SecurityContextHolder.getContext().setAuthentication(auth);

                // Redirigir al usuario basado en su rol
                switch (user.getRole().getDisplayName().toUpperCase()) {
                    case "ADMIN":
                        return "redirect:/dashboard/admin/index";
                    case "EMPLOYEE":
                        return "redirect:/dashboard/employee/index";
                    default:
                        return "redirect:/dashboard";
                }
            } else {
                // Si el cuerpo de la respuesta no contiene un token, mostrar un mensaje de error
                model.addAttribute("error", "Credenciales inválidas");
                return "auth/login";
            }
        } catch (HttpClientErrorException e) {
            logger.error("HTTP Client Error: {}", e.getMessage());
            logger.error("Status code: {}", e.getStatusCode());
            logger.error("Response body: {}", e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor, inténtelo de nuevo.");
            } else {
                model.addAttribute("error", "Ocurrió un error. Por favor, inténtelo de nuevo más tarde.");
            }
            return "auth/login";
        } catch (Exception e) {
            logger.error("Unexpected error during login: ", e);
            model.addAttribute("error", "Ocurrió un error inesperado. Por favor, inténtelo de nuevo más tarde.");
            e.printStackTrace();
            return "auth/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión
        session.invalidate();
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        return "redirect:/login?logout";
    }
}