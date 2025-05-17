package sv.sinai.client.controllers;

import jakarta.servlet.http.HttpSession;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sv.sinai.client.models.User;
import sv.sinai.client.models.Batch;
import sv.sinai.client.models.Product;

@Controller
@RequestMapping("/dashboard")
public class EmployeeDashboardController extends BaseController {
    
    private final RestTemplate restTemplate;
    
    @Value("${api.baseURL}")
    private String BASE_URL;
    
    public EmployeeDashboardController(RestTemplate restTemplate) {
        super(restTemplate);
        this.restTemplate = restTemplate;
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "employee");
    }

    // ======================================== EMPLOYEE DASHBOARD ========================================
    @GetMapping("/employee")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String redirectToDashboard() {
        return "redirect:/dashboard/employee/index";
    }
    
    @GetMapping("/employee/index")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String employeeDashboard(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Dashboard de Empleado");
        model.addAttribute("activePage", "dashboard");
        return "dashboard/employee/index";
    }

    // ======================================== EMPLOYEE MANEJO DE LOTE ========================================
    @GetMapping("/employee/batches")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String batchesManagement(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Registro de Lotes");
        model.addAttribute("activePage", "batches");
        
        // Obtener productos para el formulario de lotes
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        ResponseEntity<Product[]> productsResponse = restTemplate.exchange(
                BASE_URL + "/products",
                HttpMethod.GET,
                entity,
                Product[].class);
        
        model.addAttribute("products", productsResponse.getBody());
        return "dashboard/batches";
    }
    
    // API para obtener lotes registrados por el empleado
    @GetMapping("/employee/api/batches")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    @ResponseBody
    public Batch[] getEmployeeBatches(HttpSession session) {
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        User user = getSessionUser(session);
        
        ResponseEntity<Batch[]> response = restTemplate.exchange(
                BASE_URL + "/batches/employee/" + user.getId(),
                HttpMethod.GET,
                entity,
                Batch[].class);
        
        return response.getBody();
    }
    
    private String getTokenFromSession(HttpSession session) {
        return (String) session.getAttribute("token");
    }
}