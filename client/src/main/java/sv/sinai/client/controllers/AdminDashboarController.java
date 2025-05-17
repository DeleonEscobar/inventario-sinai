package sv.sinai.client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import sv.sinai.client.models.User;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class AdminDashboarController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    private final RestTemplate restTemplate;
    
    @Value("${api.baseURL}")
    private String BASE_URL;

    public AdminDashboarController(RestTemplate restTemplate) {
        super(restTemplate);
        this.restTemplate = restTemplate;
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }

    @GetMapping("/admin/index")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String adminDashboard(HttpSession session, Model model) {
        User user = getSessionUser(session);

        model.addAttribute("user", user);
        model.addAttribute("activePage", "dashboard");  
        
        return "dashboard/index";
    }

    // API Endpoint para datos del dashboard
    @GetMapping("/api/dashboard/boss")
    @ResponseBody
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public ResponseEntity<Map> getDashboardManagerTools(HttpSession session) {
        String token = getSessionToken(session);
        Long userId = getSessionUser(session).getId();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        return restTemplate.exchange(
                BASE_URL + "/dashboard/boss/" + userId,
                HttpMethod.GET,
                entity,
                Map.class
        );
    }


    @GetMapping("/api/token")
    @ResponseBody
    public Map<String, String> getToken(HttpSession session) {
        String token = getSessionToken(session);
        return Map.of("token", token);
    }
}
