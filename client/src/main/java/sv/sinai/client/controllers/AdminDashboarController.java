package sv.sinai.client.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import sv.sinai.client.models.User;

@Controller
@RequestMapping("/dashboard")
public class AdminDashboarController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    public AdminDashboarController(){

    }

    @ModelAttribute
    public void commonAttributes(Model model){
        model.addAttribute("route", "admin");
    }

    @GetMapping("/admin/index")
    @PreAuthorize("hasAuthority('ACCESS_ADMIN')")
    public String adminDashboard(HttpSession session, Model model) {

        User user = (User) session.getAttribute("user");

        model.addAttribute("user", user);

        return "dashboard/index";
    }
}
