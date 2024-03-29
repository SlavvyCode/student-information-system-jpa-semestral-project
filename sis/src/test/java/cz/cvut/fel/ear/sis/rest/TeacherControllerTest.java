package cz.cvut.fel.ear.sis.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import cz.cvut.fel.ear.sis.dao.environment.Environment;
import cz.cvut.fel.ear.sis.model.*;
import cz.cvut.fel.ear.sis.model.Enrollment;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.*;
import cz.cvut.fel.ear.sis.utils.exception.CourseException;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


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

    private CourseRepository courseRepository;

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
    @WithMockUser(roles = {"TEACHER"},username = "user1234")
    public void listMyCoursesReturnsAllCoursesForTeacher() throws Exception {

            Person mockTeacher = new Teacher();
            mockTeacher.setId(1L); // Set a sample ID

            mockTeacher.setUserName("user1234");
            mockTeacher.setFirstName("Jirka");
            mockTeacher.setLastName("Velebil");

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

        when(teacherServiceMock.getCoursesByTeacherUsername(eq(mockTeacher.getUserName()))).thenReturn(Collections.singletonList(mockCourse));


        // Act
        // Perform the request
        final MvcResult mvcResult = mockMvc.perform(get("/teacher/course"))
                .andExpect(status().isOk())
                .andReturn();

        verify(teacherServiceMock).getCoursesByTeacherUsername(eq(mockTeacher.getUserName()));


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
    public void gradeStudentReturnsNoContentForSuccessfulGradeButAddsGradeToStudent() throws Exception {


        Person mockTeacher = new Teacher();
        mockTeacher.setId(1L);
        mockTeacher.setFirstName("Jirka");
        mockTeacher.setLastName("Velebil");



        // Create a mock course object
        Course mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setName("Math");
        mockCourse.setTeacher((Teacher) mockTeacher);

        // Create a mock parallel object
        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);
        mockParallel.setCourse(mockCourse);

        // Create a mock student object
        Person mockStudent = new Student();
        mockStudent.setId(1L);

        Enrollment mockEnrollment = new Enrollment();
        mockEnrollment.setId(1L);
        mockEnrollment.setParallel(mockParallel);
        mockEnrollment.setStudent((Student) mockStudent);


        ((Student) mockStudent).addEnrollment(mockEnrollment);

        mockParallel.addEnrollment(mockEnrollment);
        mockParallel.addStudent((Student) mockStudent);

        //etc etc continue defining relationships if necessary



        // Define the behavior of the teacherServiceMock to return the mockEnrollment when gradeStudent is called
        doAnswer(invocation -> {
            mockEnrollment.setGrade(Grade.E);
            return mockEnrollment;
        }).when(teacherServiceMock).gradeStudent(eq(mockParallel.getId()), eq(mockStudent.getId()), eq(Grade.E));



        when(teacherServiceMock.getEnrollmentByParallelIdAndStudentId(eq(mockParallel.getId()), eq(mockStudent.getId()))).thenReturn(mockEnrollment);

        when(teacherServiceMock.getTeacherByUsername(anyString())).thenReturn((Teacher) mockTeacher);

        // Perform the request with the grade as a JSON string
        mockMvc.perform(post("/teacher/grade/{parallelId}/{studentId}", mockParallel.getId(), mockStudent.getId())
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("E")) // Grade passed as a JSON string with quotes
                .andExpect(status().isNoContent())
                .andReturn();


        // Verify that the gradeStudent method was called with the expected parallelId, studentId and grade
        verify(teacherServiceMock).gradeStudent(eq(mockParallel.getId()), eq(mockStudent.getId()), eq(Grade.E));

        // Verify that the grade was added to the student
        assertEquals(Grade.E, mockEnrollment.getGrade(), "The grade was not added to the student.");







    }


    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void createParallelReturnsCreatedForValidParallel() throws Exception {
        // Mock data
        Person mockTeacher = new Teacher();
        mockTeacher.setId(1L);

        Course mockCourse = new Course();
        mockCourse.setId(1L);
        mockCourse.setTeacher((Teacher) mockTeacher);

        courseRepository.save(mockCourse);

        when(teacherServiceMock.getCourseById(anyLong())).thenReturn(Optional.of(mockCourse));

        Parallel mockParallel = new Parallel();
        mockParallel.setId(1L);
        mockParallel.setCourse(mockCourse);

        // Mock service response
        when(teacherServiceMock.createParallel(eq(mockTeacher.getId()), anyInt(), any(TimeSlot.class), any(DayOfWeek.class),
                anyLong(), anyLong(), eq(mockCourse.getId()))).thenReturn(mockParallel);

//        teacherService.getTeacherByUsername(user.getUsername()).getId()
        when(teacherServiceMock.getTeacherByUsername(anyString())).thenReturn((Teacher) mockTeacher);

//        createParallel(@PathVariable Long courseId,
//                                                   @RequestParam int capacity, @RequestParam String timeSlot,
//                                                   @RequestParam String dayOfWeek, @RequestParam long semesterId,
//                                                   @RequestParam long classroomId)

        mockMvc.perform(post("/teacher/parallel/{courseId}", mockCourse.getId())
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("capacity", "30")
                        .param("timeSlot", "SLOT1")
                        .param("dayOfWeek", "MON")
                        .param("semesterId", "123")
                        .param("classroomId", "456"))
                .andExpect(status().isCreated())
                .andReturn();


        // Verify that the createParallel method was called with the expected parameters
        verify(teacherServiceMock).createParallel(eq(mockTeacher.getId()), eq(30), eq(TimeSlot.SLOT1),
                eq(DayOfWeek.MON), eq(123L), eq(456L), eq(mockCourse.getId()));
    }

    @Test
    @WithMockUser(roles = {"TEACHER"})
    public void createCourseTestCreatesCourse() throws Exception {
        Person mockTeacher = new Teacher();
        mockTeacher.setId(1L);

        // Mock service response
        when(teacherServiceMock.createCourse(eq(mockTeacher.getId()), anyString(), anyString(), anyInt(), any(Locale.class))).thenReturn(new Course());
        when(teacherServiceMock.getTeacherByUsername(anyString())).thenReturn((Teacher) mockTeacher);

        // Perform the request

        mockMvc.perform(post("/teacher/course")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .param("courseName", "Math")
                        .param("code", "MATH123")
                        .param("ECTS", "5")
                        .param("language", "en"))
                .andExpect(status().isCreated())
                .andReturn();

        // Verify that the createCourse method was called with the expected parameters
        verify(teacherServiceMock).createCourse(eq(mockTeacher.getId()), eq("Math"), eq("MATH123"), eq(5), eq(Locale.ENGLISH));



    }
}
