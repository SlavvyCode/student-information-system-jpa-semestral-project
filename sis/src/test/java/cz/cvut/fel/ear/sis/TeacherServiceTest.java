package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.Role;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TeacherServiceTest {



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
    PersonService personService;

    @Autowired
    TeacherService teacherService;
    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);



    //test all methods of teacher service

    //test createCourse
    //test createParallel


    @Test
    @Transactional
    public void createCourseAndParallelTest() throws PersonException, CourseException, CourseException, ParallelException, SemesterException, ClassroomException {
        //create teacher
        Person teacher = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "teacherKeyPass");

        String courseName = "Math";
        String courseCode = "MATH123";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;
        Course course = teacherService.
                createCourse(teacher.getId(), courseName, courseCode, ECTS, language);


        SemesterType semesterType = SemesterType.SPRING;
        int year = 2024;

        Semester semester = new Semester(year, semesterType);

//        Semester semester = new Semester(year, semesterType);
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


        //assert if parallel is in course

        assertTrue(parallel.getCourse().getParallelsList().contains(parallel));
        //check if parallel is in teacher
        assertSame(parallel.getCourse().getTeacher(), teacher);

    }


    //checkCourseDetailsTest
    @Test
    @Transactional
    public void checkCourseAndParallelDetailsTest() throws PersonException, CourseException, ParallelException {

        Person teacher = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "teacherKeyPass");

        long badTeacherId = 999999999999999999L;
        String courseName = "Math";
        String courseCode = "MATH123";

        int badECTS1 = -5;
        int badECTS2 = 355;

        String badCourseName = ")*(^&@#%(& I AM A HACKER :) 9098)!)@(%&!)@(%Y!)@(%&)@(!%&)(!@%&";
        String badCourseCode = "MATH123*!!@#!!%@^$&%&*";

        Locale badLanguage = Locale.FRENCH;

        int ECTS = 5;
        Locale language = Locale.ENGLISH;

        //bad inputs throw

        Assertions.assertThrows(PersonException.class, () -> {
            teacherService.createCourse(badTeacherId, courseName, courseCode, ECTS, language);
        });
        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createCourse(teacher.getId(), badCourseName, courseCode, ECTS, language);
        });
        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createCourse(teacher.getId(), courseName, badCourseCode, ECTS, language);
        });


        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createCourse(teacher.getId(), courseName, courseCode, badECTS1, language);
        });
        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createCourse(teacher.getId(), courseName, courseCode, badECTS2, language);
        });

        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createCourse(teacher.getId(), courseName, courseCode, ECTS, badLanguage);
        });



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



        //bad inputs

        int badParallelCapacity1 = -5;
        int badParallelCapacity2 = classroomCapacity + 1;
        int badParallelCapacity3 = 0;

        long badSemesterId = 999999999999999999L;
        long badClassroomId = 999999999999999999L;
        long badCourseId = 999999999999999999L;

        //multiple classrooms
        Classroom classroom2 = new Classroom("T9:124", classroomCapacity);

        classroomRepository.save(classroom2);


        //assert bad teacher id
        Assertions.assertThrows(PersonException.class, () -> {
            teacherService.createParallel
                    (badTeacherId, parallelCapacity, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), course.getId());
        });
        Assertions.assertThrows(ParallelException.class, () -> {
            teacherService.createParallel(teacher.getId(), badParallelCapacity1, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), course.getId());
        });

        Assertions.assertThrows(ParallelException.class, () -> {
            teacherService.createParallel(teacher.getId(), badParallelCapacity2, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), course.getId());
        });

        Assertions.assertThrows(ParallelException.class, () -> {
            teacherService.createParallel(teacher.getId(), badParallelCapacity3, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), course.getId());
        });

        Assertions.assertThrows(SemesterException.class, () -> {
            teacherService.createParallel(teacher.getId(), parallelCapacity, timeSlot, dayOfWeek, badSemesterId, classroom.getId(), course.getId());
        });

        Assertions.assertThrows(ClassroomException.class, () -> {
            teacherService.createParallel(teacher.getId(), parallelCapacity, timeSlot, dayOfWeek, semester.getId(), badClassroomId, course.getId());
        });

        Assertions.assertThrows(CourseException.class, () -> {
            teacherService.createParallel(teacher.getId(), parallelCapacity, timeSlot, dayOfWeek, semester.getId(), classroom.getId(), badCourseId);
        });

    }
}
