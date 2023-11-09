package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Person, Long> {
}
