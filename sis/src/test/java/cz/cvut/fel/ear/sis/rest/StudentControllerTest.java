package cz.cvut.fel.ear.sis.rest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.fel.ear.sis.config.SecurityConfig;
import cz.cvut.fel.ear.sis.dao.environment.Environment;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.exception.rest.NotStudentException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

@WebMvcTest
@ContextConfiguration(
        classes = {StudentControllerTest.TestConfig.class,
                SecurityConfig.class})
public class StudentControllerTest extends BaseControllerTestRunner {


        //gets the object mapper from the TestConfig
        @Autowired
        private StudentService studentServiceMock;


        @Autowired
        private MockMvc mockMvc;


        @MockBean
        private PersonService personService;

        @MockBean
        private TeacherRepository teacherRepository;

        @MockBean

        private CourseRepository courseRepository;

        @MockBean
        private SemesterRepository semesterRepository;

        @MockBean
        private ClassroomRepository classroomRepository;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private EntityManager entityManager;

//    private Person teacher;

        @BeforeEach
        public void setUp() {
            this.objectMapper = Environment.getObjectMapper();
//        this.teacher = TestDataGenerator.generateTeacher();
        }

        @AfterEach
        public void tearDown() {
            Environment.clearSecurityContext();
            Mockito.reset(studentServiceMock);
        }



        @Configuration
        @TestConfiguration
        public static class TestConfig {

            @MockBean
            private StudentService studentService;

            @MockBean
            private PersonRepository personRepository;



            @Bean
            public StudentController studentController() {
                return new StudentController(studentService);
            }
//
//            @Bean
//            public PlatformTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
//                return new JpaTransactionManager(entityManagerFactory);
//            }
        }





    // Test1
    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void listCoursesForNextSemesterReturnsListOfCourses() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");

