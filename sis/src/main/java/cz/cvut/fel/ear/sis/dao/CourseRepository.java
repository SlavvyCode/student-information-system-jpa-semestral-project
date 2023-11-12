package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Course;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends CrudRepository<Course, Long> {

}
