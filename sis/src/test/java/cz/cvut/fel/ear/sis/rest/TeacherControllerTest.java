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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static cz.cvut.fel.ear.sis.utils.Constants.AGE_OVER_18;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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

    //gets the object mapper from the TestConfig
    @Autowired
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

//    private Person teacher;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
//        this.teacher = TestDataGenerator.generateTeacher();
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





    //TEST1
    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listParallelsForCourseReturnsParallelsForGivenCourseId() throws Exception {


        Person mockTeacher = new Teacher();
        mockTeacher.setId(1L); // Set a sample ID
        mockTeacher.setFirstName("Jirka");
        mockTeacher.setLastName("Velebil");
        mockTeacher.setEmail("jv@fel.cz");
        // ... set other necessary fields

        // Define the behavior of the personService mock to return the mockTeacher when createANewPerson is called
        when(personService.createANewPerson(
                anyString(), anyString(), anyString(), anyString(), any(), anyString(), anyString()
        )).thenReturn(mockTeacher);

        // Create a mock course object
        Course mockCourse = new Course();
        mockCourse.setId(1L); // Set a sample ID
        mockCourse.setName("Math");
        mockCourse.setCode("MATH123");



//        public Course createCourse(long teacherId, String courseName, String code, int ECTS,Locale language)

        // Define the behavior of the teacherServiceMock to return the mockCourse when createCourse is called
        when(teacherServiceMock.createCourse(
                eq(mockTeacher.getId()), anyString(), anyString(), anyInt(), any(Locale.class)
        )).thenReturn(mockCourse);


        // Create a mock parallel object
        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L); // Set a sample ID


        // Define the behavior of the teacherServiceMock to return a list containing mockParallel when getParallelByCourseId is called
        when(teacherServiceMock.getParallelByCourseId(eq(mockCourse.getId()))).thenReturn(Collections.singletonList(mockParallel));

        // Act
        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/parallel/{courseId}", mockCourse.getId()))
                .andExpect(status().isOk())
                .andReturn();



        verify(teacherServiceMock).getParallelByCourseId(eq(mockCourse.getId()));

        // Assert
        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Parallels returned does not match the expected size.");
    }


    //TEST2
    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listMyCoursesReturnsAllCoursesForTeacher() throws Exception {

            Person mockTeacher = new Teacher();
            mockTeacher.setId(1L); // Set a sample ID
            mockTeacher.setFirstName("Jirka");
            mockTeacher.setLastName("Velebil");

            // Define the behavior of the personService mock to return the mockTeacher when createANewPerson is called
            when(personService.createANewPerson(
                    anyString(), anyString(), anyString(), anyString(), any(), anyString(), anyString()
            )).thenReturn(mockTeacher);

            // Create a mock course object
            Course mockCourse = new Course();
            mockCourse.setId(1L); // Set a sample ID
            mockCourse.setName("Math");
            mockCourse.setCode("MATH123");


            when(teacherServiceMock.createCourse(
                    eq(mockTeacher.getId()), anyString(), anyString(), anyInt(), any(Locale.class)
            )).thenReturn(mockCourse);


        // Define the behavior of the teacherServiceMock to return a list containing mockParallel when getParallelByCourseId is called
        when(teacherServiceMock.getCourseByTeacherId(eq(mockTeacher.getId()))).thenReturn(Collections.singletonList(mockCourse));


        // Act
        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/course"))
                .andExpect(status().isOk())
                .andReturn();

        verify(teacherServiceMock).getCourseByTeacherId(eq(mockTeacher.getId()));


        // Assert
        // Verify the response
        final List<Course> result = readValue(mvcResult, new TypeReference<List<Course>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Courses returned does not match the expected size.");


    }






    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listStudentsForParallelReturnsStudentsForGivenParallelId() throws Exception {
        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);

        List<Person> mockStudents = new ArrayList<>();
        Person mockStudent1 = new Student();
        mockStudent1.setId(1L);
        mockStudent1.setFirstName("Jan");
        mockStudent1.setLastName("Novak");
        mockStudents.add(mockStudent1);

        // Define the behavior of the teacherServiceMock to return the mockStudents when getStudentsByParallelId is called
        when(teacherServiceMock.getAllStudentsByParallelId(eq(mockParallel.getId())))
                .thenAnswer(invocation -> Collections.singletonList(mockStudent1));

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/students/{parallelId}", mockParallel.getId()))
                .andExpect(status().isOk())
                .andReturn();


        // Verify that the getStudentsByParallelId method was called with the expected parallelId
        verify(teacherServiceMock).getAllStudentsByParallelId(eq(mockParallel.getId()));

        // Verify the response
        final List<Student> result = readValue(mvcResult, new TypeReference<List<Student>>() {});
        assertNotNull(result, "The list of students should not be null.");
        assertEquals(mockStudents.size(), result.size(), "The number of Students returned does not match the expected size.");

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
