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
    
    // Crear nuevo movimiento - Paso 1: Cliente
    @GetMapping("/admin/movements/create")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String createMovementStep1(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Nuevo Movimiento - Seleccionar Cliente");
        model.addAttribute("step", 1);
        // Obtener clientes
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<Client[]> response = restTemplate.exchange(
            BASE_URL + "/clients",
            HttpMethod.GET,
            entity,
            Client[].class
        );
        model.addAttribute("clients", response.getBody());
        return "dashboard/movements/create";
    }
    
    // Crear nuevo movimiento - Paso 2: Lotes
    @GetMapping("/admin/movements/create/step2")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String createMovementStep2(
        @RequestParam("clientId") Long clientId,
        HttpSession session, 
        Model model
    ) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Nuevo Movimiento - Seleccionar Lotes");
        model.addAttribute("step", 2);
        model.addAttribute("clientId", clientId);
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Obtener lotes del cliente
        ResponseEntity<Batch[]> response = restTemplate.exchange(
            BASE_URL + "/batches?clientId=" + clientId,
            HttpMethod.GET,
            entity,
            Batch[].class
        );
        model.addAttribute("batches", response.getBody());
        return "dashboard/movements/create";
    }
    
    // Crear nuevo movimiento - Paso 3: Responsable
    @GetMapping("/admin/movements/create/step3")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String createMovementStep3(
        @RequestParam("clientId") Long clientId,
        @RequestParam("batchIds") String batchIds,
        HttpSession session, 
        Model model
    ) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Nuevo Movimiento - Seleccionar Responsable");
        model.addAttribute("step", 3);
        model.addAttribute("clientId", clientId);
        model.addAttribute("batchIds", batchIds);
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        // Obtener responsables (usuarios con rol 2)
        ResponseEntity<User[]> response = restTemplate.exchange(
            BASE_URL + "/users?role=2",
            HttpMethod.GET,
            entity,
            User[].class
        );
        model.addAttribute("responsibles", response.getBody());
        return "dashboard/movements/create";
    }
    
    // Crear nuevo movimiento - Paso 4: Confirmación
    @GetMapping("/admin/movements/create/confirm")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String createMovementConfirm(
        @RequestParam("clientId") Long clientId,
        @RequestParam("batchIds") String batchIds,
        @RequestParam("responsibleId") Long responsibleId,
        HttpSession session, 
        Model model
    ) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Nuevo Movimiento - Confirmar");
        model.addAttribute("step", 4);
        model.addAttribute("clientId", clientId);
        model.addAttribute("batchIds", batchIds);
        model.addAttribute("responsibleId", responsibleId);
        
        // Obtener datos para resumen
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        // Cliente
        ResponseEntity<Object> clientResponse = restTemplate.exchange(
            BASE_URL + "/clients/" + clientId,
            HttpMethod.GET,
            entity,
            Object.class
        );
        model.addAttribute("client", clientResponse.getBody());
        
        // Responsable
        ResponseEntity<Object> responsibleResponse = restTemplate.exchange(
            BASE_URL + "/users/" + responsibleId,
            HttpMethod.GET,
            entity,
            Object.class
        );
        model.addAttribute("responsible", responsibleResponse.getBody());
        
        // Lotes
        String[] batchIdArray = batchIds.split(",");
        Object[] selectedBatches = new Object[batchIdArray.length];
        
        for (int i = 0; i < batchIdArray.length; i++) {
            ResponseEntity<Object> batchResponse = restTemplate.exchange(
                BASE_URL + "/batches/" + batchIdArray[i],
                HttpMethod.GET,
                entity,
                Object.class
            );
            selectedBatches[i] = batchResponse.getBody();
        }
        
        model.addAttribute("selectedBatches", selectedBatches);
        
        return "dashboard/movements/create";
    }
    
    // Procesar la creación del movimiento
    @PostMapping("/admin/movements/create")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String saveMovement(
        @RequestParam("clientId") Long clientId,
        @RequestParam("batchIds") String batchIds,
        @RequestParam("responsibleId") Long responsibleId,
        HttpSession session,
        RedirectAttributes redirectAttributes
    ) {
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            // Crear el objeto de movimiento
            Movement movement = new Movement();
            movement.setName("Movimiento de inventario");
            movement.setType(1);
            movement.setStatus(1);
            
            // Agregar cliente, responsable y lotes
            // Nota: Este código es simplificado, deberías construir el objeto completo
            
            HttpEntity<Movement> requestEntity = new HttpEntity<>(movement, headers);
            
            ResponseEntity<Movement> response = restTemplate.exchange(
                BASE_URL + "/movements",
                HttpMethod.POST,
                requestEntity,
                Movement.class
            );
            
            redirectAttributes.addFlashAttribute("success", "Movimiento creado correctamente");
            return "redirect:/dashboard/admin/movements";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements";
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