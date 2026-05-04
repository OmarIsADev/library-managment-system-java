package com.system.librarymanagmentsystem.app;

import com.system.librarymanagmentsystem.DAO.PersonDAO;

public class Admin extends Person
{

    protected Admin(String id, Name name, String password)
    {
        super(id, name, password);
    }

    /**
     * Attempts to log in an Admin by verifying credentials against the database.
     * Returns the Admin if credentials are valid, null otherwise.
     */
    public static Admin login(String id, String password)
    {
        PersonDAO dao = new PersonDAO();
        Person person = dao.getPersonByIdAndPassword(id, password);
        dao.disconnect();

        if (person instanceof Admin) {
            System.out.println("Admin login successful for: " + person.getName().getFullName());
            return (Admin) person;
        }

        System.out.println("Admin login failed for id: " + id);
        return null;
    }

    public static Admin register(String id, Name name, String password)
    {
        return new Admin(id, name, password);
    }

    public void addBookToShelf(Book book, Shelf shelf)
    {
        shelf.addBook(book);
        System.out.println("Book '" + book.getTitle() + "' added to shelf " + shelf.getShelfId());
    }

    public void removeBook(Book book, Shelf shelf)
    {
        shelf.removeBook(book);
        System.out.println("Book '" + book.getTitle() + "' removed from shelf " + shelf.getShelfId());
    }
}