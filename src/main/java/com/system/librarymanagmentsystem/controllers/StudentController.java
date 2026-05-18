package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.BookDAO;
import com.system.librarymanagmentsystem.DAO.PersonDAO;
import com.system.librarymanagmentsystem.DAO.TransactionDAO;
import com.system.librarymanagmentsystem.app.Book;
import com.system.librarymanagmentsystem.app.Person;
import com.system.librarymanagmentsystem.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    /**
     * Get the profile of the currently authenticated student,
     * including their reserved books from the database.
     */
    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(
            @RequestAttribute(value = "userId", required = false) String userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Authentication required"));
        }

        PersonDAO personDAO = new PersonDAO();
        Person person = personDAO.getRecordById(userId);
        personDAO.disconnect();

        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("User not found"));
        }

        // Get books currently reserved by this student
        BookDAO bookDAO = new BookDAO();
        List<Book> reserved = bookDAO.getBooksByReservedBy(userId);
        bookDAO.disconnect();

        List<Map<String, Object>> reservedBooks = reserved.stream()
                .map(this::bookToMap)
                .toList();

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", person.getId());
        profile.put("name", person.getName().getFullName());
        profile.put("firstName", person.getName().getFirstName());
        profile.put("lastName", person.getName().getLastName());
        profile.put("reservedBooks", reservedBooks);
        profile.put("totalReserved", reservedBooks.size());

        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    /**
     * Get a student's profile by ID (admin only).
     * Includes their reserved books.
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse> getStudentProfile(
            @PathVariable("studentId") String studentId,
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can view other students' profiles"));
        }

        PersonDAO personDAO = new PersonDAO();
        Person person = personDAO.getRecordById(studentId);
        personDAO.disconnect();

        if (person == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Student not found with id: " + studentId));
        }

        // Get books currently reserved by this student
        BookDAO bookDAO = new BookDAO();
        List<Book> reserved = bookDAO.getBooksByReservedBy(studentId);
        bookDAO.disconnect();

        List<Map<String, Object>> reservedBooks = reserved.stream()
                .map(this::bookToMap)
                .toList();

        // Get transaction history
        TransactionDAO transactionDAO = new TransactionDAO();
        List<Map<String, Object>> transactions = transactionDAO.getTransactionsByStudentId(studentId);
        transactionDAO.disconnect();

        Map<String, Object> profile = new LinkedHashMap<>();
        profile.put("id", person.getId());
        profile.put("name", person.getName().getFullName());
        profile.put("firstName", person.getName().getFirstName());
        profile.put("lastName", person.getName().getLastName());
        profile.put("reservedBooks", reservedBooks);
        profile.put("totalReserved", reservedBooks.size());
        profile.put("transactions", transactions);

        return ResponseEntity.ok(ApiResponse.ok(profile));
    }

    private Map<String, Object> bookToMap(Book book) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", book.getID());
        map.put("title", book.getTitle());
        map.put("price", book.getPrice());
        map.put("reserved", book.isReserved());
        map.put("reservedBy", book.getReservedBy());
        map.put("reservedDate", book.getReservedDate());
        map.put("dueDate", book.getDueDate());
        map.put("overdue", book.isOverdue());
        map.put("lateFee", book.calculateLateFee());
        map.put("lateFeePerDay", book.getLateFeePerDay());
        return map;
    }
}
