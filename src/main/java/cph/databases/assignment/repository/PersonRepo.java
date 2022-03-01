package cph.databases.assignment.repository;

import cph.databases.assignment.entity.Person;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Date;
import java.util.List;

public interface PersonRepo extends CrudRepository<Person, String> {

    @Query("select p.cpr from Person p where p.birthday >= ?1 and p.birthday <= ?2")
    List<String> findCprInBetween(Date oldest, Date youngest);
}
