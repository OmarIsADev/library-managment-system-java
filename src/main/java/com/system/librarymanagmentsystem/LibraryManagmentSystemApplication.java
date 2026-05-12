package com.system.librarymanagmentsystem;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.system.librarymanagmentsystem.DAO.PersonDAO;
import com.system.librarymanagmentsystem.app.HeadAdmin;
import com.system.librarymanagmentsystem.app.Name;

@SpringBootApplication
public class LibraryManagmentSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryManagmentSystemApplication.class, args);
    }

    @Bean
    public CommandLineRunner initDefaultAdmin() {
        return args -> {
            PersonDAO dao = new PersonDAO();
            if (dao.getRecordById("admin") == null) {
                Name adminName = new Name("System Admin", "System", "Admin");
                HeadAdmin defaultAdmin = HeadAdmin.register("admin", adminName, "admin");
                dao.insertRecord(defaultAdmin);
                System.out.println("Default HeadAdmin created (id: admin, password: admin)");
            }
            dao.disconnect();
        };
    }
}
