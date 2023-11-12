package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Semester;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SemesterRepository extends CrudRepository<Semester, Long> {
}
