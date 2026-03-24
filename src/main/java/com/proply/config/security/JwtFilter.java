package com.proply.config.security;

import com.proply.features.user.repository.UserRepository;
import com.proply.shared.tenant.TenantContext; // Importante: O nosso cofre Multi-tenant!
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
/**
 * Filtro de interceptacao de requisições para validação de JWT e isolamento de Tenant.
 * OncePerRequestFilter e umaclasse base abstrata do Spring Framework que garante que um filtro personalizado seja executado exatamente uma vez por requisição HTTP
 * Herdando de OncePerRequestFilter garantimos que a lógica execute apenas uma vez por request.
 */

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. EXTRAÇÃO DO HEADER: Padrão RFC 6750 (Bearer Token)
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // Validação de "Fail-Fast": Se não houver token, segue para o próximo filtro (ex: login ou rotas públicas)
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Limpamos a palavra "Bearer " para ficar só com o código do token (começa no índice 7)
        jwt = authHeader.substring(7);
        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            // Em caso de token malformado ou erro na extração, interrompemos aqui por segurança
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 3. VALIDAÇÃO DE ESTADO: Verifica se o email existe e se o SecurityContext ainda está vazio
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Procuramos o utilizador no banco de dados
                var user = userRepository.findByEmail(email).orElse(null);

                // Se o utilizador existir e o token for válido
                if (user != null && jwtService.isTokenValid(jwt, user.getEmail())) {

                    // --- PARTE A: AUTENTICAÇÃO SPRING SECURITY ---
                    // Criamos o objeto de autenticação.
                    // Nota: O 'null' final seriam as Authorities/Roles (RBAC) para controle de permissões.
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            null
                    );

                    // Adiciona detalhes da requisição (IP, SessionID) ao objeto de autenticação
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Injeta o usuário autenticado no contexto global do Spring para esta Thread
                    SecurityContextHolder.getContext().setAuthentication(authToken);


                    // --- PARTE B: ESTRATÉGIA MULTI-TENANT (Isolamento de Dados) ---
                    // Se o usuário pertence a uma empresa, "setamos" o ID/Entidade no ThreadLocal.
                    // Isso garante que todos os SELECTs e INSERTs subsequentes sejam filtrados por essa empresa.
                    if (user.getCompany() != null) {
                        TenantContext.setTenant(user.getCompany());
                    }
                }
            }

            // 4. CONTINUIDADE: Permite que a requisição siga para o Controller
            filterChain.doFilter(request, response);

        } finally {
            // 5. HIGIENE DE THREAD (CRÍTICO):
            // Como servidores como Tomcat reutilizam Threads (Thread Pool), se não limparmos o TenantContext,
            // a próxima requisição de OUTRO cliente poderia, teoricamente, acessar dados desta empresa.
            // O bloco 'finally' garante a limpeza mesmo em caso de Runtime Exceptions no Controller.
            TenantContext.clear();
        }
    }
}