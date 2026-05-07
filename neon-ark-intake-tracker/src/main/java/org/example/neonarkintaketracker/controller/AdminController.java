package org.example.neonarkintaketracker.controller;

import org.example.neonarkintaketracker.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller responsible for administrative endpoints.
 *
 * Provides access to system-level operations.
 * Enforces role-based access control for admin-only routes.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService service;

    public AdminController(AdminService service) {
        this.service = service;
    }

    //Test 200
    // Invoke-RestMethod http://localhost:8080/api/admin/users -Headers @{"role"="ADMIN"}
    // Test 403
    // Invoke-RestMethod http://localhost:8080/api/admin/users -Headers @{"role"="STAFF"}


    /**
     * Retrieves all users in the system.
     *
     * Access is restricted to users with the ADMIN role.
     * Returns status codes based on authorization.
     *
     * API Route: GET /api/admin/users
     *
     * @param role role provided via request header
     * @return list of users if authorized, otherwise an error response
     */
    @GetMapping("/users")
    public ResponseEntity<?> getUsers(
            @RequestHeader(value = "role", required = false) String role) {

        if (role == null) {
            return ResponseEntity.status(401)
                    .body("Unauthorized: no role provided");
        }

        if (!role.equalsIgnoreCase("ADMIN")) {
            return ResponseEntity.status(403)
                    .body("Forbidden: admin access required");
        }

        return ResponseEntity.ok(service.getAllUsers());
    }
}