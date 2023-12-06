package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    @Query("SELECT COALESCE(SUM(e.parallel.course.ECTS), 0) FROM Enrollment e " +
            "WHERE e.student = :student " +
            "AND e.parallel.semester = :semester")
    Integer getTotalECTSCreditsForStudentThisSemester(@Param("student") Student student,
                                                      @Param("semester") Semester semester);




    public List<Enrollment> findAllByStudent_IdAndParallel_Course_Id(long studentId, long parallelId);


    //check that student isn't already enrolled in a parallel at this time
    public Enrollment findByStudent_IdAndParallel_Semester_IdAndParallel_DayOfWeekAndParallel_TimeSlot(long studentId, long semesterId, DayOfWeek dayOfWeek, TimeSlot timeSlot);


    public List<Enrollment> findAllByStudent_IdAndParallel_Semester_Id(long studentId, long semesterId);



    public Enrollment findByStudent_IdAndParallel_Id(long studentId, long parallelId);

    //find all enrollments for a student in a semester, order them by parallel day of week and time slot, first by day of week, then by time slot
    //this is used to display the student's schedule

    //ordered query?
    public List<Enrollment> findAllByStudent_IdAndParallel_Semester_IdOrderByParallel_DayOfWeekAscParallel_TimeSlotAsc(long studentId, long semesterId);





}
