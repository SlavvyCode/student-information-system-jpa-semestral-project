package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Person;
import org.springframework.data.repository.CrudRepository;

public interface ParallelRepository extends CrudRepository<Person, Long> {
}