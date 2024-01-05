package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.repository.EnrollmentRepository;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;


@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class QueryTest {



    @Autowired
    private TestEntityManager em;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);


    // not relevant for grading
    @Test
    public void testGettingTotalECTSCreditsForStudentThisSemester() {

        Student student = new Student("Jan", "Novak", "jn@fel.cz", "123456789", ageOver18, "jnovak", "123456");
        Course course = new Course(null, "Matematika", "MAT", 5, Locale.ENGLISH);
        Classroom classroom = new Classroom("U1", 50);
        Semester semester = new Semester(2022, SemesterType.FALL);
        Parallel parallel = new Parallel(2, TimeSlot.SLOT1, DayOfWeek.FRI, semester, classroom, course);
        Enrollment enrollment = new Enrollment(parallel, student);

        em.persist(student);
        em.persist(course);
        em.persist(classroom);
        em.persist(semester);
        em.persist(parallel);
        em.persist(enrollment);

        List<Enrollment> enrollments = student.getMyEnrollments();
        enrollments.add(enrollment);
        student.setMyEnrollments(enrollments);
        enrollmentRepository.save(enrollment);
        em.persist(student);


        int recievedCreditNum = enrollmentRepository.getTotalECTSCreditsForStudentThisSemester(student, parallel.getSemester());

        Assertions.assertEquals(5, recievedCreditNum);

    }



}
