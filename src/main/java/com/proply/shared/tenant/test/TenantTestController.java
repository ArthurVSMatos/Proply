package com.proply.shared.tenant.test;

import com.proply.shared.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint de diagnóstico para validar o isolamento de dados.
 */

@RestController
@RequestMapping("/test-tenant")
public class TenantTestController {

    @GetMapping
    public ResponseEntity<String> getTenantInfo() {
        // 1. RECUPERAÇÃO DO CONTEXTO:
        // Aqui acessamos o "cofre" que o TenantFilter ou JwtFilter preencheu.
        var tenant = TenantContext.getTenant();
        // 2. VALIDAÇÃO DE ESTADO:
        // Se cair aqui, significa que o filtro falhou ou a rota não foi protegida.
        if (tenant == null) return ResponseEntity.status(404).body("No tenant set!");
        // 3. RETORNO DE SUCESSO:
        // Exibimos os dados da empresa que está "presa" na Thread atual.
        return ResponseEntity.ok("Tenant: " + tenant.getName() + " | ID: " + tenant.getId());
    }
}