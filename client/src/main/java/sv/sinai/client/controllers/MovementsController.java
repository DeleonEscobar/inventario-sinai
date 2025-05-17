package sv.sinai.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;
import sv.sinai.client.models.User;
import sv.sinai.client.models.Movement;
import sv.sinai.client.models.Client;
import sv.sinai.client.models.Batch;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class MovementsController extends BaseController {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(MovementsController.class);

    @Value("${api.baseURL}")
    private String BASE_URL;

    public MovementsController(RestTemplate restTemplate) {
        super(restTemplate);
        this.restTemplate = restTemplate;
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }   

    // Lista de movimientos
    @GetMapping("/admin/movements")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String movements(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        // model.addAttribute("pageTitle", "Gestión de Movimientos");

        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Movement[]> response = restTemplate.exchange(
            BASE_URL + "/movements",
            HttpMethod.GET,
            entity,
            Movement[].class
        );
        model.addAttribute("movements", response.getBody());
        return "dashboard/movements/index";
    }
    
    // Crear nuevo movimiento (vista única)
    @GetMapping("/admin/movements/create")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String createMovement(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Nuevo Movimiento");
        
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Obtener clientes
        ResponseEntity<Client[]> clientsResponse = restTemplate.exchange(
            BASE_URL + "/clients",
            HttpMethod.GET,
            entity,
            Client[].class
        );
        model.addAttribute("clients", clientsResponse.getBody());
        
        // Obtener responsables (usuarios con rol 2)
        ResponseEntity<User[]> responsiblesResponse = restTemplate.exchange(
            BASE_URL + "/users?role=2",
            HttpMethod.GET,
            entity,
            User[].class
        );
        model.addAttribute("responsibles", responsiblesResponse.getBody());
        
        return "dashboard/movements/create";
    }
    
    // API para obtener lotes disponibles (para AJAX)
    @GetMapping("/admin/movements/api/batches")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    @ResponseBody
    public Batch[] getAvailableBatches(HttpSession session) {
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Get all available batches not associated with any movement
        ResponseEntity<Batch[]> response = restTemplate.exchange(
            BASE_URL + "/batches/available",
            HttpMethod.GET,
            entity,
            Batch[].class
        );
        
        return response.getBody();
    }
    
    // Procesar la creación del movimiento
    @PostMapping("/admin/movements/create")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String saveMovement(
        @RequestParam("clientId") Long clientId,
        @RequestParam(value = "batchIds", required = false) String batchIds,
        @RequestParam("responsibleId") Long responsibleId,
        @RequestParam("name") String name,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            User user = getSessionUser(session);
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            // Lotes son opcionales - el encargado de almacén puede añadirlos posteriormente
            List<Integer> batchesList = new ArrayList<>();
            if (batchIds != null && !batchIds.isEmpty()) {
                // Convertir string de IDs de lotes a lista de enteros
                String[] batchIdArray = batchIds.split(",");
                for (String batchId : batchIdArray) {
                    batchesList.add(Integer.parseInt(batchId));
                }
            }
            
            // Crear objetos para el request
            Client client = new Client();
            client.setId(clientId);
            
            User responsible = new User();
            responsible.setId(responsibleId);
            
            // Crear el objeto de request de movimiento
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("notes", name);  // Usar el nombre como notas
            requestMap.put("name", name);
            requestMap.put("type", 1);  // Tipo predeterminado
            requestMap.put("status", 1);  // Estado inicial (Pendiente)
            requestMap.put("client", client);
            requestMap.put("responsibleUser", responsible);
            requestMap.put("createdByUser", user);
            requestMap.put("batches", batchesList);
            requestMap.put("createdAt", Instant.now());
            
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);
            
            ResponseEntity<Movement> response = restTemplate.exchange(
                BASE_URL + "/movements",
                HttpMethod.POST,
                requestEntity,
                Movement.class
            );
            
            redirectAttributes.addFlashAttribute("success", "Movimiento creado correctamente");
            return "redirect:/dashboard/admin/movements";
            
        } catch (Exception e) {
            logger.error("Error al crear el movimiento", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements/create";
        }
    }
    
    // Ver detalle de un movimiento
    @GetMapping("/admin/movements/{id}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String viewMovement(
        @PathVariable("id") Long id,
        HttpSession session,
        Model model,
        RedirectAttributes redirectAttributes
    ) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Movement> response = restTemplate.exchange(
                BASE_URL + "/movements/" + id,
                HttpMethod.GET,
                entity,
                Movement.class
            );
            
            Movement movement = response.getBody();
            model.addAttribute("movement", movement);
            model.addAttribute("pageTitle", "Detalle de Movimiento: " + movement.getName());
            
            return "dashboard/movements/view";
            
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                redirectAttributes.addFlashAttribute("error", "El movimiento solicitado no existe");
            } else {
                redirectAttributes.addFlashAttribute("error", "Error al cargar el movimiento.");
            }
            return "redirect:/dashboard/admin/movements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Ocurrió un error inesperado. Por favor intente más tarde.");
            return "redirect:/dashboard/admin/movements";
        }
    }
    
    // Editar un movimiento
    @GetMapping("/admin/movements/{id}/edit")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String editMovement(
        @PathVariable("id") Long id,
        HttpSession session,
        Model model
    ) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Movement> response = restTemplate.exchange(
                BASE_URL + "/movements/" + id,
                HttpMethod.GET,
                entity,
                Movement.class
            );
            
            Movement movement = response.getBody();
            model.addAttribute("movement", movement);
            model.addAttribute("pageTitle", "Editar Movimiento: " + movement.getName());
            
            return "dashboard/movements/edit";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el movimiento: " + e.getMessage());
            return "dashboard/movements/index";
        }
    }
    
    // Actualizar un movimiento
    @PostMapping("/admin/movements/{id}/update")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String updateMovement(
        @PathVariable("id") Long id,
        @RequestParam("name") String name,
        @RequestParam("status") Integer status,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            // Obtener el movimiento actual
            ResponseEntity<Movement> getResponse = restTemplate.exchange(
                BASE_URL + "/movements/" + id,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Movement.class
            );
            
            Movement movement = getResponse.getBody();
            movement.setName(name);
            movement.setStatus(status);
            
            HttpEntity<Movement> requestEntity = new HttpEntity<>(movement, headers);
            
            ResponseEntity<Movement> response = restTemplate.exchange(
                BASE_URL + "/movements/" + id,
                HttpMethod.PUT,
                requestEntity,
                Movement.class
            );
            
            redirectAttributes.addFlashAttribute("success", "Movimiento actualizado correctamente");
            return "redirect:/dashboard/admin/movements/" + id;
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements/" + id;
        }
    }
    
    // Eliminar un movimiento
    @PostMapping("/admin/movements/{id}/delete")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String deleteMovement(
        @PathVariable("id") Long id,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            restTemplate.exchange(
                BASE_URL + "/movements/" + id,
                HttpMethod.DELETE,
                entity,
                Void.class
            );
            
            redirectAttributes.addFlashAttribute("success", "Movimiento eliminado correctamente");
            return "redirect:/dashboard/admin/movements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements";
        }
    }
    
    // Método auxiliar para obtener el token desde la sesión
    private String getTokenFromSession(HttpSession session) {
        Object token = session.getAttribute("token");
        return token != null ? token.toString() : null;
    }
}