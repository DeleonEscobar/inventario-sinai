package sv.sinai.client.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import sv.sinai.client.models.User;

@Controller
@RequestMapping("/dashboard")
public class EmployeeDashboardController extends BaseController {
    public EmployeeDashboardController(org.springframework.web.client.RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "employee");
    }

    // ======================================== EMPLOYEE DASHBOARD ========================================
    @GetMapping("/employee/index")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String bossDashboard(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("activePage", "home");
        return "dashboard/index";
    }

    // ======================================== EMPLOYEE PROJECTS MANAGEMENT ========================================
    @GetMapping("/employee/management/project")
    @PreAuthorize("hasAuthority('ACCESS_EMPLOYEE_DASHBOARD')")
    public String projectManagement(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Your Projects");
        model.addAttribute("activePage", "projects");
        return "dashboard/employee/projectManagement";
    }

    // El método de detalles de proyecto se omite porque ProjectService y Project no existen en el cliente.
}