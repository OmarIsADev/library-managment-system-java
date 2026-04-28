package com.system.librarymanagmentsystem.app;

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

    public static Student login(String id, String password)
    {
        System.out.println("Student login attempt for id: " + id);
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

    public void reserveBook(Book book)
    {
        reservedBooks.add(book);
    }

    public void returnBook(Book book)
    {
        reservedBooks.remove(book);
    }
}
