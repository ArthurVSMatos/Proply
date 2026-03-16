package com.proply.shared.tenant;

import com.proply.features.company.entity.Company;

public class TenantContext {

    private static final ThreadLocal<Company> CURRENT_TENANT = new ThreadLocal<>();


    //cada requisição HTTP que chega ao servidor é processada por uma Thread (um fio de execução)
    //O ThreadLocal permite que você armazene dados que fiquem isolados dentro daquela Thread específica.
    public static void setTenant(Company company) {
        CURRENT_TENANT.set(company);
    }

    public static Company getTenant() {
        return CURRENT_TENANT.get();
    }


    //O Spring usa um "Thread Pool"
    //Esvazia o "cofre" daquela Thread.
    public static void clear() {
        CURRENT_TENANT.remove();
    }
}