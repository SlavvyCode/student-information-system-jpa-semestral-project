package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Person;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParallelRepository extends CrudRepository<Parallel, Long> {
}
