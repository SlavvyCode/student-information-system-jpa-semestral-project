package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Parallel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
