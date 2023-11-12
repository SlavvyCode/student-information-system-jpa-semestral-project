package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

}
