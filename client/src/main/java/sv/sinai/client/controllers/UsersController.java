package sv.sinai.client.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import sv.sinai.client.models.User;

@Controller
@RequestMapping("/dashboard")
public class UsersController extends BaseController {

    private final RestTemplate restTemplate;

    @Value("${api.baseURL}")
    private String BASE_URL;

    public UsersController(RestTemplate restTemplate) {
        super(restTemplate);
        this.restTemplate = restTemplate;
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }

    @GetMapping("admin/users")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String users(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Gestión de Usuarios");
        model.addAttribute("activePage", "users");
        return "dashboard/users";
    }
} 