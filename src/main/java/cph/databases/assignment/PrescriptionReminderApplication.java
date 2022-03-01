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
/*
        List<Doctor> fake_doctors = faker.createDoctors();
        System.out.println("The size of fake doctors list is " + fake_doctors.size());
        faker.createDoser();
*/

    }


}
