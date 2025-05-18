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

    @GetMapping({"/admin/movements", "/employee/movements"})
    @PreAuthorize("hasAnyAuthority('ACCESS_ADMIN', 'ACCESS_EMPLOYEE_DASHBOARD')")
    public String movements(HttpSession session, Model model) {
        User user = getSessionUser(session);
        boolean isAdmin = user.getRoleId() == 1;
        
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", isAdmin ? "Gestión de Movimientos" : "Movimientos Asignados");
        model.addAttribute("activePage", "movements");
        
        if (!isAdmin) {
            model.addAttribute("route", "employee");
        }

        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        
        String url = isAdmin ? 
            BASE_URL + "/movements" :
            BASE_URL + "/movements/responsible/" + user.getId();
            
        ResponseEntity<Movement[]> response = restTemplate.exchange(
                url,
                HttpMethod.GET, 
                entity,
                Movement[].class);

        model.addAttribute("movements", response.getBody());
        return "dashboard/movements/index";
    }
    
    // ======================================== MOVIMIENTOS EMPLEADOS ========================================
    @GetMapping("/employee/movements/{id}")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String viewEmployeeMovement(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Detalles de Movimiento");
        model.addAttribute("activePage", "movements");
        model.addAttribute("route", "employee");
        
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
    public String updateEmployeeMovementStatus(
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
                Client[].class);
        model.addAttribute("clients", clientsResponse.getBody());

        // Obtener responsables (usuarios con rol 2)
        ResponseEntity<User[]> responsiblesResponse = restTemplate.exchange(
                BASE_URL + "/users/role/keeper",
                HttpMethod.GET,
                entity,
                User[].class);
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
                Batch[].class);

        return response.getBody();
    }

    // API para obtener lotes disponibles (para AJAX)
    @GetMapping("/employee/movements/api/batches")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    @ResponseBody
    public Batch[] getAvailableBatchesForEmployees(HttpSession session) {
        String token = getTokenFromSession(session);
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Get all available batches not associated with any movement
        ResponseEntity<Batch[]> response = restTemplate.exchange(
                BASE_URL + "/batches/available",
                HttpMethod.GET,
                entity,
                Batch[].class);

        return response.getBody();
    }

    // Procesar la creación del movimiento
    @PostMapping("/admin/movements/create")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String saveMovement(
            @RequestParam(value = "clientId", required = false) Long clientId,
            @RequestParam(value = "batchIds", required = false) String batchIds,
            @RequestParam("responsibleId") Long responsibleId,
            @RequestParam("notes") String notes,
            @RequestParam("type") String type,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
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

            // Crear el objeto de request de movimiento
            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("notes", notes);
            requestMap.put("type", type.equals("traslado") ? 2 : 1); // 1 = pedido, 2 = traslado
            requestMap.put("status", 1); // Estado inicial (Pendiente)

            // Configurar cliente solo cuando sea necesario (pedido) o esté presente
            if (clientId != null) {
                Client client = new Client();
                client.setId(clientId);
                requestMap.put("client", client);
            } else if (type.equals("pedido")) {
                throw new IllegalArgumentException("El tipo de movimiento 'pedido' requiere un cliente");
            }

            User responsible = new User();
            responsible.setId(responsibleId);

            requestMap.put("responsibleUser", responsible);
            requestMap.put("createdByUser", user);
            requestMap.put("batches", batchesList);
            requestMap.put("createdAt", Instant.now());

            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(requestMap, headers);

            ResponseEntity<Movement> response = restTemplate.exchange(
                    BASE_URL + "/movements",
                    HttpMethod.POST,
                    requestEntity,
                    Movement.class);

            redirectAttributes.addFlashAttribute("success", "Movimiento creado correctamente");
            return "redirect:/dashboard/admin/movements";

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                redirectAttributes.addFlashAttribute("error", "Error al crear el movimiento: " + e.getMessage());
                logger.error("Error al crear el movimiento", e);
                return "redirect:/dashboard/admin/movements/create";
            }
        } catch (Exception e) {
            logger.error("Error al crear el movimiento", e);
            redirectAttributes.addFlashAttribute("error", "Error al crear el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements/create";
        }

        return "redirect:/dashboard/admin/movements/create";
    }

    // Ver detalle de un movimiento
    @GetMapping("/admin/movements/{id}")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String viewMovement(
            @PathVariable("id") Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {
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
                    Movement.class);

            Movement movement = response.getBody();
            model.addAttribute("movement", movement);
            model.addAttribute("pageTitle", "Detalle de Movimiento: " + movement.getNotes());

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
            Model model) {
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
                    Movement.class);

            Movement movement = response.getBody();
            model.addAttribute("movement", movement);
            model.addAttribute("pageTitle", "Editar Movimiento: " + movement.getNotes());

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
            @RequestParam("notes") String notes,
            @RequestParam("status") Integer status,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
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
                    Movement.class);

            Movement movement = getResponse.getBody();
            movement.setNotes(notes);
            movement.setStatus(status);

            HttpEntity<Movement> requestEntity = new HttpEntity<>(movement, headers);

            ResponseEntity<Movement> response = restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.PUT,
                    requestEntity,
                    Movement.class);

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
            RedirectAttributes redirectAttributes) {
        try {
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.DELETE,
                    entity,
                    Void.class);

            redirectAttributes.addFlashAttribute("success", "Movimiento eliminado correctamente");
            return "redirect:/dashboard/admin/movements";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el movimiento: " + e.getMessage());
            return "redirect:/dashboard/admin/movements";
        }
    }

    // Actualizar lotes de un movimiento (API para AJAX)
    @PostMapping("/employee/movements/{id}/update-batches")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    @ResponseBody
    public Map<String, Object> updateMovementBatches(
            @PathVariable("id") Long id,
            @RequestParam("batchIds") String batchIds,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            User user = getSessionUser(session);
            String token = getTokenFromSession(session);
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + token);
            headers.set("Content-Type", "application/json");
            
            // Primero verificar que el movimiento está asignado al usuario
            HttpEntity<String> getEntity = new HttpEntity<>(headers);
            ResponseEntity<Movement> getResponse = restTemplate.exchange(
                    BASE_URL + "/movements/" + id,
                    HttpMethod.GET,
                    getEntity,
                    Movement.class);
            
            Movement movement = getResponse.getBody();
            
            if (!movement.getResponsibleUser().getId().equals(user.getId())) {
                response.put("success", false);
                response.put("message", "No tienes permiso para actualizar este movimiento");
                return response;
            }
            
            // Convertir string de IDs de lotes a lista de enteros
            List<Integer> batchesList = new ArrayList<>();
            if (batchIds != null && !batchIds.isEmpty()) {
                String[] batchIdArray = batchIds.split(",");
                for (String batchId : batchIdArray) {
                    batchesList.add(Integer.parseInt(batchId));
                }
            }
            
            // Enviar la actualización de lotes al API
            HttpEntity<List<Integer>> updateEntity = new HttpEntity<>(batchesList, headers);
            restTemplate.exchange(
                    BASE_URL + "/movements/" + id + "/batches",
                    HttpMethod.PUT,
                    updateEntity,
                    Void.class);
            
            response.put("success", true);
            response.put("message", "Lotes actualizados correctamente");
            
        } catch (Exception e) {
            logger.error("Error al actualizar los lotes del movimiento", e);
            response.put("success", false);
            response.put("message", "Error al actualizar los lotes: " + e.getMessage());
        }
        
        return response;
    }

    // Método auxiliar para obtener el token desde la sesión
    private String getTokenFromSession(HttpSession session) {
        Object token = session.getAttribute("token");
        return token != null ? token.toString() : null;
    }
}