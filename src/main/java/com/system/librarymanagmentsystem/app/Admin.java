package com.system.librarymanagmentsystem.app;

public class Admin extends Person
{

    protected Admin(String id, Name name, String password)
    {
        super(id, name, password);
    }

    public static Admin login(String id, String password)
    {
        System.out.println("Admin login attempt for id: " + id);
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