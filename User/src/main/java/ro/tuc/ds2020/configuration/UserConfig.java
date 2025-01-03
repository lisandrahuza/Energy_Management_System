package ro.tuc.ds2020.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class UserConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // This should work for all endpoints in your application
        registry.addMapping("/**") // Allowing all paths
                .allowedOrigins("http://web:3000") // Allow only localhost:3000 (where your front-end is hosted)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*") // Allow any header
                .allowCredentials(true); // Allow credentials (cookies, authorization headers)
    }
}
