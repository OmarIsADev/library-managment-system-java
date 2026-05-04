package com.system.librarymanagmentsystem.app;

import java.util.ArrayList;
import java.util.List;

public class TransactionHandler {

    private List<TransactionDetails> transactions;

    public TransactionHandler() {
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(TransactionDetails transaction) {
        transactions.add(transaction);
        System.out.println("Transaction added: " + transaction);
    }

    public void removeTransaction(TransactionDetails transaction) {
        transactions.remove(transaction);
        System.out.println("Transaction removed: " + transaction);
    }

    public List<TransactionDetails> getTransactions() {
        return transactions;
    }
}
