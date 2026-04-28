package com.system.librarymanagmentsystem.app;

import java.util.ArrayList;
import java.util.List;

public class Shelf
{
    private String shelfId;
    private List<Book> books;

    public Shelf(String shelfId)
    {
        this.shelfId = shelfId;
        this.books = new ArrayList<>();
    }

    public String getShelfId()
    {
        return shelfId;
    }

    public List<Book> getBooks()
    {
        return books;
    }

    public void addBook(Book book)
    {
        books.add(book);
    }

    public void removeBook(Book book)
    {
        books.remove(book);
    }
}