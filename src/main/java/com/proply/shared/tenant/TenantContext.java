package com.proply.shared.tenant;

import com.proply.features.company.entity.Company;

/**
 * Esta classe é o container de contexto da aplicação.
 * Ela utiliza a estratégia de ThreadLocal para isolar os dados de cada empresa (Tenant).
 */
public class TenantContext {

    // 1. O CORAÇÃO DO ISOLAMENTO: ThreadLocal.
    // Ele cria uma "caixa" de memória que só pode ser acessada pela Thread que a criou.
    // Como o Spring processa cada requisição HTTP em uma Thread diferente,
    // garantimos que o Usuário A nunca acesse o Tenant do Usuário B.
    private static final ThreadLocal<Company> CURRENT_TENANT = new ThreadLocal<>();

    /**
     * Define a empresa do contexto atual.
     * Chamado geralmente logo após a autenticação no JwtFilter.
     */
    public static void setTenant(Company company) {
        CURRENT_TENANT.set(company);
    }

    /**
     * Recupera a empresa do contexto atual.
     * Pode ser usado em Services, Repositories ou até em Auditorias (CreatedBy).
     */
    public static Company getTenant() {
        return CURRENT_TENANT.get();
    }

    /**
     * LIMPEZA (Crucial): Remove o valor da Thread atual.
     * O Spring reutiliza Threads (Thread Pool). Se não removermos,
     * a próxima requisição que usar esta mesma Thread "herdará" o Tenant anterior (Vazamento de dados).
     */
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}