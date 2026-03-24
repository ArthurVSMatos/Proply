package com.proply.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // 1. CSRF: Desabilitado.
                // POR QUE? O CSRF protege contra ataques em sessões de browser (cookies).
                // Como nossa API é Stateless e usa JWT no Header, o CSRF não é necessário e até impediria chamadas externas.
                .csrf(csrf -> csrf.disable())

                // 2. POLÍTICA DE AUTORIZAÇÃO: Define quem entra onde.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register"
                        ).permitAll() // Portas abertas para novos usuários ou autenticação.
                        .anyRequest().authenticated() // Bloqueio total para qualquer outra rota sem um token válido.
                )

                // 3. STATELESS:
                // Aqui garantimos que o servidor não guardará "estado" (sessão) no servidor.
                // Cada requisição é independente e deve trazer sua própria prova de identidade (JWT).
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 4. ORDEM DOS FILTROS:
                // O Spring tem um filtro padrão chamado UsernamePasswordAuthenticationFilter.
                // Nós injetamos o nosso 'jwtFilter' ANTES dele.
                // Assim, validamos o token e preenchemos o contexto ANTES do Spring tentar qualquer outra autenticação.
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}