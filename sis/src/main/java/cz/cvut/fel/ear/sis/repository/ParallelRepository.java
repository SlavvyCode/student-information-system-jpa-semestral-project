package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParallelRepository extends JpaRepository<Parallel, Long> {

    //find all parallels belonging to classroom
    List<Parallel> findByClassroomAndSemesterAndDayOfWeekAndTimeSlot(Classroom classroom, Semester semester, DayOfWeek dayOfWeek, TimeSlot timeSlot);


    //find parallel by student id, course id, semester id
    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.id = :studentId " +
            "AND p.course.id = :courseId " +
            "AND p.semester.id = :semesterId")
    Parallel findByStudentIdAndCourseIdAndSemesterId(long studentId, long courseId, long semesterId);

}
