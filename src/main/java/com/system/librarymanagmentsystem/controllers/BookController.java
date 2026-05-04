package com.system.librarymanagmentsystem.controllers;

import com.system.librarymanagmentsystem.DAO.BookDAO;
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

    @GetMapping
    public ResponseEntity<ApiResponse> getAllBooks() {
        BookDAO dao = new BookDAO();
        List<Book> books = dao.getAllBooks();
        dao.disconnect();

        List<Map<String, Object>> bookList = books.stream().map(this::bookToMap).toList();
        return ResponseEntity.ok(ApiResponse.ok(bookList));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getBookById(@PathVariable String id) {
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
    public ResponseEntity<ApiResponse> addBook(@RequestBody Map<String, Object> body) {
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
    public ResponseEntity<ApiResponse> updateBook(@PathVariable String id, @RequestBody Map<String, Object> body) {
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
        // Price is read-only in current Book class, so we recreate if needed
        dao.updateRecord(book);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book updated", bookToMap(book)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteBook(@PathVariable String id) {
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
    public ResponseEntity<ApiResponse> reserveBook(@PathVariable String id) {
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

        book.reserveBook();
        dao.updateRecord(book);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book reserved", bookToMap(book)));
    }

    @PostMapping("/{id}/return")
    public ResponseEntity<ApiResponse> returnBook(@PathVariable String id) {
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

        book.returnBook();
        dao.updateRecord(book);
        dao.disconnect();

        return ResponseEntity.ok(ApiResponse.ok("Book returned", bookToMap(book)));
    }

    private Map<String, Object> bookToMap(Book book) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", book.getID());
        map.put("title", book.getTitle());
        map.put("price", book.getPrice());
        map.put("reserved", book.isReserved());
        map.put("dueDate", book.getDueDate());
        map.put("overdue", book.isOverdue());
        map.put("lateFee", book.calculateLateFee());
        return map;
    }
}
