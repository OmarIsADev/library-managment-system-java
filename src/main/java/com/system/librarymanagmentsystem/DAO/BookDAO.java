package com.system.librarymanagmentsystem.DAO;

import com.system.librarymanagmentsystem.app.Book;
import com.system.librarymanagmentsystem.app.Shelf;
import com.system.librarymanagmentsystem.handlers.DBHandler;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DBHandler {

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS books (" +
                    "id TEXT PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "reserved INTEGER NOT NULL DEFAULT 0, " +
                    "reserved_by TEXT, " +
                    "reserved_date TEXT, " +
                    "due_date TEXT, " +
                    "late_fee_per_day REAL NOT NULL DEFAULT 0.50, " +
                    "shelf_id TEXT" +
                    ")";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public BookDAO() {
        connect();
        createTable(CREATE_TABLE_SQL);
        migrateTable();
    }

    /**
     * Adds new columns to existing databases that were created before these columns existed.
     * Each ALTER TABLE is wrapped in try/catch so it silently skips if the column already exists.
     */
    private void migrateTable() {
        String[] migrations = {
                "ALTER TABLE books ADD COLUMN reserved_by TEXT",
                "ALTER TABLE books ADD COLUMN reserved_date TEXT",
                "ALTER TABLE books ADD COLUMN due_date TEXT",
                "ALTER TABLE books ADD COLUMN late_fee_per_day REAL NOT NULL DEFAULT 0.50"
        };

        for (String sql : migrations) {
            try (Statement stmt = getConnection().createStatement()) {
                stmt.execute(sql);
            } catch (SQLException ignored) {
                // Column already exists — safe to ignore
            }
        }
    }

    @Override
    public <T> void insertRecord(T record) {
        Book book = (Book) record;
        String sql = "INSERT INTO books (id, title, price, reserved, reserved_by, reserved_date, due_date, late_fee_per_day, shelf_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getID());
            pstmt.setString(2, book.getTitle());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setInt(4, book.isReserved() ? 1 : 0);
            pstmt.setString(5, book.getReservedBy());
            pstmt.setString(6, book.getReservedDate() != null ? DATE_FORMAT.format(book.getReservedDate()) : null);
            pstmt.setString(7, book.getDueDate() != null ? DATE_FORMAT.format(book.getDueDate()) : null);
            pstmt.setDouble(8, book.getLateFeePerDay());
            pstmt.setString(9, book.getShelf() != null ? book.getShelf().getShelfId() : null);
            pstmt.executeUpdate();
            System.out.println("Book '" + book.getTitle() + "' inserted into database.");
        } catch (SQLException e) {
            System.out.println("Error inserting book: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getRecordById(int id) {
        return (T) getRecordById(String.valueOf(id));
    }

    public Book getRecordById(String id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToBook(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching book: " + e.getMessage());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] getAllRecords() {
        List<Book> books = getAllBooks();
        return (T[]) books.toArray(new Book[0]);
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching books: " + e.getMessage());
        }
        return books;
    }

    /**
     * Gets all books currently reserved by a specific student.
     */
    public List<Book> getBooksByReservedBy(String studentId) {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books WHERE reserved = 1 AND reserved_by = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                books.add(mapResultSetToBook(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching reserved books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public <T> void updateRecord(T record) {
        Book book = (Book) record;
        String sql = "UPDATE books SET title = ?, price = ?, reserved = ?, reserved_by = ?, reserved_date = ?, due_date = ?, late_fee_per_day = ?, shelf_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setDouble(2, book.getPrice());
            pstmt.setInt(3, book.isReserved() ? 1 : 0);
            pstmt.setString(4, book.getReservedBy());
            pstmt.setString(5, book.getReservedDate() != null ? DATE_FORMAT.format(book.getReservedDate()) : null);
            pstmt.setString(6, book.getDueDate() != null ? DATE_FORMAT.format(book.getDueDate()) : null);
            pstmt.setDouble(7, book.getLateFeePerDay());
            pstmt.setString(8, book.getShelf() != null ? book.getShelf().getShelfId() : null);
            pstmt.setString(9, book.getID());
            pstmt.executeUpdate();
            System.out.println("Book '" + book.getTitle() + "' updated in database.");
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    @Override
    public <T> void deleteRecord(T record) {
        Book book = (Book) record;
        String sql = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getID());
            pstmt.executeUpdate();
            System.out.println("Book '" + book.getTitle() + "' deleted from database.");
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    private Book mapResultSetToBook(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String title = rs.getString("title");
        double price = rs.getDouble("price");
        boolean reserved = rs.getInt("reserved") == 1;
        String reservedBy = rs.getString("reserved_by");
        String reservedDateStr = rs.getString("reserved_date");
        String dueDateStr = rs.getString("due_date");
        // Shelf is not fully reconstructed here; set to null
        // In a real app you'd look up the shelf from a ShelfDAO
        Book book = new Book(title, id, price, null);

        // Restore late fee per day (handle missing column gracefully)
        try {
            double lateFeePerDay = rs.getDouble("late_fee_per_day");
            book.setLateFeePerDay(lateFeePerDay);
        } catch (SQLException ignored) {
            // Column may not exist in older databases
        }

        if (reserved) {
            book.setReserved(true);
            book.setReservedBy(reservedBy);
            try {
                if (reservedDateStr != null) {
                    book.setReservedDate(DATE_FORMAT.parse(reservedDateStr));
                }
                if (dueDateStr != null) {
                    book.setDueDate(DATE_FORMAT.parse(dueDateStr));
                }
            } catch (ParseException e) {
                System.out.println("Error parsing date for book '" + title + "': " + e.getMessage());
            }
        }
        return book;
    }
}
