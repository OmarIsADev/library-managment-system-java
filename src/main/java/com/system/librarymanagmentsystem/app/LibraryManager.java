package com.system.librarymanagmentsystem.app;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LibraryManager {

    private List<Shelf> shelves;

    public LibraryManager() {
        this.shelves = new ArrayList<>();
    }

    public void addShelf(Shelf shelf) {
        shelves.add(shelf);
        System.out.println("Shelf '" + shelf.getShelfId() + "' added to library.");
    }

    public void removeShelf(Shelf shelf) {
        shelves.remove(shelf);
        System.out.println("Shelf '" + shelf.getShelfId() + "' removed from library.");
    }

    public List<Shelf> getShelves() {
        return shelves;
    }

    /**
     * Borrows (reserves) a book with a custom return date.
     */
    public void borrowBook(Book book, Date returnDate) {
        if (book.isReserved()) {
            System.out.println("Book '" + book.getTitle() + "' is already reserved and cannot be borrowed.");
            return;
        }
        book.reserveBook();
        System.out.println("Book '" + book.getTitle() + "' borrowed. Due: " + returnDate);
    }

    /**
     * Returns a previously borrowed book.
     */
    public void returnBook(Book book) {
        if (!book.isReserved()) {
            System.out.println("Book '" + book.getTitle() + "' was not borrowed.");
            return;
        }
        book.returnBook();
        System.out.println("Book '" + book.getTitle() + "' has been returned to the library.");
    }

    /**
     * Buys (permanently removes) a book from its shelf.
     */
    public void buyBook(Book book) {
        Shelf shelf = book.getShelf();
        if (shelf != null) {
            shelf.removeBook(book);
            book.setShelf(null);
            System.out.println("Book '" + book.getTitle() + "' has been purchased and removed from shelf '" + shelf.getShelfId() + "'.");
        } else {
            System.out.println("Book '" + book.getTitle() + "' is not on any shelf.");
        }
    }

    /**
     * Searches for a book by title within a specific shelf.
     */
    public Book searchBook(String title, Shelf shelf) {
        for (Book b : shelf.getBooks()) {
            if (b.getTitle().equalsIgnoreCase(title)) {
                return b;
            }
        }
        return null;
    }

    /**
     * Prints a report of all shelves and their books in the library.
     */
    public void printLibraryReport() {
        System.out.println("========== LIBRARY REPORT ==========");
        System.out.println("Total shelves: " + shelves.size());
        System.out.println();

        for (Shelf shelf : shelves) {
            System.out.println("--- Shelf: " + shelf.getShelfId() +
                    " (Capacity: " + shelf.getCapacity() +
                    ", Books: " + shelf.getBooks().size() +
                    ", Full: " + shelf.isFull() + ") ---");

            if (shelf.getBooks().isEmpty()) {
                System.out.println("  (empty)");
            } else {
                for (Book book : shelf.getBooks()) {
                    String status = book.isReserved() ? "RESERVED" : "AVAILABLE";
                    System.out.println("  [" + status + "] " + book.getTitle() +
                            " (ID: " + book.getID() +
                            ", Price: $" + book.getPrice() + ")");
                }
            }

            List<Book> available = shelf.getAvailableBooks();
            System.out.println("  Available: " + available.size() + "/" + shelf.getBooks().size());
            System.out.println();
        }

        System.out.println("====================================");
    }
}
