package com.system.librarymanagmentsystem.DAO;

import com.system.librarymanagmentsystem.app.Name;
import com.system.librarymanagmentsystem.app.Person;
import com.system.librarymanagmentsystem.app.Student;
import com.system.librarymanagmentsystem.app.Admin;
import com.system.librarymanagmentsystem.app.HeadAdmin;
import com.system.librarymanagmentsystem.handlers.DBHandler;
import com.system.librarymanagmentsystem.handlers.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDAO extends DBHandler {

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS persons (" +
            "id TEXT PRIMARY KEY, " +
            "first_name TEXT NOT NULL, " +
            "last_name TEXT NOT NULL, " +
            "full_name TEXT NOT NULL, " +
            "password TEXT NOT NULL, " +
            "role TEXT NOT NULL" +
            ")";

    public PersonDAO() {
        connect();
        createTable(CREATE_TABLE_SQL);
    }

    @Override
    public <T> void insertRecord(T record) {
        Person person = (Person) record;
        String role = getRole(person);
        String sql = "INSERT INTO persons (id, first_name, last_name, full_name, password, role) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, person.getId());
            pstmt.setString(2, person.getName().getFirstName());
            pstmt.setString(3, person.getName().getLastName());
            pstmt.setString(4, person.getName().getFullName());
            pstmt.setString(5, PasswordUtils.hash(person.getPassword()));
            pstmt.setString(6, role);
            pstmt.executeUpdate();
            System.out
                    .println("Person '" + person.getName().getFullName() + "' (" + role + ") inserted into database.");
        } catch (SQLException e) {
            System.out.println("Error inserting person: " + e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getRecordById(int id) {
        return (T) getRecordById(String.valueOf(id));
    }

    public Person getRecordById(String id) {
        String sql = "SELECT * FROM persons WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPerson(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching person: " + e.getMessage());
        }
        return null;
    }

    public Person getPersonByIdAndPassword(String id, String password) {
        String sql = "SELECT * FROM persons WHERE id = ? AND password = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, PasswordUtils.hash(password));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return mapResultSetToPerson(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error during login lookup: " + e.getMessage());
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T[] getAllRecords() {
        List<Person> persons = getAllPersons();
        return (T[]) persons.toArray(new Person[0]);
    }

    public List<Person> getAllPersons() {
        List<Person> persons = new ArrayList<>();
        String sql = "SELECT * FROM persons";

        try (Statement stmt = getConnection().createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                persons.add(mapResultSetToPerson(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching persons: " + e.getMessage());
        }
        return persons;
    }

    @Override
    public <T> void updateRecord(T record) {
        Person person = (Person) record;
        String sql = "UPDATE persons SET first_name = ?, last_name = ?, full_name = ?, password = ? WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, person.getName().getFirstName());
            pstmt.setString(2, person.getName().getLastName());
            pstmt.setString(3, person.getName().getFullName());
            pstmt.setString(4, person.getPassword());
            pstmt.setString(5, person.getId());
            pstmt.executeUpdate();
            System.out.println("Person '" + person.getName().getFullName() + "' updated in database.");
        } catch (SQLException e) {
            System.out.println("Error updating person: " + e.getMessage());
        }
    }

    @Override
    public <T> void deleteRecord(T record) {
        Person person = (Person) record;
        String sql = "DELETE FROM persons WHERE id = ?";

        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, person.getId());
            pstmt.executeUpdate();
            System.out.println("Person '" + person.getName().getFullName() + "' deleted from database.");
        } catch (SQLException e) {
            System.out.println("Error deleting person: " + e.getMessage());
        }
    }

    private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
        String id = rs.getString("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        String fullName = rs.getString("full_name");
        String password = rs.getString("password");
        String role = rs.getString("role");

        Name name = new Name(fullName, firstName, lastName);

        return switch (role) {
            case "STUDENT" -> Student.register(id, name, password);
            case "ADMIN" -> Admin.register(id, name, password);
            case "HEAD_ADMIN" -> HeadAdmin.register(id, name, password);
            default -> Student.register(id, name, password);
        };
    }

    private String getRole(Person person) {
        if (person instanceof HeadAdmin) {
            return "HEAD_ADMIN";
        } else if (person instanceof Admin) {
            return "ADMIN";
        } else if (person instanceof Student) {
            return "STUDENT";
        }
        return "UNKNOWN";
    }
}
