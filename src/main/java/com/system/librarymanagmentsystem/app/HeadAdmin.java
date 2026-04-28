package com.system.librarymanagmentsystem.app;

public class HeadAdmin extends Admin
{

    private HeadAdmin(String id, Name name, String password)
    {
        super(id, name, password);
    }

    public static HeadAdmin login(String id, String password)
    {
        System.out.println("HeadAdmin login attempt for id: " + id);
        return null;
    }

    public Admin addAdmin(String id, Name name, String password)
    {
        System.out.println("HeadAdmin creating new Admin: " + name.getFullName());
        return Admin.register(id, name, password);
    }
}
