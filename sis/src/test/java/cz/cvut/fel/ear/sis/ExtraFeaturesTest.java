package cz.cvut.fel.ear.sis;

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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class ExtraFeaturesTest {



    @Autowired
    PersonService personService;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;

    @Autowired
    TeacherService teacherService;
//
    @Autowired
    CourseRepository courseRepository;

    @Autowired
    StudentService studentService;

    @Autowired
    SemesterRepository semesterRepository;

    @Autowired
    AdminService adminService;

    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);

    @Test
    @Transactional
    public void OrderedCourseTest() throws PersonException, CourseException {

        // add three ordered courses and test if they're returned by ascending order
        //
        // add three new courses with different names change their name and do it again
        Person teacher = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");


        Person teacher2 = personService.createANewPerson("Petr", "Fifka", "velebil2@fel.cz", "12368878228", ageOver18, "Jnovak125984", "teacherKeyPass");


        String courseName1 = "AAAA";
        String courseName2 = "BBBB";
        String courseName3 = "CCCC";


        String courseCode1 = "MATH1";
        String courseCode2 = "MATH2";
        String courseCode3 = "MATH3";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;



        Course course = teacherService.createCourse(teacher.getId(), courseName1, courseCode1, ECTS, language);

        Course course2 = teacherService.createCourse(teacher.getId(), courseName2, courseCode2, ECTS, language);

        Course course3 = teacherService.createCourse(teacher.getId(), courseName3, courseCode3, ECTS, language);


        courseRepository.save(course);
        courseRepository.save(course2);
        courseRepository.save(course3);


        List<Course> courseList = courseRepository.findAllByTeacher_Id(teacher.getId());

        assertEquals(courseList.get(0).getName(), courseName1);
        assertEquals(courseList.get(1).getName(), courseName2);
        assertEquals(courseList.get(2).getName(), courseName3);


        String courseName4 = "XXXX";
        String courseName5 = "YYYY";
        String courseName6 = "ZZZZ";


        String courseCode4 = "MATH4";
        String courseCode5 = "MATH5";
        String courseCode6 = "MATH6";

        Course course4 = teacherService.createCourse(teacher.getId(), courseName4, courseCode4, ECTS, language);
        Course course5 = teacherService.createCourse(teacher.getId(), courseName5, courseCode5, ECTS, language);
        Course course6 = teacherService.createCourse(teacher.getId(), courseName6, courseCode6, ECTS, language);


        courseRepository.save(course4);
        courseRepository.save(course5);
        courseRepository.save(course6);

        List<Course> courseList2 = courseRepository.findAllByTeacher_Id(teacher.getId());

        assertEquals(courseList2.get(0).getName(), courseName1);
        assertEquals(courseList2.get(1).getName(), courseName2);
        assertEquals(courseList2.get(2).getName(), courseName3);




    }



    @Test
    @Transactional
    public void CriteriaAPITest() throws PersonException, CourseException, SemesterException, ParallelException, ClassroomException {
        //make multiple courses with a parallel

        Locale language = Locale.ENGLISH;


        Person teacher = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");

        Person teacher2 = personService.createANewPerson("Petr", "Fifka", "veleb13il@fel.cz", "123688132788", ageOver18, "Jva2k125984", "teacherKeyPass");

        String courseName1 = "AAAA";
        String courseName2 = "BBBB";

        String courseCode1 = "MATH1";
        String courseCode2 = "MATH2";
        int ECTS = 5;

        Course course = teacherService.createCourse(teacher.getId(), courseName1, courseCode1, ECTS, language);
        Course course2 = teacherService.createCourse(teacher2.getId(), courseName2, courseCode2, ECTS, language);

        courseRepository.save(course);
        courseRepository.save(course2);

        Classroom classroom = adminService.createClassroom("T9:100", 100);

        //put one course into one semester other in other semester



        Semester activeSemester = adminService.createSemester(2024, SemesterType.FALL);

        adminService.setActiveSemester(activeSemester);

        Semester nextSemester = adminService.createSemester(2025, SemesterType.SPRING);




        Parallel parallelNextSemester = teacherService.createParallel(teacher.getId(), classroom.getCapacity()-1, TimeSlot.SLOT1,  DayOfWeek.MON,
                nextSemester.getId(), classroom.getId(),course.getId());



        List<Parallel> foundParallels = studentService.getParallelsFromCourseNextSemesterWhereLanguageIsChosen(course.getId(), String.valueOf(language));


        assertEquals(foundParallels.size(), 1);
        assertEquals(foundParallels.get(0).getId(), parallelNextSemester.getId());



    }


}
