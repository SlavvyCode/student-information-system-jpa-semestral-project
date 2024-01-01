package cz.cvut.fel.ear.sis.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.rest.dto.CreateClassroomRequestBody;
import cz.cvut.fel.ear.sis.rest.dto.CreateSemesterRequestBody;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.ClassroomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminServiceMock;

    @InjectMocks
    private AdminController adminController;

    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(adminController)
                // Include any necessary exception handlers or advice
                .build();
    }

    @Test
    public void getClassrooms_ReturnsAllClassrooms() throws Exception {
        List<Classroom> classrooms = Arrays.asList(new Classroom("C101", 30), new Classroom("C102", 40));
        when(adminServiceMock.getAllClassrooms()).thenReturn(classrooms);

        mockMvc.perform(get("/rest/admin/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("C101"))
                .andExpect(jsonPath("$[0].capacity").value(30))
                .andExpect(jsonPath("$[1].code").value("C102"))
                .andExpect(jsonPath("$[1].capacity").value(40));

        verify(adminServiceMock).getAllClassrooms();
    }

    @Test
    public void createClassroom_CreatesClassroomSuccessfully() throws Exception {
        CreateClassroomRequestBody requestBody = new CreateClassroomRequestBody();
        requestBody.code = "C103";
        requestBody.capacity = 50;

        Classroom createdClassroom = new Classroom("C103", 50);
        when(adminServiceMock.createClassroom("C103", 50)).thenReturn(createdClassroom);

        mockMvc.perform(post("/rest/admin/classroom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/rest/admin/classroom/C103"));

        verify(adminServiceMock).createClassroom("C103", 50);
    }

    @Test
    public void getSemesters_ReturnsAllSemesters() throws Exception {
        List<Semester> semesters = Arrays.asList(new Semester(2023, SemesterType.FALL), new Semester(2023, SemesterType.SPRING));
        when(adminServiceMock.getAllSemesters()).thenReturn(semesters);

        mockMvc.perform(get("/rest/admin/semester"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].semesterType").value("FALL"))
                .andExpect(jsonPath("$[0].startDate").exists())
                .andExpect(jsonPath("$[0].endDate").exists())
                .andExpect(jsonPath("$[1].semesterType").value("SPRING"))
                .andExpect(jsonPath("$[1].startDate").exists())
                .andExpect(jsonPath("$[1].endDate").exists());

        verify(adminServiceMock).getAllSemesters();
    }


    @Test
    public void createSemester_CreatesSemesterSuccessfully() throws Exception {
        CreateSemesterRequestBody requestBody = new CreateSemesterRequestBody();
        requestBody.year = 2024;
        requestBody.semesterType = SemesterType.FALL;

        Semester createdSemester = new Semester(2024, SemesterType.FALL);
        when(adminServiceMock.createSemester(2024, SemesterType.FALL)).thenReturn(createdSemester);

        mockMvc.perform(post("/rest/admin/semester")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/rest/admin/semester/" + createdSemester.getCode()));

        verify(adminServiceMock).createSemester(2024, SemesterType.FALL);
    }

    @Test
    public void setActiveSemester_SetsActiveSemesterSuccessfully() throws Exception {
        int year = 2023;
        SemesterType semesterType = SemesterType.FALL;
        Semester semester = new Semester(year, semesterType);
        String semesterCode = semesterType.name() + year; // Auto-generated code

        when(adminServiceMock.getSemesterByCode(semesterCode)).thenReturn(Optional.of(semester));
        doNothing().when(adminServiceMock).setActiveSemester(semester);

        mockMvc.perform(patch("/rest/admin/semester/{code}", semesterCode))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "http://localhost/rest/admin/semester/" + semesterCode));

        verify(adminServiceMock).getSemesterByCode(semesterCode);
        verify(adminServiceMock).setActiveSemester(semester);
    }

}