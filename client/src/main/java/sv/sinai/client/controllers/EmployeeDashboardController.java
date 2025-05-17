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
import sv.sinai.client.models.Movement;
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
        model.addAttribute("activePage", "home");
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

    // ======================================== EMPLOYEE MANEJO DE MOVIMIENTOS ========================================
    @GetMapping("/employee/movements")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String movementsManagement(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Movimientos Asignados");
        model.addAttribute("activePage", "movements");
        
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Obtener movimientos asignados al empleado
        ResponseEntity<Movement[]> response = restTemplate.exchange(
                BASE_URL + "/movements/responsible/" + user.getId(),
                HttpMethod.GET,
                entity,
                Movement[].class);
        
        model.addAttribute("movements", response.getBody());
        return "dashboard/movements/index";
    }
    
    @GetMapping("/employee/movements/{id}")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String viewMovement(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Detalles de Movimiento");
        model.addAttribute("activePage", "movements");
        
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        try {
            ResponseEntity<Movement> response = restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.GET,
                    entity,
                    Movement.class);
            
            Movement movement = response.getBody();
            
            // Verificar que el movimiento esté asignado al usuario actual
            if (movement.getResponsibleUser().getId().equals(user.getId())) {
                model.addAttribute("movement", movement);
                return "dashboard/employee/movement_detail";
            } else {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver este movimiento");
                return "redirect:/dashboard/employee/movements";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el movimiento: " + e.getMessage());
            return "redirect:/dashboard/employee/movements";
        }
    }
    
    @PostMapping("/employee/movements/{id}/update-status")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String updateMovementStatus(
            @PathVariable("id") Long id,
            @RequestParam("status") Integer status,
            @RequestParam("notes") String notes,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        
        User user = getSessionUser(session);
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("Content-Type", "application/json");
        
        try {
            // Primero verificar que el movimiento está asignado al usuario
            HttpEntity<String> getEntity = new HttpEntity<>(headers);
            ResponseEntity<Movement> getResponse = restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.GET,
                    getEntity,
                    Movement.class);
            
            Movement movement = getResponse.getBody();
            
            if (!movement.getResponsibleUser().getId().equals(user.getId())) {
                redirectAttributes.addFlashAttribute("error", "No tienes permiso para actualizar este movimiento");
                return "redirect:/dashboard/employee/movements";
            }
            
            // Actualizar el estado del movimiento
            movement.setStatus(status);
            movement.setNotes(notes);
            
            HttpEntity<Movement> updateEntity = new HttpEntity<>(movement, headers);
            restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.PUT,
                    updateEntity,
                    Movement.class);
            
            redirectAttributes.addFlashAttribute("success", "Información del movimiento actualizada correctamente");
            return "redirect:/dashboard/employee/movements/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el movimiento: " + e.getMessage());
            return "redirect:/dashboard/employee/movements/" + id;
        }
    }
    
    private String getTokenFromSession(HttpSession session) {
        return (String) session.getAttribute("token");
    }
}