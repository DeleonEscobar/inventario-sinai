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
        return "dashboard/index";
    }

    // API Endpoints para datos del dashboard
    @GetMapping("/api/dashboard/available-products")
    @ResponseBody
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public ResponseEntity<Map> getAvailableProducts(HttpSession session) {
        String token = getSessionToken(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.exchange(
            BASE_URL + "/dashboard/available-products",
            HttpMethod.GET,
            entity,
            Map.class
        );
    }

    @GetMapping("/api/dashboard/pending-movements")
    @ResponseBody
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public ResponseEntity<Map> getPendingMovements(HttpSession session) {
        String token = getSessionToken(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.exchange(
            BASE_URL + "/dashboard/pending-movements",
            HttpMethod.GET,
            entity,
            Map.class
        );
    }

    @GetMapping("/api/dashboard/recent-activities")
    @ResponseBody
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public ResponseEntity<Map> getRecentActivities(HttpSession session) {
        String token = getSessionToken(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.exchange(
            BASE_URL + "/dashboard/recent-activities",
            HttpMethod.GET,
            entity,
            Map.class
        );
    }

    @GetMapping("/api/dashboard/expiring-batches")
    @ResponseBody
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public ResponseEntity<Map> getExpiringBatches(HttpSession session) {
        String token = getSessionToken(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        return restTemplate.exchange(
            BASE_URL + "/dashboard/expiring-batches",
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
