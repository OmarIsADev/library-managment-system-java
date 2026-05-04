package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.PersonDAO;
import com.system.librarymanagmentsystem.app.Admin;
import com.system.librarymanagmentsystem.app.Name;
import com.system.librarymanagmentsystem.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/add-admin")
    public ResponseEntity<ApiResponse> addAdmin(
            @RequestBody Map<String, String> body,
            HttpServletRequest request
    ) {
        String role = (String) request.getAttribute("role");

        if (!"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only HeadAdmin can add new admins"));
        }

        String id = body.get("id");
        String firstName = body.get("firstName");
        String lastName = body.get("lastName");
        String password = body.get("password");

        if (id == null || firstName == null || lastName == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("id, firstName, lastName, and password are required"));
        }

        PersonDAO dao = new PersonDAO();

        // Check if user already exists
        if (dao.getRecordById(id) != null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("User with id '" + id + "' already exists"));
        }

        String fullName = firstName + " " + lastName;
        Name name = new Name(fullName, firstName, lastName);
        Admin admin = Admin.register(id, name, password);
        dao.insertRecord(admin);
        dao.disconnect();

        Map<String, Object> data = new HashMap<>();
        data.put("id", id);
        data.put("name", fullName);
        data.put("role", "ADMIN");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Admin created", data));
    }
}
