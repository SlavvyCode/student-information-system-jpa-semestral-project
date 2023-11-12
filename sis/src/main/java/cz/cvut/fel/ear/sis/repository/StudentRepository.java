package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

}
