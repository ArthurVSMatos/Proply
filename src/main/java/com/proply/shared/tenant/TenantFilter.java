package com.proply.shared.tenant;

import com.proply.features.company.repository.CompanyRepository;
import com.proply.shared.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TenantFilter extends HttpFilter {

    private final CompanyRepository companyRepository;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = request.getRequestURI();

        // 1. BYPASS DE ROTAS: Ignora endpoints que não precisam de contexto de empresa.
        if (path.startsWith("/login") || path.startsWith("/health")|| path.startsWith("/auth")||path.startsWith("/register")) {
            chain.doFilter(request, response);
            return;
        }


        // 2. EXTRAÇÃO VIA HEADER: Busca o identificador da empresa (Slug).
        // Um "Slug" é um nome amigável para URL (ex: 'imobiliaria-silva').
        String slug = request.getHeader("X-Company-Slug");

        // Se o header não vier, barramos a requisição. No White Label, o contexto é obrigatório.
        if (slug == null || slug.isBlank()) {
            writeError(response, new BusinessException("Company slug header is missing", HttpStatus.BAD_REQUEST));
            return;
        }

        try {
            // 3. VALIDAÇÃO NO BANCO: Verifica se a empresa realmente existe.
            var company = companyRepository.findBySlug(slug)
                    .orElseThrow(() -> new BusinessException("Company not found for slug: " + slug, HttpStatus.NOT_FOUND));

            // 4. ATIVAÇÃO DO CONTEXTO: Guarda a empresa encontrada no ThreadLocal.
            TenantContext.setTenant(company);
            MDC.put("tenant", company.getSlug());

            chain.doFilter(request, response);

        } catch (BusinessException ex) {
            writeError(response, ex);
        } finally {
            // 5. LIMPEZA DE SEGURANÇA: Garante que o contexto morra ao fim da requisição.
            TenantContext.clear();
            MDC.clear();
        }
    }

    // 6. TRATAMENTO DE ERRO MANUAL: Como o filtro roda antes do Controller,
    // precisamos escrever o JSON de erro manualmente na resposta.
    private void writeError(HttpServletResponse response, BusinessException ex) throws IOException {
        response.setStatus(ex.getStatus().value());
        response.setContentType("application/json");
        response.getWriter().write(
                "{\"timestamp\":" + System.currentTimeMillis() +
                        ",\"status\":" + ex.getStatus().value() +
                        ",\"error\":\"" + ex.getStatus().getReasonPhrase() + "\"" +
                        ",\"message\":\"" + ex.getMessage() + "\"}"
        );
    }
}