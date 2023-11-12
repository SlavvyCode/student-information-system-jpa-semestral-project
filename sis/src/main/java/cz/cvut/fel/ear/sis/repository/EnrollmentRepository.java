package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {


    @Query("SELECT SUM(e.parallel.course.ECTS) FROM Enrollment e " +
            "WHERE e.student = :student " +
            "AND e.parallel.semester = :semester")
    Integer getTotalECTSCreditsForStudentThisSemester(@Param("student") Student student,
                                                      @Param("semester") Semester semester);

}
