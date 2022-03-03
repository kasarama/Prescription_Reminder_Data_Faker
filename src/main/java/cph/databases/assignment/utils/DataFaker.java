package cph.databases.assignment.utils;

import cph.databases.assignment.entity.*;
import cph.databases.assignment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
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
    private static AddressRepo addressRepo;
    private static PharmacyRepo pharmacyRepo;
    private static PharmacistRepo pharmacistRepo;
    private static PatientRepo patientRepo;
    private static ContactRepo contactRepo;
    private static PrescriptionRepo prescRepo;
    private static Helepr helper = new Helepr();
    private static Random random = new Random();
    private static List<Address> addressList;


    @Autowired
    public DataFaker(DoseRepo doseRepo, DrugRepo drugRepo, PersonRepo personRepo, DoctorRepo doctorRepo,
                     AddressRepo addressRepo, PharmacyRepo pharmacyRepo, PharmacistRepo pharmacistRepo,
                     PatientRepo patientRepo, ContactRepo contactRepo, PrescriptionRepo prescRepo) {
        this.doseRepo = doseRepo;
        this.drugRepo = drugRepo;
        this.personRepo = personRepo;
        this.doctorRepo = doctorRepo;
        this.addressRepo = addressRepo;
        this.pharmacyRepo = pharmacyRepo;
        this.pharmacistRepo = pharmacistRepo;
        this.patientRepo = patientRepo;
        this.contactRepo = contactRepo;
        this.prescRepo = prescRepo;
        this.addressList = (List<Address>) addressRepo.findAll();


    }

    public DataFaker() {
    }

    /**
     * gets random unit per drug,
     * gets random form of that drug,
     * gets random number for available strengths
     * and creates a new dose with that unit, form and strength
     * Persists doses
     */
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
    private static final double people_in_denmark = 5843347;
    private static final double practicing_doctors = 27572 - 465;
    private static final double pharmacies_and_pharmaconomists_in_Denmark = 3500;
    private static final double pharmacies_in_Denmark = 522;

    private static int get_ratio(double real_number_of_entity) {
        long numberOfFakePersons = personRepo.count();
        return (int) (real_number_of_entity * numberOfFakePersons / people_in_denmark);
    }

    /**
     * returns two dates one low years ago and the other high years ago
     */
    private static Date[] dates(int low, int high) {

        Date[] dates = new Date[2];
        LocalDateTime oldest = LocalDateTime.now().minusYears(high);
        LocalDateTime youngest = LocalDateTime.now().minusYears(low);

        Date oldestDate = Date.from(oldest.atZone(ZoneId.systemDefault()).toInstant());
        Date youngestDate = Date.from(youngest.atZone(ZoneId.systemDefault()).toInstant());
        dates[0] = oldestDate;
        dates[1] = youngestDate;
        return dates;
    }

    /**
     * Calculates the proportional number of doctors to create,
     * creates uniq doctors and persists them
     */
    public static List<Doctor> createDoctors() {
        Date[] dates = dates(23, 75);

        List<String> cprList = personRepo.findCprInBetween(dates[0], dates[1]);

        System.out.println("all_fake_people: ");
        System.out.println("Number of people in age from 24 to 75 : " + cprList.size());

        List<Doctor> doctors = new ArrayList<>();

        int total_of_doctors = get_ratio(practicing_doctors);
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

    /**
     * Calculates the proportional number of pharmacies to create,
     * for each pharmacy creates random number of pharmacists,
     * persist all created entities
     */
    public static void create_pharmacies_with_pharmacists() {
        int entities_to_persist = get_ratio(pharmacies_in_Denmark);
        Date[] dates = dates(23, 75);
        List<String> cprList = personRepo.findCprInBetween(dates[0], dates[1]);

        List<Pharmacy> pharmacyList = new ArrayList<>();
        List<Pharmacist> phists = new ArrayList<>();


        while (entities_to_persist > 0) {
            String p_address = random_address().getStreet() + ", " + Integer.toString(random_address().getZipcode());
            Pharmacy pharmacy = new Pharmacy(p_address, random_phone());

            for (int i = 0; i < random.nextInt(5) + 1; i++) {
                int list_size = cprList.size();
                int rand_index = random.nextInt(list_size);
                String cpr = cprList.get(rand_index);
                Exception e = new Exception(String.format("Person with cpr %s does not exist in DB", cpr));

                Person person = null;
                try {
                    person = personRepo.findById(cpr).orElseThrow(() -> e);
                    Pharmacist phist = new Pharmacist(person, pharmacy);
                    phists.add(phist);
                    pharmacy.addEmployee(phist);

                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.out.println(e.getMessage());
                }
                cprList.remove(cpr);
            }
            pharmacyList.add(pharmacy);

            entities_to_persist--;
        }
        List<Pharmacy> persisted = (List<Pharmacy>) pharmacyRepo.saveAll(pharmacyList);
        List<Pharmacist> persisted_pharmacists = (List<Pharmacist>) pharmacistRepo.saveAll(phists);
        System.out.println("Persisted pharmacies : " + persisted.size());
        System.out.println("Created pharmacists: " + persisted_pharmacists.size());

    }


    public static int create_patients_and_contactinfo(int low, int high) {
        Date[] dates = dates(low, high);
        List<String> cprList = personRepo.findCprInBetween(dates[0], dates[1]);
        String[] fake_emails = {"debug000333@gmail.com", "debug000222@gmail.com", "debug000111@gmail.com"};

        List<Pharmacy> pharmacies = (List<Pharmacy>) pharmacyRepo.findAll();

        int added = 0;

        for (String cpr : cprList) {

            Exception e = new Exception(String.format("Person with cpr %s does not exist in DB", cpr));

            try {
                Person person = personRepo.findById(cpr).orElseThrow(() -> e);
                int rand_mail = random.nextInt(3);
                ContactInformation cinfo = new ContactInformation(person, fake_emails[rand_mail], random_phone(), random_address());

                ContactInformation saved_info = contactRepo.save(cinfo);
                person.setCi(saved_info);

                personRepo.save(person);

                Patient patient = new Patient(person);
                patient.setFavoritePharmacyId(pharmacies.get(random.nextInt(pharmacies.size())).getId());
                patientRepo.save(patient);
                added++;


            } catch (Exception ex) {
                ex.printStackTrace();
                System.out.println(ex.getMessage());
            }

        }
        System.out.println("Added patients and contact info: " + added);
        return added;
    }


    private static int random_phone() {
        return random.nextInt(99999999 - 10000000) + 10000000;
    }

    /**
     * returns random address from DB
     *
     * @return Address
     */
    private static Address random_address() {
        int list_size = addressList.size();
        int rand_index = random.nextInt(list_size);
        return addressList.get(rand_index);
    }


    public static String prescriptions(int quant) {
        List<Doctor> doctors = (List<Doctor>) doctorRepo.findAll();

        List<Dose> doses = (List<Dose>) doseRepo.findAll();
        Exception e = new Exception("Person with cpr %s does not exist in DB");
        int all_pat = (int) patientRepo.count();

        int counter = 0;

        while (counter < quant) {
            long rand_index = (long) random.nextInt(all_pat) + 1;
            try {
                Patient patient = patientRepo.findById(rand_index).orElseThrow(() -> e);

                for (int i = 0; i < random.nextInt(5) + 1; i++) {


                    Doctor doctor = doctors.get(random.nextInt(doctors.size()));
                    Dose dose = doses.get(random.nextInt(doctors.size()));
                    LocalDateTime tr_start = LocalDateTime.now().minusDays(random.nextInt(50) + 2);
                    LocalDateTime tr_end = LocalDateTime.now().plusDays(random.nextInt(50) + 2);
                    LocalDateTime valid = LocalDateTime.now().plusDays(random.nextInt(20));


                    Date start_date = Date.from(tr_start.atZone(ZoneId.systemDefault()).toInstant());
                    Date end_date = Date.from(tr_end.atZone(ZoneId.systemDefault()).toInstant());
                    Date valid_date = Date.from(valid.atZone(ZoneId.systemDefault()).toInstant());
                    int remaining_handouts = random.nextInt(6);

                    boolean subst = random.nextBoolean();

                    Prescription pres = new Prescription(patient, dose, doctor, remaining_handouts, start_date, end_date, valid_date, subst);
                    prescRepo.save(pres);
                }

                counter++;
            } catch (Exception ex) {
                System.out.println("Patient with index : " + rand_index + " not found");
            }
        }

        return "Added more than " + counter;
    }

}
