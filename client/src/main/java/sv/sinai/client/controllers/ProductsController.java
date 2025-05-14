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

@Controller
@RequestMapping("/dashboard")
public class ProductsController {

    private final RestTemplate restTemplate;

    @Value("${api.baseURL}")
    private String BASE_URL;

    public ProductsController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }

    @GetMapping("admin/products")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String products(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("user", user);
        return "dashboard/products";
    }

}