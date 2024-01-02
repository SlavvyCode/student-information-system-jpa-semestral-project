package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

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



    List<Parallel> findAllBySemester_StartDateAndCourse_Teacher_Id(LocalDate startDate, long teacherId);
    List<Parallel> findAllBySemester_StartDate(LocalDate startDate);

    List<Parallel> findAllByCourse_Id(Long id);



    List<Parallel> findAllByCourse_IdAndSemester_StartDate(Long id, LocalDate startDate);


    //find all parallels belonging to student next semester
    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.id = :studentId " +
            "AND p.semester.code = :semesterCode")
    List<Parallel> findAllByStudents_IdAndSemester_Code(long studentId, String semesterCode);


    @Query("SELECT p FROM Parallel p " +
            "JOIN p.students s " +
            "WHERE s.userName = :studentUsername " +
            "AND p.semester.code = :semesterCode")
    List<Parallel> findAllByStudents_UsernameAndSemester_Code(String studentUsername, String semesterCode);




    @Query("select p from Parallel p where p.course.id = ?1 and p.semester.startDate = ?2 and p.course.language = ?3")
    List<Parallel> testingFunciton(
            Long courseId,
            LocalDate startDate,
            Locale language
    );

    @Query("select p from Parallel p where p.course.id = ?1 and p.course.language = ?2")
    List<Parallel> testingFuncitonWithoutStartDate(
            Long courseId,

            Locale language
    );
}
