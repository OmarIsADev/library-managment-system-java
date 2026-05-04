package com.system.librarymanagmentsystem.app;

import java.util.ArrayList;
import java.util.List;

public class Shelf
{
    private String shelfId;
    private List<Book> books;
    private int capacity;

    public Shelf(String shelfId, int capacity)
    {
        this.shelfId = shelfId;
        this.books = new ArrayList<>();
        this.capacity = capacity;
    }

    public String getShelfId()
    {
        return shelfId;
    }

    public List<Book> getBooks()
    {
        return books;
    }

    public int getCapacity()
    {
        return capacity;
    }

    public void addBook(Book book)
    {
        if (isFull()) {
            System.out.println("Shelf '" + shelfId + "' is full. Cannot add book '" + book.getTitle() + "'.");
            return;
        }
        books.add(book);
    }

    public void removeBook(Book book)
    {
        books.remove(book);
    }

    /**
     * Returns a list of books that are not currently reserved.
     */
    public List<Book> getAvailableBooks()
    {
        List<Book> available = new ArrayList<>();
        for (Book book : books) {
            if (!book.isReserved()) {
                available.add(book);
            }
        }
        return available;
    }

    /**
     * Checks whether the shelf has reached its capacity.
     */
    public boolean isFull()
    {
        return books.size() >= capacity;
    }
}