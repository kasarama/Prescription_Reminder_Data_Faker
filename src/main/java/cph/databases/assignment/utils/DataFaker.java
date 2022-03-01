package cph.databases.assignment.utils;

import cph.databases.assignment.entity.Doctor;
import cph.databases.assignment.entity.Dose;
import cph.databases.assignment.entity.Drug;
import cph.databases.assignment.entity.Person;
import cph.databases.assignment.repository.DoctorRepo;
import cph.databases.assignment.repository.DoseRepo;
import cph.databases.assignment.repository.DrugRepo;
import cph.databases.assignment.repository.PersonRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class DataFaker {
    private static DoseRepo doseRepo;
    private static DrugRepo drugRepo;
    private static PersonRepo personRepo;
    private static DoctorRepo doctorRepo;
    private static Helepr helper = new Helepr();
    private static Random random = new Random();


    @Autowired
    public DataFaker(DoseRepo doseRepo, DrugRepo drugRepo, PersonRepo personRepo, DoctorRepo doctorRepo) {
        this.doseRepo = doseRepo;
        this.drugRepo = drugRepo;
        this.personRepo = personRepo;
        this.doctorRepo = doctorRepo;


    }

    public DataFaker() {
    }

    public static void createDoser() {
        String[] forms = {
                "pill",
                "aerosol",
                "syrup",
                "suppository",
                "powder",
                "liquid",
                "inhaler"
        };

        String[] units = {
                "mg",
                "mikg",
                "g",
                "ml"
        };

        Iterable<Drug> allDrugs = drugRepo.findAll();
        int counter = 1;
        for (Drug drug : allDrugs) {
            int rand_index = (int) ((Math.random() * (units.length)));
            String unit = units[rand_index];
            int form_q = (int) (1 + (Math.random() * (4 - 1)));
            String form = forms[form_q];
            for (int i = 0; i < form_q; i++) {
                int one_tenth_strength = (int) (1 + (Math.random() * (101 - 1)));
                Dose dose = new Dose();
                dose.setForm(form);
                dose.setUnit(unit);
                dose.setStrength(one_tenth_strength * 5);
                dose.setDrug(drug);
                doseRepo.save(dose);
                System.out.println(counter);
                counter++;
            }
        }
    }

    /**
     * https://www.laeger.dk/laegeforeningen-i-tal
     */
    private static double people_in_danmark = 5843347;
    private static double prakticing_doctors = 27572 - 465;

    private static int get_ratio(long numberOfFakePersons) {
        return (int) (numberOfFakePersons * prakticing_doctors / people_in_danmark);
    }

    private static Date[] dates() {

        Date[] dates = new Date[2];
        LocalDateTime oldest = LocalDateTime.now().minusYears(75);
        LocalDateTime youngest = LocalDateTime.now().minusYears(24);

        Date oldestDate = Date.from(oldest.atZone(ZoneId.systemDefault()).toInstant());
        Date youngestDate = Date.from(youngest.atZone(ZoneId.systemDefault()).toInstant());
        dates[0] = oldestDate;
        dates[1] = youngestDate;
        return dates;
    }

    public static List<Doctor> createDoctors() {
        Date[] dates = dates();

        List<String> cprList = personRepo.findCprInBetween(dates[0], dates[1]);
        long all_fake_people = personRepo.count();
        System.out.println("all_fake_people: ");
        System.out.println("Number of people in age from 24 to 75 : " + cprList.size());

        List<Doctor> doctors = new ArrayList<>();

        int total_of_doctors = get_ratio(all_fake_people);
        while (total_of_doctors > 0) {
            int list_size = cprList.size();
            int rand_index = random.nextInt(list_size);
            String cpr = cprList.get(rand_index);
            Exception e = new Exception(String.format("Person with cpr %s does not exist in DB", cpr));

            Person p = null;
            try {
                p = personRepo.findById(cpr).orElseThrow(() -> e);
                Doctor d = new Doctor(p);
                doctors.add(d);
                total_of_doctors--;
            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(e.getMessage());
            }
            cprList.remove(cpr);
        }
        List<Doctor> saved_doctors = (List<Doctor>) doctorRepo.saveAll(doctors);


        return saved_doctors;

    }


}
