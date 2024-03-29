package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    @Query("SELECT s FROM Student s JOIN s.myEnrollments e JOIN e.parallel p JOIN p.course c WHERE c = :course")
    public List<Student> findAllByCourse(@Param("course") Course course);

    //find all students which study in a parallel of a course
    @Query("SELECT s FROM Student s JOIN s.myEnrollments e JOIN e.parallel p JOIN p.course c WHERE c = :course AND s.id = :id")
    public List<Student> findAllByParallel(long id, Course course);



    @Query("SELECT s FROM Student s JOIN s.myEnrollments e JOIN e.parallel p WHERE p.id = :id")
    List<Student> findAllByParallelId(Long id);

    Optional<Student> findByUserName(String username);}
