package sv.sinai.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpSession;
import sv.sinai.client.models.User;

@Controller
@RequestMapping("/dashboard")
public class BatchesController extends BaseController {

    @Value("${api.baseURL}")
    private String BASE_URL;

    public BatchesController(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @Override
    @ModelAttribute
    public void commonAttributes(Model model) {
        model.addAttribute("route", "admin");
    }

    @GetMapping("admin/batches")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String batches(HttpSession session, Model model) {
        User user = getSessionUser(session);
        model.addAttribute("user", user);
        return "dashboard/batches";
    }

}