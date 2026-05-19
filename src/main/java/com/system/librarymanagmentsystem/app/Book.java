package com.system.librarymanagmentsystem.app;

import java.util.Date;

public class Book {

    private final String ID;
    private String title;
    private double price;
    private boolean reserved;
    private String reservedBy;
    private Date reservedDate;
    private Date dueDate;
    private Shelf shelf;

    private double lateFeePerDay = 0.50;

    public Book(String title, String ID, double price, Shelf shelf){
        this.title=title;
        this.ID=ID;
        this.price=price;
        this.shelf=shelf;
        this.reserved=false;
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

    public void setPrice(double price) {
        this.price = price;
    }

    public double getLateFeePerDay() {
        return lateFeePerDay;
    }

    public void setLateFeePerDay(double lateFeePerDay) {
        this.lateFeePerDay = lateFeePerDay;
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

    public Date getReservedDate() {
        return reservedDate;
    }

    public String getReservedBy() {
        return reservedBy;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShelf(Shelf shelf) {
        this.shelf = shelf;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
    }

    public void setReservedDate(Date reservedDate) {
        this.reservedDate = reservedDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }


    /**
     * Reserves the book with a default duration of 14 days.
     */
    public void reserveBook(){
        reserveBook(null, 14);
    }

    /**
     * Reserves the book for a student with a default duration of 14 days.
     */
    public void reserveBook(String studentId){
        reserveBook(studentId, 14);
    }

    public void reserveBook(String studentId, int days){
        if(reserved) {
            throw new IllegalStateException("Book '"+title + "' is  already reserved");
        }
        this.reserved=true;
        this.reservedBy=studentId;
        this.reservedDate= new Date();
        this.dueDate=new Date(System.currentTimeMillis()+ (long) days *24*60*60*1000);
        System.out.println("Reserved: "+title+" Due: "+dueDate);
    }

    public void returnBook(){
        if(!reserved){
            throw  new IllegalStateException("Book '"+title+ "' was not reserved ");
        }
        this.reserved=false;
        this.reservedBy=null;
        this.reservedDate=null;
        this.dueDate=null;
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
        return overdueDays * lateFeePerDay;
    }

    public Book searchBook(String name) {
        if (shelf == null)
            return null;
        for (Book b : shelf.getBooks()) {
            if (b.getTitle().equalsIgnoreCase(name)) {
                return b;
            }
        }
        return null;
    }

    public Book searchBookById(String id) {
        if (shelf == null)
            return null;
        for (Book b : shelf.getBooks()) {
            if (b.getID().equals(id)) return b;
        }
        return null;
    }
}
