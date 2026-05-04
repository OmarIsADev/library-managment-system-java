package com.system.librarymanagmentsystem.app;

import com.system.librarymanagmentsystem.DAO.PersonDAO;

import java.util.ArrayList;
import java.util.List;

public class Student extends Person
{
    private List<Book> reservedBooks;

    private Student(String id, Name name, String password)
    {
        super(id, name, password);
        this.reservedBooks = new ArrayList<>();
    }

    /**
     * Attempts to log in a Student by verifying credentials against the database.
     * Returns the Student if credentials are valid, null otherwise.
     */
    public static Student login(String id, String password)
    {
        PersonDAO dao = new PersonDAO();
        Person person = dao.getPersonByIdAndPassword(id, password);
        dao.disconnect();

        if (person instanceof Student) {
            System.out.println("Student login successful for: " + person.getName().getFullName());
            return (Student) person;
        }

        System.out.println("Student login failed for id: " + id);
        return null;
    }

    public static Student register(String id, Name name, String password)
    {
        return new Student(id, name, password);
    }

    public List<Book> getReservedBooks()
    {
        return reservedBooks;
    }

    /**
     * Reserves a book for this student.
     * Delegates to Book.reserveBook() to set reservation state, then tracks it locally.
     */
    public void reserveBook(Book book)
    {
        book.reserveBook();
        reservedBooks.add(book);
    }

    /**
     * Returns a book that was reserved by this student.
     * Delegates to Book.returnBook() to clear reservation state, then removes from local list.
     */
    public void returnBook(Book book)
    {
        book.returnBook();
        reservedBooks.remove(book);
    }
}
