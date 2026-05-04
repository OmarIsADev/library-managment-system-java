package com.system.librarymanagmentsystem.app;

public class TransactionDetails {

    private Student student;
    private TransType transactionType;
    private Book book;

    public TransactionDetails(Student student, TransType type, Book book) {
        this.student = student;
        this.transactionType = type;
        this.book = book;
    }

    public Student getStudent() {
        return student;
    }

    public TransType getTransactionType() {
        return transactionType;
    }

    public Book getBook() {
        return book;
    }

    public String getTransactionDetails() {
        return "Transaction { " +
                "student=" + student.getName().getFullName() +
                ", type=" + transactionType +
                ", book='" + book.getTitle() + "'" +
                " }";
    }

    @Override
    public String toString() {
        return getTransactionDetails();
    }
}
