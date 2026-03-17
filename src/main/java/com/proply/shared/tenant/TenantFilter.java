package com.proply.shared.tenant;

import com.proply.features.company.repository.CompanyRepository;
import com.proply.shared.exception.BusinessException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        // Se for endpoint público, ignora
        if (path.startsWith("/login") || path.startsWith("/health")|| path.startsWith("/auth")) {
            chain.doFilter(request, response);
            return;
        }

        String slug = request.getHeader("X-Company-Slug");
        if (slug == null || slug.isBlank()) {
            writeError(response, new BusinessException("Company slug header is missing", HttpStatus.BAD_REQUEST));
            return;
        }

        try {
            var company = companyRepository.findBySlug(slug)
                    .orElseThrow(() -> new BusinessException("Company not found for slug: " + slug, HttpStatus.NOT_FOUND));

            // Define o tenant atual (ThreadLocal)
            TenantContext.setTenant(company);

            chain.doFilter(request, response);

        } catch (BusinessException ex) {
            writeError(response, ex);
        } finally {
            TenantContext.clear();
        }
    }

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