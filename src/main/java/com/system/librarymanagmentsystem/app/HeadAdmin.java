package com.system.librarymanagmentsystem.app;

import com.system.librarymanagmentsystem.DAO.PersonDAO;

public class HeadAdmin extends Admin
{

    private HeadAdmin(String id, Name name, String password)
    {
        super(id, name, password);
    }

    public static HeadAdmin register(String id, Name name, String password)
    {
        return new HeadAdmin(id, name, password);
    }

    /**
     * Attempts to log in a HeadAdmin by verifying credentials against the database.
     * Returns the HeadAdmin if credentials are valid, null otherwise.
     */
    public static HeadAdmin login(String id, String password)
    {
        PersonDAO dao = new PersonDAO();
        Person person = dao.getPersonByIdAndPassword(id, password);
        dao.disconnect();

        if (person instanceof HeadAdmin) {
            System.out.println("HeadAdmin login successful for: " + person.getName().getFullName());
            return (HeadAdmin) person;
        }

        System.out.println("HeadAdmin login failed for id: " + id);
        return null;
    }

    /**
     * Creates a new Admin and persists them to the database.
     */
    public Admin addAdmin(String id, Name name, String password)
    {
        Admin admin = Admin.register(id, name, password);
        PersonDAO dao = new PersonDAO();
        dao.insertRecord(admin);
        dao.disconnect();
        System.out.println("HeadAdmin created new Admin: " + name.getFullName());
        return admin;
    }
}
