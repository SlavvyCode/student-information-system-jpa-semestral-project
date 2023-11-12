package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

}
