package cph.databases.assignment.repository;

import cph.databases.assignment.entity.Prescription;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PrescriptionRepo extends CrudRepository<Prescription, Long> {

    @Query("select p from Prescription p where p.validUntil = ?1")
    List<Prescription> findPrescriptionThatExpireBy(String date);


    @Query("select p from Prescription p where p.remainingHandouts = ?1")
    List<Prescription> findPrescriptionWIthHandoutsLessThan(int handouts);
}
