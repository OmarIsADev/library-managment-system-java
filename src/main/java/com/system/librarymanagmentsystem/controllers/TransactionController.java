package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.TransactionDAO;
import com.system.librarymanagmentsystem.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    /**
     * Get transactions for the currently authenticated user.
     */
    @GetMapping("/my")
    public ResponseEntity<ApiResponse> getMyTransactions(
            @RequestAttribute(value = "userId", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        TransactionDAO dao = new TransactionDAO();
        List<Map<String, Object>> transactions = dao.getTransactionsByStudentId(userId);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }

    /**
     * Get active reservations (currently checked-out books) for the authenticated user.
     */
    @GetMapping("/my/active")
    public ResponseEntity<ApiResponse> getMyActiveReservations(
            @RequestAttribute(value = "userId", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        TransactionDAO dao = new TransactionDAO();
        List<Map<String, Object>> reservations = dao.getActiveReservationsByStudentId(userId);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok(reservations));
    }

    /**
     * Get all transactions for a specific student (admin only).
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse> getTransactionsByStudent(
            @PathVariable("studentId") String studentId,
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can view other students' transactions"));
        }

        TransactionDAO dao = new TransactionDAO();
        List<Map<String, Object>> transactions = dao.getTransactionsByStudentId(studentId);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }

    /**
     * Get all transactions for a specific book (admin only).
     */
    @GetMapping("/book/{bookId}")
    public ResponseEntity<ApiResponse> getTransactionsByBook(
            @PathVariable("bookId") String bookId,
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can view book transaction history"));
        }

        TransactionDAO dao = new TransactionDAO();
        List<Map<String, Object>> transactions = dao.getTransactionsByBookId(bookId);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }

    /**
     * Get all transactions (admin only).
     */
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllTransactions(
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can view all transactions"));
        }

        TransactionDAO dao = new TransactionDAO();
        List<Map<String, Object>> transactions = dao.getAllTransactions();
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok(transactions));
    }
}
