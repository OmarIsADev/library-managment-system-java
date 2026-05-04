package com.system.librarymanagmentsystem.app;

import java.util.Date;

public class Book {

    private final String ID;
    private String title;
    private double price;
    private boolean reserved;
    private Date reservedDate;
    private Date dueDate;
    private Shelf shelf;

    private static final double LATE_FEE_PER_DAY = 0.50;

    public Book(String title, String ID, double price, Shelf shelf){
        this.title=title;
        this.ID=ID;
        this.price=price;
        this.shelf=shelf;
        this.reserved=false;// book is not reserved by default
    }



    public String getTitle() {
        return title;
    }

    public Shelf getShelf() {
        return shelf;
    }

    public double getPrice() {
        return price;
    }

    public String getID() {
        return ID;
    }

    public boolean isReserved() {
        return reserved;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }


    /**
     * Reserves the book with a default duration of 14 days.
     */
    public void reserveBook(){
        reserveBook(14);
    }

    /**
     * Reserves the book for a specified number of days.
     */
    public void reserveBook(int days){
        // cannot reserve an already reserved book
        if(reserved) {
            throw new IllegalStateException("Book '"+title + "' is  already reserved");
        }
        this.reserved=true;
        this.reservedDate= new Date(); // set reservation date to today
        this.dueDate=new Date(System.currentTimeMillis()+ (long) days *24*60*60*1000);
        System.out.println("Reserved: "+title+" Due: "+dueDate);
    }

    public void returnBook(){
        // cannot return a book that was never reserved
        if(!reserved){
            throw  new IllegalStateException("Book '"+title+ "' was not reserved ");
        }
        this.reserved=false;
        this.reservedDate=null;// clear reservation date
        this.dueDate=null;// clear due date
        System.out.println("Returned: "+title);
    }

    /**
     * Checks whether the book is overdue (past its due date).
     */
    public boolean isOverdue() {
        if (!reserved || dueDate == null) {
            return false;
        }
        return new Date().after(dueDate);
    }

    /**
     * Calculates the late fee based on the number of overdue days.
     * Returns 0 if the book is not overdue.
     */
    public double calculateLateFee() {
        if (!isOverdue()) {
            return 0.0;
        }
        long overdueMs = System.currentTimeMillis() - dueDate.getTime();
        long overdueDays = overdueMs / (24 * 60 * 60 * 1000);
        return overdueDays * LATE_FEE_PER_DAY;
    }

    //searchByName
    public Book searchBook(String name) {
        // if the book has no shelf, nothing to search
        if (shelf == null)
            return null;
        for (Book b : shelf.getBooks()) {
            if (b.getTitle().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;// nothing matched
    }

    public Book searchBookById(String id) {
        if (shelf == null)
            return null;
        for (Book b : shelf.getBooks()) {
            if (b.getID().equals(id)) return b;
        }
        return null;
    }
    //these two methods have the same name and same parameter  So I changed the names and keeps the parameter and also add get id in book class
}
