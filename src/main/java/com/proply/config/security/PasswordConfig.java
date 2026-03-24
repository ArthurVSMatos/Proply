package com.proply.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuração centralizada para codificação de senhas.
 * Usar uma classe separada facilita a manutenção e evita dependências circulares.
 */
@Configuration
public class PasswordConfig {

    /**
     * Define o algoritmo de Hash que será usado em toda a aplicação.
     * @return Uma instância de BCryptPasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        // O BCrypt é o padrão da indústria porque ele possui o "Salt"(um conjunto de caracteres aleatórios) embutido
        // e é resistente a ataques de força bruta (Brute Force) e Rainbow Tables.
        return new BCryptPasswordEncoder();
    }
}