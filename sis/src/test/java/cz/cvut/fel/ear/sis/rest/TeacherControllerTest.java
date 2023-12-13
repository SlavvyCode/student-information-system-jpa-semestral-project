package cz.cvut.fel.ear.sis.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.fel.ear.sis.dao.environment.Environment;
import cz.cvut.fel.ear.sis.dao.environment.TestDataGenerator;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.ClassroomRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.SemesterRepository;
import cz.cvut.fel.ear.sis.repository.TeacherRepository;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.cvut.fel.ear.sis.utils.Constants.AGE_OVER_18;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import cz.cvut.fel.ear.sis.utils.Constants;


import cz.cvut.fel.ear.sis.config.SecurityConfig;
@WebMvcTest
@ContextConfiguration(
        classes = {TeacherControllerTest.TestConfig.class,
                SecurityConfig.class})

public class TeacherControllerTest extends BaseControllerTestRunner{
    @Mock
    private TeacherService teacherServiceMock;


    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private PersonService personService;

    @MockBean
    private TeacherRepository teacherRepository;


    @MockBean
    private SemesterRepository semesterRepository;

    @MockBean
    private ClassroomRepository classroomRepository;

    private Person teacher;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.teacher = TestDataGenerator.generateTeacher();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(teacherServiceMock);
    }



    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private TeacherService teacherService;

        @MockBean
        private PersonRepository personRepository;

        @Bean
        public TeacherController teacherController() {
            return new TeacherController(teacherService);
        }
    }





    //TEST2
    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listParallelsForCourseReturnsParallelsForGivenCourseId() throws Exception {
        // Mock data

        //create parallels and course
        //create teacher
        Person teacher = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", AGE_OVER_18, "Jnovak125984", "teacherKeyPass");

        String courseName = "Math";
        String courseCode = "MATH123";
        int ECTS = 5;
        Locale language = Locale.ENGLISH;
        Course course = teacherServiceMock.createCourse(teacher.getId(), courseName, courseCode, ECTS, language);


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
        Parallel parallel = teacherServiceMock.createParallel
                (teacher.getId(), parallelCapacity, timeSlot,dayOfWeek, semester.getId(), classroom.getId(), course.getId());


        List<Parallel> parallels = Collections.singletonList(parallel);

        // Mock service response
        when(teacherServiceMock.getParallelByCourseId(course.getId())).thenReturn(parallels);

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/parallel/{courseId}", course.getId()))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});

        assertNotNull(result);

        assertEquals(1, result.size(), "The number of Parallels returned does not match the expected size.");
    }


    @WithMockUser(roles = {"TEACHER"})
    @Test
    public void listMyCoursesReturnsAllCoursesForTeacher() throws Exception {
//        // Mock data
//
//        teacher.setRole(Role.TEACHER);
//        Environment.setCurrentUser(teacher);
//
//
//        final List<Course> courses = IntStream.range(0, 5)
//                .mapToObj(i -> TestDataGenerator.generateCourse())
//                .collect(Collectors.toList());
//
//        // Mock service response
//        when(teacherServiceMock.getCourseByTeacherId(any())).thenReturn(courses);
//
//        // Perform the request with authentication
//        final MvcResult mvcResult = mockMvc.perform(get("/teacher/course"))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        // Verify the response
//        final List<Course> result = readValue(mvcResult, new TypeReference<List<Course>>() {});
//        assertNotNull(result);
//        assertEquals(courses.size(), result.size());
    }






    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listStudentsForParallelReturnsStudentsForGivenParallelId() throws Exception {
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void gradeStudentReturnsNoContentForSuccessfulGrade() throws Exception {
//        ..
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void createCourseReturnsCreatedForValidCourse() throws Exception {
//
    }

}
