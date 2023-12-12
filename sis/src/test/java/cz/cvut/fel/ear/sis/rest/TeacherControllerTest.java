package cz.cvut.fel.ear.sis.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.fel.ear.sis.dao.environment.Environment;
import cz.cvut.fel.ear.sis.dao.environment.TestDataGenerator;
import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.Grade;
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
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import cz.cvut.fel.ear.sis.model.Parallel;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest
@ContextConfiguration(
        classes = {TeacherControllerTest.TestConfig.class,
                SecurityConfig.class})

public class TeacherControllerTest extends BaseControllerTestRunner{
    @Mock
    private TeacherService teacherServiceMock;



    private MockMvc mockMvc;

    @Autowired
    private static TeacherService teacherService;

    private Person teacher;

    @BeforeEach
    public void setUp() {
        this.objectMapper = Environment.getObjectMapper();
        this.teacher = TestDataGenerator.generateTeacher();
    }

    @AfterEach
    public void tearDown() {
        Environment.clearSecurityContext();
        Mockito.reset(teacherService);
    }



    @Configuration
    @TestConfiguration
    public static class TestConfig {

        @MockBean
        private PersonService personService;

        @Bean
        public TeacherController teacherController() {
            return new TeacherController(teacherService);
        }
    }




    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void listMyCoursesReturnsAllCoursesForTeacher() throws Exception {
        // Mock data
        final List<Course> courses = IntStream.range(0, 5)
                .mapToObj(i -> TestDataGenerator.generateCourse())
                .collect(Collectors.toList());

        // Mock service response
        when(teacherServiceMock.getCourseByTeacherId(any())).thenReturn(courses);

        // Perform the request with authentication
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/course"))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response
        final List<Course> result = readValue(mvcResult, new TypeReference<List<Course>>() {});
        assertNotNull(result);
        assertEquals(courses.size(), result.size());
    }


    @Test
    public void listParallelsForCourseReturnsParallelsForGivenCourseId() throws Exception {
        // Mock data
        final List<Parallel> parallels = IntStream.range(0, 3)
                .mapToObj(i -> TestDataGenerator.generateParallel())
                .collect(Collectors.toList());

        // Mock service response
        when(teacherServiceMock.getParallelByCourseId(any())).thenReturn(parallels);

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/rest/teacher/parallel/{courseId}", 123L))
                .andExpect(status().isOk())
                .andReturn();

        // Verify the response
        final List<Parallel> result = readValue(mvcResult, new TypeReference<List<Parallel>>() {});
        assertNotNull(result);
        assertEquals(parallels.size(), result.size());
        // Add more assertions as needed for the Parallel objects
    }

    @Test
    public void listStudentsForParallelReturnsStudentsForGivenParallelId() throws Exception {
        // Similar structure to the previous tests
        // Mock data, mock service response, perform request, verify response
    }

    @Test
    public void gradeStudentReturnsNoContentForSuccessfulGrade() throws Exception {
        // Mock data
        final Grade grade = Grade.A;

        // Perform the request
        mockMvc.perform(post("/rest/teacher/grade/{parallelId}/{studentId}", 456L, 789L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(grade)))
                .andExpect(status().isNoContent());

        // Verify service interaction
        verify(teacherServiceMock).gradeStudent(456L, 789L, grade);
    }

    @Test
    public void createCourseReturnsCreatedForValidCourse() throws Exception {
        // Mock data
        final Course course = TestDataGenerator.generateCourse();

        // Mock service response
        when(teacherServiceMock.createCourse(any(), any(), any(), any(), any())).thenReturn(course);

        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(post("/rest/teacher/course")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("courseId", "123")
                        .param("courseName", "Sample Course")
                        .param("code", "CS101")
                        .param("ECTS", "5")
                        .param("language", "en"))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify the response
        verifyLocationEquals("/rest/teacher/course/123", mvcResult);
    }

}
