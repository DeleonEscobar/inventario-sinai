package sv.sinai.client.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

@Controller
public class WebController extends BaseController {
    @Value("${api.baseURL}")
    private String BASE_URL;

    public WebController(RestTemplate restTemplate) {
        super(restTemplate);
    }

    @GetMapping("/")    
    public String index() {
        return "index";
    }
}