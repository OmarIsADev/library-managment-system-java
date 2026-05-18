package com.system.librarymanagmentsystem.DAO;

import com.system.librarymanagmentsystem.handlers.DBHandler;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class TransactionDAO extends DBHandler {

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "student_id TEXT NOT NULL, " +
                    "book_id TEXT NOT NULL, " +
                    "type TEXT NOT NULL, " +
                    "created_at TEXT NOT NULL, " +
                    "due_date TEXT, " +
                    "late_fee REAL DEFAULT 0" +
                    ")";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    public TransactionDAO() {
        connect();
        createTable(CREATE_TABLE_SQL);
    }

    /**
     * Inserts a new transaction record.
     *
     * @param studentId the student who performed the action
     * @param bookId    the book involved
     * @param type      "RESERVE" or "RETURN"
     * @param dueDate   the due date (only meaningful for RESERVE transactions)
     * @param lateFee   the late fee charged (only meaningful for RETURN transactions)
     */
    public void insertTransaction(String studentId, String bookId, String type, Date dueDate, double lateFee) {
        String sql = "INSERT INTO transactions (student_id, book_id, type, created_at, due_date, late_fee) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, bookId);
            pstmt.setString(3, type);
            pstmt.setString(4, DATE_FORMAT.format(new Date()));
            pstmt.setString(5, dueDate != null ? DATE_FORMAT.format(dueDate) : null);
            pstmt.setDouble(6, lateFee);
            pstmt.executeUpdate();
            System.out.println("Transaction recorded: " + type + " for book " + bookId + " by student " + studentId);
        } catch (SQLException e) {
            System.out.println("Error inserting transaction: " + e.getMessage());
        }
    }

    /**
     * Gets all transactions for a specific student.
     */
    public List<Map<String, Object>> getTransactionsByStudentId(String studentId) {
        String sql = "SELECT t.*, b.title as book_title FROM transactions t " +
                "LEFT JOIN books b ON t.book_id = b.id " +
                "WHERE t.student_id = ? ORDER BY t.created_at DESC";

        return queryTransactions(sql, studentId);
    }

    /**
     * Gets all transactions for a specific book.
     */
    public List<Map<String, Object>> getTransactionsByBookId(String bookId) {
        String sql = "SELECT t.*, b.title as book_title FROM transactions t " +
                "LEFT JOIN books b ON t.book_id = b.id " +
                "WHERE t.book_id = ? ORDER BY t.created_at DESC";

        return queryTransactions(sql, bookId);
    }

    /**
     * Gets active reservations (books currently checked out) for a student.
     * An active reservation is a RESERVE transaction that has no corresponding RETURN.
     */
    public List<Map<String, Object>> getActiveReservationsByStudentId(String studentId) {
        String sql = "SELECT t.*, b.title as book_title, b.reserved as book_reserved " +
                "FROM transactions t " +
                "LEFT JOIN books b ON t.book_id = b.id " +
                "WHERE t.student_id = ? AND t.type = 'RESERVE' " +
                "AND t.book_id NOT IN (" +
                "  SELECT t2.book_id FROM transactions t2 " +
                "  WHERE t2.student_id = ? AND t2.type = 'RETURN' " +
                "  AND t2.created_at > t.created_at" +
                ") " +
                "ORDER BY t.created_at DESC";

        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, studentId);
            pstmt.setString(2, studentId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToMap(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching active reservations: " + e.getMessage());
        }

        return results;
    }

    /**
     * Gets all transactions (admin use).
     */
    public List<Map<String, Object>> getAllTransactions() {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = "SELECT t.*, b.title as book_title FROM transactions t " +
                "LEFT JOIN books b ON t.book_id = b.id " +
                "ORDER BY t.created_at DESC";

        try (Statement stmt = getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                results.add(mapResultSetToMap(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching all transactions: " + e.getMessage());
        }

        return results;
    }

    private List<Map<String, Object>> queryTransactions(String sql, String paramValue) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, paramValue);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                results.add(mapResultSetToMap(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        }

        return results;
    }

    private Map<String, Object> mapResultSetToMap(ResultSet rs) throws SQLException {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", rs.getInt("id"));
        map.put("studentId", rs.getString("student_id"));
        map.put("bookId", rs.getString("book_id"));
        map.put("type", rs.getString("type"));
        map.put("createdAt", rs.getString("created_at"));
        map.put("dueDate", rs.getString("due_date"));
        map.put("lateFee", rs.getDouble("late_fee"));

        // Book title from JOIN (may be null if book was deleted)
        try {
            map.put("bookTitle", rs.getString("book_title"));
        } catch (SQLException ignored) {
            map.put("bookTitle", null);
        }

        return map;
    }

    // These abstract methods are required by DBHandler but not used for TransactionDAO
    @Override
    public <T> void insertRecord(T record) {
        throw new UnsupportedOperationException("Use insertTransaction() instead");
    }

    @Override
    public <T> T getRecordById(int id) {
        throw new UnsupportedOperationException("Use specific query methods instead");
    }

    @Override
    public <T> T[] getAllRecords() {
        throw new UnsupportedOperationException("Use getAllTransactions() instead");
    }

    @Override
    public <T> void updateRecord(T record) {
        throw new UnsupportedOperationException("Transactions are immutable");
    }

    @Override
    public <T> void deleteRecord(T record) {
        throw new UnsupportedOperationException("Transactions should not be deleted");
    }
}
