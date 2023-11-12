package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {
}