        Course mockCourse = new Course();
        mockCourse.setId(1L);

        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);


        when(studentServiceMock.getAllParallelsForNextSemester()).thenReturn(Collections.singletonList(mockParallel));

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/student/course"))
                .andExpect(status().isOk())
                .andReturn();

        verify(studentServiceMock).getAllParallelsForNextSemester();


        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Parallels returned does not match the expected size.");
        assertEquals(result.get(0).getId(), mockParallel.getId(), "The Parallel returned does not match the expected Parallel.");
    }

    // Test2
    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void listCoursesForNextSemesterReturnsAllCoursesForStudent() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");

        Parallel mockParallel = new Parallel();

        mockParallel.setId(1L);

        // Define the behavior of the studentServiceMock to return a list containing mockParallel when getAllParallelsForNextSemester is called
        when(studentServiceMock.getAllParallelsForNextSemester()).thenReturn(Collections.singletonList(mockParallel));

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/student/course"))
                .andExpect(status().isOk())
                .andReturn();

        verify(studentServiceMock).getAllParallelsForNextSemester();

        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Parallels returned does not match the expected size.");
    }

    // Test3

    @Test
    @WithMockUser(roles = {"STUDENT"},username = "student1234")
    public void viewScheduleForSemesterReturnsScheduleForGivenSemester() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");
        mockStudent.setUserName("student1234");

        String semesterCode = "2023S";

        Parallel mockParallel = new Parallel();

        mockParallel.setId(1L);

        // Define the behavior of the studentServiceMock to return a list containing mockParallel when getAllEnrolledParallelsForNextSemester is called
        when(studentServiceMock.getAllEnrolledParallelsForNextSemester(eq(mockStudent.getId()), eq(semesterCode))).thenReturn(Collections.singletonList(mockParallel));

        when(studentServiceMock.getAllEnrolledParallelsForNextSemesterByStudentUsername(eq(mockStudent.getUserName()), eq(semesterCode))).thenReturn(Collections.singletonList(mockParallel));

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/student/schedule/{semesterCode}", semesterCode))
                .andExpect(status().isOk())
                .andReturn();

        verify(studentServiceMock).getAllEnrolledParallelsForNextSemesterByStudentUsername(eq(mockStudent.getUserName()),  eq(semesterCode));

        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Parallels returned does not match the expected size.");
    }




    // Test4
    @Test
    @WithMockUser(roles = {"STUDENT"}, username = "student1234")
    public void listEnrollmentReportReturnsReportForStudent() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");
        mockStudent.setUserName("student1234");

        Enrollment mockEnrollment = new Enrollment();
        mockEnrollment.setId(1L);

        // Define the behavior of the studentServiceMock to return a list containing mockEnrollment when getEnrollmentReport is called
        when(studentServiceMock.getEnrollmentReportByUsername(eq(mockStudent.getUserName()))).thenReturn(Collections.singletonList(mockEnrollment));

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/student/report"))
                .andExpect(status().isOk())
                .andReturn();

        verify(studentServiceMock).getEnrollmentReportByUsername(eq(mockStudent.getUserName()));

        // Verify the response
        final List<Enrollment> result = readValue(mvcResult, new TypeReference<List<Enrollment>>() {});
        assertNotNull(result);
        assertEquals(1, result.size(), "The number of Enrollments returned does not match the expected size.");
    }

    // Test5
    @Test
    @WithMockUser(roles = {"STUDENT"}, username = "student12345")
    public void enrollInParallelNextSemesterEnrollsStudent() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");
        mockStudent.setUserName("student12345");


        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);

        // Perform the request
        mockMvc.perform(post("/student/enroll/{parallelId}", mockParallel.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockStudent)))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(studentServiceMock).enrollToParallelByUsername(eq(mockStudent.getUserName()), eq(mockParallel.getId()));
    }

    // Test6
    @Test
    @WithMockUser(roles = {"STUDENT"}, username = "student1234")
    public void revertEnrollmentCancelsEnrollmentForStudent() throws Exception {

        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setUserName("student1234");
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");

        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);
        mockParallel.addStudent((Student) mockStudent);

        Enrollment mockEnrollment = new Enrollment(mockParallel, (Student) mockStudent);
        ((Student) mockStudent).addEnrollment(mockEnrollment);


        // Perform the request
        mockMvc.perform(delete("/student/enroll/{parallelId}", mockParallel.getId()))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(studentServiceMock).dropFromParallelByUsername(eq(mockStudent.getUserName()), eq(mockParallel.getId()));
    }

    // Test7
    @Test
    @WithMockUser(roles = {"STUDENT"})
    public void listParallelsForCourseNextSemesterWithCourseIdLanguageCriteriaAPITest() throws Exception {
        Person mockStudent = new Student();
        mockStudent.setId(1L);
        mockStudent.setFirstName("Jan");
        mockStudent.setLastName("Novak");

        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);
        mockParallel.addStudent((Student) mockStudent);

        Enrollment mockEnrollment = new Enrollment(mockParallel, (Student) mockStudent);
        ((Student) mockStudent).addEnrollment(mockEnrollment);

        Course mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setLanguage(Locale.ENGLISH);


//        when(mockCourse.getLanguage()).thenReturn(Locale.ENGLISH);
        when(studentServiceMock.getParallelsFromCourseNextSemesterWhereLanguageIsChosen(eq(mockStudent.getId()), eq(String.valueOf(mockCourse.getLanguage())))).thenReturn(Collections.singletonList(mockParallel));

        // Perform the request
        MvcResult result = mockMvc.perform(get("/student/parallel/{courseId}/{language}", mockParallel.getId(), mockCourse.getLanguage()))
                .andExpect(status().isOk())
                .andReturn();

        //check that result is list of parallels and that it contains the mockParallel
        List<Parallel> resultParallels = readValue(result, new TypeReference<List<Parallel>>() {});

        assertNotNull(resultParallels);
        assertEquals(1, resultParallels.size(), "The number of Parallels returned does not match the expected size.");
        assertEquals(resultParallels.get(0).getId(), mockParallel.getId(), "The Parallel returned does not match the expected Parallel.");
        verify(studentServiceMock).getParallelsFromCourseNextSemesterWhereLanguageIsChosen(eq(mockStudent.getId()), eq(String.valueOf(mockCourse.getLanguage())));

    }




    }
