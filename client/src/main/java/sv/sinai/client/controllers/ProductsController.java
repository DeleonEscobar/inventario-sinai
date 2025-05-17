package sv.sinai.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.annotation.JsonCreator.Mode;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.client.RestTemplate;
import sv.sinai.client.models.User;
import sv.sinai.client.controllers.BaseController;

@Controller
@RequestMapping("/dashboard")
public class ProductsController extends BaseController {

    private final RestTemplate restTemplate;

    @Value("${api.baseURL}")
    private String BASE_URL;

    public ProductsController(RestTemplate restTemplate) {
        super(restTemplate);
        this.restTemplate = restTemplate;
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }

    @GetMapping("admin/products")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String products(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "Gestión de Productos");
        model.addAttribute("activePage", "products");
        return "dashboard/products";
    }

}