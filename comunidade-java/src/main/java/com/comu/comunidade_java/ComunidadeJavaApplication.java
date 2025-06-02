package com.comu.comunidade_java;

import com.comu.comunidade_java.entity.User;
import com.comu.comunidade_java.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
public class ComunidadeJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(ComunidadeJavaApplication.class, args);
    }

    @Bean
    CommandLineRunner run(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            
            if (userRepository.findByUsername("sistema").isEmpty()) {
                User systemUser = new User("sistema", "sistema@comunidade.com", passwordEncoder.encode("sistemaDefaultPass123"));
                systemUser.setRoles(Set.of("ROLE_SYSTEM")); 
                userRepository.save(systemUser);
                System.out.println(">>> Utilizador padrão 'sistema' criado.");
            }

            
            if (userRepository.findByUsername("userfiap").isEmpty()) {
                User testUser = new User(
                        "userfiap",
                        "user@fiap.com.br",
                        passwordEncoder.encode("senha123")
                );
                testUser.setRoles(Set.of("ROLE_USER"));
                userRepository.save(testUser);
                System.out.println(">>> Utilizador de teste 'userfiap' criado com senha 'senha123' e role ROLE_USER");
            }
           
            if (userRepository.findByUsername("adminfiap").isEmpty()) {
                User adminUser = new User(
                        "adminfiap",
                        "admin@fiap.com.br",
                        passwordEncoder.encode("admin123")
                );
                adminUser.setRoles(Set.of("ROLE_USER", "ROLE_ADMIN")); // Dar role USER também é comum para admins
                userRepository.save(adminUser);
                System.out.println(">>> Utilizador admin 'adminfiap' criado com senha 'admin123' e roles ROLE_USER, ROLE_ADMIN");
            }
        };
    }
}