package cph.databases.assignment;

import cph.databases.assignment.entity.Doctor;
import cph.databases.assignment.utils.DataFaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class PrescriptionReminderApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrescriptionReminderApplication.class, args);
        DataFaker faker = new DataFaker();
//todo remove comment tags
/*
        faker.createDoser();
        List<Doctor> fake_doctors = faker.createDoctors();
        System.out.println("The size of fake doctors list is " + fake_doctors.size());

        faker.create_pharmacies_with_pharmacists();

        int a = faker.create_patients_and_contactinfo(0, 35);
        int b = faker.create_patients_and_contactinfo(35, 70);
        int c = faker.create_patients_and_contactinfo(70, 105);
        int d = faker.create_patients_and_contactinfo(105, 200);
        int z = a + b + c + d;
        System.out.println("added ci and pat: " + z);
        System.out.println("\n-----------\n_________________\n**************\n_________________\n-----------\n");
        System.out.println("\n\n\nFAKER is DONE\n\n\n");
        System.out.println("\n-----------\n_________________\n**************\n_________________\n-----------\n");
*/

    }


}
