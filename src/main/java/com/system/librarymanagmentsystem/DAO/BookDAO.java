package com.system.librarymanagmentsystem.DAO;

import com.system.librarymanagmentsystem.app.Book;
import com.system.librarymanagmentsystem.app.Shelf;
import com.system.librarymanagmentsystem.handlers.DBHandler;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDAO extends DBHandler {

    private static final String CREATE_TABLE_SQL =
            "CREATE TABLE IF NOT EXISTS books (" +
                    "id TEXT PRIMARY KEY, " +
                    "title TEXT NOT NULL, " +
                    "price REAL NOT NULL, " +
                    "reserved INTEGER NOT NULL DEFAULT 0, " +
                    "shelf_id TEXT" +
                    ")";

    public BookDAO() {
        connect();
        createTable(CREATE_TABLE_SQL);
    }

    @Override
    public <T> void insertRecord(T record) {
        Book book = (Book) record;
        String sql = "INSERT INTO books (id, title, price, reserved, shelf_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getID());
            pstmt.setString(2, book.getTitle());
            pstmt.setDouble(3, book.getPrice());
            pstmt.setInt(4, book.isReserved() ? 1 : 0);
            pstmt.setString(5, book.getShelf() != null ? book.getShelf().getShelfId() : null);
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

    @Override
    public <T> void updateRecord(T record) {
        Book book = (Book) record;
        String sql = "UPDATE books SET title = ?, price = ?, reserved = ?, shelf_id = ? WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, book.getTitle());
            pstmt.setDouble(2, book.getPrice());
            pstmt.setInt(3, book.isReserved() ? 1 : 0);
            pstmt.setString(4, book.getShelf() != null ? book.getShelf().getShelfId() : null);
            pstmt.setString(5, book.getID());
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
        // Shelf is not fully reconstructed here; set to null
        // In a real app you'd look up the shelf from a ShelfDAO
        return new Book(title, id, price, null);
    }
}
