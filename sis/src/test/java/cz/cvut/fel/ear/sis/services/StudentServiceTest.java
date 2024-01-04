package cz.cvut.fel.ear.sis.services;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.Status;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Locale;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class StudentServiceTest {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    SemesterRepository semesterRepository;
    @Autowired
    ClassroomRepository classroomRepository;
    @Autowired
    ParallelRepository parallelRepository;
    @Autowired
    EnrollmentRepository enrollmentRepository;


    @Autowired
    PersonService personService;
    @Autowired
    AdminService adminService;
    @Autowired
    TeacherService teacherService;
    @Autowired
    StudentService studentService;
    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);




    @Test
    @Transactional
    public void createAndCancelEnrollmentToParallelTest() throws Exception {

        Person studentPerson = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person teacher  = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");

        Student student = (Student) studentPerson;

        Assertions.assertEquals(0, studentService.getMyEnrollments(student.getId()).size());



        String courseName = "Math";
        String courseCode = "MATH123";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;
        Course course = teacherService.
                createCourse(teacher.getId(), courseName, courseCode, ECTS, language);





        Semester currentSemester = new Semester(2024, SemesterType.SPRING);
        Semester nextSemester = new Semester(2024, SemesterType.FALL);

        adminService.setActiveSemester(currentSemester);

        semesterRepository.save(nextSemester);

        int classroomCapacity = 30;
        Classroom classroom = new Classroom("T9:123", classroomCapacity);

        classroomRepository.save(classroom);

        DayOfWeek dayOfWeek = DayOfWeek.MON;
        TimeSlot timeSlot = TimeSlot.SLOT1;
        int parallelCapacity = 30;


        //create parallel
        Parallel parallel = teacherService.createParallel
                (teacher.getId(), parallelCapacity, timeSlot, dayOfWeek, nextSemester.getId(), classroom.getId(), course.getId());


        //enroll student in parallel
        Enrollment enrollment = studentService.enrollToParallel(student.getId(), parallel.getId());



//check that student is enrolled in parallel
        assert (studentService.getMyEnrollments(student.getId()).contains(enrollment));

        //check that parallel has student enrolled
        assert (parallel.getStudents().contains(student));

        //check that student has enrollment in parallel
        assert (student.getMyEnrollments().contains(enrollment));

        //check that enrollment has student
        assert (enrollment.getStudent().equals(student));

        //check that enrollment has parallel
        assert (enrollment.getParallel().equals(parallel));

        //check that enrollment has status IN_PROGRESS
        assert (enrollment.getStatus().equals(Status.IN_PROGRESS));

        //check that enrollment has grade null
        assert (enrollment.getGrade() == null);



        //cancel enrollment
        studentService.dropFromParallel(student.getId(), parallel.getId());

        //check that student is not enrolled in parallel
        assert (!studentService.getMyEnrollments(student.getId()).contains(enrollment));

        //check that parallel doesn't have student enrolled
        assert (!parallel.getStudents().contains(student));

        //check that student doesn't have enrollment in parallel
        assert (student.getMyEnrollments().isEmpty());

        //check that enrollment doesn't exist
        assert (!enrollmentRepository.existsById(enrollment.getId()));


    }

    @Test
    @Transactional
    public void getMyEnrollmentsTest() throws Exception {
        Person studentPerson = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person teacher  = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");


        Student student = (Student) studentPerson;

        Assertions.assertEquals(0, studentService.getMyEnrollments(student.getId()).size());



        String courseName = "Math";
        String courseCode = "MATH123";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;
        Course course = teacherService.
                createCourse(teacher.getId(), courseName, courseCode, ECTS, language);






        Semester semester = new Semester(2024, SemesterType.FALL);
        semesterRepository.save(semester);

        int classroomCapacity = 30;
        Classroom classroom = new Classroom("T9:123", classroomCapacity);

        classroomRepository.save(classroom);

        DayOfWeek dayOfWeek = DayOfWeek.MON;
        TimeSlot timeSlot = TimeSlot.SLOT1;
        int parallelCapacity = 30;


        //create parallel
        Parallel parallel = teacherService.createParallel
                (teacher.getId(), parallelCapacity, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), course.getId());


        //enroll student in parallel
        Enrollment enrollment = studentService.enrollToParallel(student.getId(), parallel.getId());



        //check that student is enrolled in parallel
        assert (studentService.getMyEnrollments(student.getId()).contains(enrollment));

    }



}
