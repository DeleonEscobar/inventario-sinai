package sv.sinai.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import sv.sinai.client.models.User;

public abstract class BaseController {
    protected final RestTemplate restTemplate;

    @Value("${api.baseURL}")
    protected String BASE_URL;

    public BaseController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected User getSessionUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }

    protected String getSessionToken(HttpSession session) {
        return (String) session.getAttribute("token");
    }

    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "default");
    }
} 