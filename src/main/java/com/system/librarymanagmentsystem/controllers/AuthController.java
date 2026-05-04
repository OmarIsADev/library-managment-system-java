package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.PersonDAO;
import com.system.librarymanagmentsystem.app.*;
import com.system.librarymanagmentsystem.dto.ApiResponse;
import com.system.librarymanagmentsystem.handlers.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;

    public AuthController(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    /**
     * POST /api/auth/login
     * Body: { "id": "...", "password": "...", "role": "STUDENT|ADMIN|HEAD_ADMIN" }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        String password = body.get("password");
        String role = body.getOrDefault("role", "STUDENT").toUpperCase();

        if (id == null || password == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("id and password are required"));
        }

        PersonDAO dao = new PersonDAO();
        Person person = dao.getPersonByIdAndPassword(id, password);
        dao.disconnect();

        if (person == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials"));
        }

        // Verify the person matches the requested role
        String actualRole = getRole(person);
        if (!actualRole.equals(role)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Invalid credentials for role: " + role));
        }

        String token = jwtUtils.generateToken(person.getId(), actualRole);

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", person.getId());
        data.put("name", person.getName().getFullName());
        data.put("role", actualRole);

        return ResponseEntity.ok(ApiResponse.ok("Login successful", data));
    }

    /**
     * POST /api/auth/register
     * Body: { "id": "...", "firstName": "...", "lastName": "...", "password": "..." }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@RequestBody Map<String, String> body) {
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
        Person existing = dao.getRecordById(id);
        if (existing != null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("User with id '" + id + "' already exists"));
        }

        String fullName = firstName + " " + lastName;
        Name name = new Name(fullName, firstName, lastName);
        Student student = Student.register(id, name, password);
        dao.insertRecord(student);
        dao.disconnect();

        String token = jwtUtils.generateToken(id, "STUDENT");

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("id", id);
        data.put("name", fullName);
        data.put("role", "STUDENT");

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Registration successful", data));
    }

    private String getRole(Person person) {
        if (person instanceof HeadAdmin) return "HEAD_ADMIN";
        if (person instanceof Admin) return "ADMIN";
        return "STUDENT";
    }
}
