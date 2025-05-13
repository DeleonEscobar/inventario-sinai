package sv.sinai.client.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
public class RestTemplateConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();

        /*
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(5000); // 5 segundos de timeout para conexión
        factory.setReadTimeout(5000);    // 5 segundos de timeout para lectura
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        return restTemplate;
        */
    }
} 