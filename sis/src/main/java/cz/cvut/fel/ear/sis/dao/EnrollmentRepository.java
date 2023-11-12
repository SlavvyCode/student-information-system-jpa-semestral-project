package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Enrollment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends CrudRepository<Enrollment, Long> {
}
