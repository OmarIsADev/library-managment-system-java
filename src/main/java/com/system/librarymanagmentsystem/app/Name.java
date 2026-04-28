package com.system.librarymanagmentsystem.app;

public class Name
{
    private String firstName;
    private String lastName;
    private String fullName;

    public Name(String fullName, String firstName, String lastName)
    {
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFullName()
    {
        return fullName;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public String getLastName()
    {
        return lastName;
    }
}
