package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    List<Person> findByUserNameEndingWith(String firstAndLastName);
}
