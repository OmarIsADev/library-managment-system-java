package com.system.librarymanagmentsystem.app;

public abstract class Person {
    private String id;
    private Name name;
    private String password;

    public Person(String id, Name name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public Name getName() {
        return name;
    }
}
