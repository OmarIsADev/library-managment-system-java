package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.BookDAO;
import com.system.librarymanagmentsystem.DAO.TransactionDAO;
import com.system.librarymanagmentsystem.app.Book;
import com.system.librarymanagmentsystem.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllBooks() {
        BookDAO dao = new BookDAO();
        List<Book> books = dao.getAllBooks();
        dao.disconnect();

        List<Map<String, Object>> bookList = books.stream().map(this::bookToMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(bookList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable("id") String id) {
        BookDAO dao = new BookDAO();
        Book book = dao.getRecordById(id);
        dao.disconnect();

        if (book == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with id: " + id));
        }

        return ResponseEntity.ok(ApiResponse.ok(bookToMap(book)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> addBook(@RequestBody Map<String, Object> body,
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can add books"));
        }

        String id = (String) body.get("id");
        String title = (String) body.get("title");
        Double price = body.get("price") != null ? ((Number) body.get("price")).doubleValue() : null;

        if (id == null || title == null || price == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("id, title, and price are required"));
        }

        BookDAO dao = new BookDAO();

        // Check if book already exists
        Book existing = dao.getRecordById(id);
        if (existing != null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Book with id '" + id + "' already exists"));
        }

        Book book = new Book(title, id, price, null);
        dao.insertRecord(book);
        dao.disconnect();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Book added", bookToMap(book)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateBook(@PathVariable("id") String id,
            @RequestBody Map<String, Object> body) {
        BookDAO dao = new BookDAO();
        Book book = dao.getRecordById(id);

        if (book == null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with id: " + id));
        }

        if (body.containsKey("title")) {
            book.setTitle((String) body.get("title"));
        }
        if (body.containsKey("price")) {
            book.setPrice(((Number) body.get("price")).doubleValue());
        }
        if (body.containsKey("lateFeePerDay")) {
            book.setLateFeePerDay(((Number) body.get("lateFeePerDay")).doubleValue());
        }
        dao.updateRecord(book);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book updated", bookToMap(book)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable("id") String id,
            @RequestAttribute(value = "role", required = false) String role) {
        if (!"ADMIN".equals(role) && !"HEAD_ADMIN".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(ApiResponse.error("Only admins can delete books"));
        }

        BookDAO dao = new BookDAO();
        Book book = dao.getRecordById(id);

        if (book == null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with id: " + id));
        }

        dao.deleteRecord(book);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book deleted", null));
    }

    @PostMapping("/{id}/reserve")
    public ResponseEntity<ApiResponse> reserveBook(@PathVariable("id") String id,
            @RequestAttribute(value = "userId", required = false) String userId) {
        BookDAO dao = new BookDAO();
        Book book = dao.getRecordById(id);

        if (book == null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with id: " + id));
        }

        if (book.isReserved()) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Book '" + book.getTitle() + "' is already reserved"));
        }

        book.reserveBook(userId);
        dao.updateRecord(book);
        dao.disconnect();

        // Record the transaction
        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.insertTransaction(userId, id, "RESERVE", book.getDueDate(), 0);
        transactionDAO.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book reserved", bookToMap(book)));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse> returnBook(@PathVariable("id") String id,
            @RequestAttribute(value = "userId", required = false) String userId) {
        BookDAO dao = new BookDAO();
        Book book = dao.getRecordById(id);

        if (book == null) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Book not found with id: " + id));
        }

        if (!book.isReserved()) {
            dao.disconnect();
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("Book '" + book.getTitle() + "' is not reserved"));
        }

        // Calculate late fee before clearing reservation state
        double lateFee = book.calculateLateFee();
        String reservedBy = book.getReservedBy();

        book.returnBook();
        dao.updateRecord(book);
        dao.disconnect();

        // Record the transaction (use the original reserver's ID, not the returner)
        TransactionDAO transactionDAO = new TransactionDAO();
        transactionDAO.insertTransaction(
                reservedBy != null ? reservedBy : userId,
                id, "RETURN", null, lateFee);
        transactionDAO.disconnect();

        Map<String, Object> responseData = bookToMap(book);
        responseData.put("lateFeeCharged", lateFee);

        return ResponseEntity.ok(ApiResponse.ok("Book returned", responseData));
    }

    private Map<String, Object> bookToMap(Book book) {
        Map<String, Object> map = new HashMap<>();
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
