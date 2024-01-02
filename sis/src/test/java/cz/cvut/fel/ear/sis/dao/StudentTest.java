package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.*;
import jakarta.persistence.EntityManager;
import org.h2.server.Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class StudentTest {


    @Autowired
    ParallelRepository parallelRepository;
    @Autowired
    EntityManager em;

    @Autowired
    PersonService personService;
    @Autowired
    TeacherService teacherService;

    @Autowired
    ClassroomRepository classroomRepository;
    @Autowired
    SemesterRepository semesterRepository;


    @Autowired
    PersonRepository personRepository;
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    StudentService studentService;
    @Autowired
    AdminService adminService;
    @Autowired
    EnrollmentRepository enrollmentRepository;

    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);

    @Test
    @Transactional
    public void studentTestLong() throws PersonException {

        Person student1 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz",
                "1254456789", LocalDate.of(2000,3,3), "Abc12345",
                "studentKeyPass");


        personService.getAllStudents();
        Assertions.assertEquals(1, personService.getAllStudents().size());
        Assertions.assertEquals(student1, personRepository.findById(student1.getId()).get());

        //check that student1 is a student
        Assertions.assertEquals("Student", student1.getClass().getSimpleName());


        //check same thing but through the repository
        Assertions.assertTrue(studentRepository.existsById(student1.getId()));


        //check that student1's parameters all match
        Assertions.assertEquals("Jan", student1.getFirstName());
        Assertions.assertEquals("Novak", student1.getLastName());
        Assertions.assertEquals("jn1@fel.cz", student1.getEmail());
        Assertions.assertEquals("1254456789", student1.getPhoneNumber());
        Assertions.assertEquals(LocalDate.of(2000,3,3), student1.getBirthDate());
        Assertions.assertEquals("1JanNovak", student1.getUserName());
        Assertions.assertEquals("Abc12345", student1.getPassword());


        //try to insert the same person again and expect a throw
        Assertions.assertThrows(PersonException.class, () -> {
            personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
        });


    }


    @Test
    @Transactional
    public void checkIfStudentRepoReachesOtherUserRoles() throws PersonException {


        Person admin = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "adminKeyPass");
        Person student = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person teacher = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");

        studentRepository.findAll();
        Assertions.assertEquals(1, studentRepository.findAll().size());

    }




    ///CASCADE REMOVE TEST
    @Test
    @Transactional
    public void checkIfEnrollmentCascadeDeletesAlongWithStudent() throws PersonException, CourseException, SemesterException, ParallelException, ClassroomException, StudentException, EnrollmentException {
        //make new parallel teacher student and course and enroll the student then delete student and check if enrollmetn si missing

        Person admin = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "adminKeyPass");
        Person student = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person teacher = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");

        //make course and enroll student

        String courseName = "Math";
        String courseCode = "MATH123";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;
        Course course = teacherService.
                createCourse(teacher.getId(), courseName, courseCode, ECTS, language);
        SemesterType semesterType = SemesterType.SPRING;
        int year = 2024;
        Semester semester = new Semester(year, semesterType);

        semesterRepository.save(semester);

        int classroomCapacity = 30;
        Classroom classroom = new Classroom("T9:123", classroomCapacity);
        classroomRepository.save(classroom);

        DayOfWeek dayOfWeek = DayOfWeek.MON;
        TimeSlot timeSlot = TimeSlot.SLOT1;

        int parallelCapacity = 30;
        //create parallel
        Parallel parallel = teacherService.createParallel
                (teacher.getId(), parallelCapacity, timeSlot,dayOfWeek, semester.getId(), classroom.getId(), course.getId());

        Parallel parallel1 = teacherService.createParallel
                (teacher.getId(), parallelCapacity, timeSlot,DayOfWeek.THU, semester.getId(), classroom.getId(), course.getId());


        parallelRepository.save(parallel);
        parallelRepository.save(parallel1);


        //enroll student
        studentService.enrollToParallel(student.getId(), parallel.getId());

        Enrollment enrollment = enrollmentRepository.findByStudent_IdAndParallel_Id(student.getId(), parallel.getId());

        long enrollmentId = enrollment.getId();
        //check that student is enrolled
        Assertions.assertEquals(1, studentService.getMyEnrollments(student.getId()).size());

        //delete student

        em.refresh(student);
        em.refresh(parallel);


        em.flush();
        em.clear();
        adminService.deleteStudent(student.getId());


        //check that student is deleted
        Assertions.assertNull(studentRepository.findById(student.getId()).orElse(null));

        Assertions.assertNotNull(parallelRepository.findById(parallel.getId()));

        Assertions.assertNotNull(parallelRepository.findById(parallel1.getId()));

        Optional<Enrollment> enrollmentAfterDeletion = Optional.ofNullable(enrollmentRepository.findById(enrollmentId).orElse(null));

        Assertions.assertEquals(Optional.empty(),enrollmentAfterDeletion);
        //check that student is deleted
        Assertions.assertNull(studentRepository.findById(student.getId()).orElse(null));

        //check that enrollment is deleted
        Assertions.assertNull(enrollmentRepository.findByStudent_IdAndParallel_Id(student.getId(), parallel.getId()));

    }

}
