package ro.tuc.ds2020.configuration;/*
package ro.tuc.ds2020.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ro.tuc.ds2020.entities.UserReference;
import ro.tuc.ds2020.repositories.UserReferenceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Configuration
public class UserReferenceConfig {
    @Bean
    CommandLineRunner commandLineRunnerUser(UserReferenceRepository userRepository){
        return args -> {
            UUID specificId1 = UUID.fromString("3c5e39f1-305e-4026-b8c6-3ad252b59df0");
            UUID specificId2 = UUID.fromString("4566aa06-c409-4575-a949-e8617dd0093d");
            UserReference u1=new UserReference(specificId1);
            UserReference u2=new UserReference(specificId2);
            List users=new ArrayList<>();
            users.add(u1);
            users.add(u2);

            //userRepository.saveAll(users);
        };
    }
}*/
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class DeviceConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
