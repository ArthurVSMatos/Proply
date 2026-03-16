package com.proply.shared.tenant.test;

import com.proply.shared.tenant.TenantContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-tenant")
public class TenantTestController {

    @GetMapping
    public ResponseEntity<String> getTenantInfo() {
        var tenant = TenantContext.getTenant();
        if (tenant == null) return ResponseEntity.status(404).body("No tenant set!");
        return ResponseEntity.ok("Tenant: " + tenant.getName() + " | ID: " + tenant.getId());
    }
}