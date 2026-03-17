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

        // 1. Extraímos o cabeçalho Authorization da requisição
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String email;

        // Se não houver token (ex: rotas públicas como /auth/login), passamos para o próximo filtro
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Limpamos a palavra "Bearer " para ficar só com o código do token (começa no índice 7)
        jwt = authHeader.substring(7);
        try {
            email = jwtService.extractEmail(jwt);
        } catch (Exception e) {
            filterChain.doFilter(request, response);
            return;
        } // Extrai o e-mail que guardámos no token

        try {
            // 3. Verificamos se temos um e-mail e se o utilizador ainda NÃO está autenticado no contexto atual
            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                // Procuramos o utilizador no banco de dados
                var user = userRepository.findByEmail(email).orElse(null);

                // Se o utilizador existir e o token for válido
                if (user != null && jwtService.isTokenValid(jwt, user.getEmail())) {

                    // --- PARTE A: AUTENTICAÇÃO DO SPRING SECURITY ---
                    // Criamos o "crachá" de acesso dizendo que ele está validado
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            null // Aqui futuramente podes colocar as permissões (user.getAuthorities())
                    );

                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken); // Guarda o crachá no Spring


                    // --- PARTE B: A MÁGICA DO MULTI-TENANT ---
                    // Pegamos a empresa atrelada a este utilizador e guardamos no nosso cofre
                    if (user.getCompany() != null) {
                        TenantContext.setTenant(user.getCompany());
                    }
                }
            }

            // 4. Deixamos a requisição seguir o seu caminho (ex: ir para o PropertyController)
            filterChain.doFilter(request, response);

        } finally {
            // 5. LIMPEZA DE SEGURANÇA (MUITO IMPORTANTE)
            // O bloco 'finally' garante que, independentemente do que aconteça no Controller (sucesso ou erro),
            // o contexto da empresa é limpo no final da requisição. Isso evita que os dados da Empresa A
            // vazem para a próxima pessoa que usar esta mesma Thread do servidor!
            TenantContext.clear();
        }
    }
}