package cz.cvut.fel.ear.sis.services;

import cz.cvut.fel.ear.sis.model.Classroom;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Semester;
import cz.cvut.fel.ear.sis.repository.*;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.service.StudentService;
import cz.cvut.fel.ear.sis.service.TeacherService;
import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import cz.cvut.fel.ear.sis.utils.exception.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AdminServiceTest {


    @Autowired
    ClassroomRepository classroomRepository;
    @Autowired
    SemesterRepository semesterRepository;
    @Autowired
    AdminService adminService;

    @Test
    @Order(1)
    @Transactional
    public void createSemester_basic_verifyThatGettable() throws SemesterException {
        Semester semester = adminService.createSemester(2023, SemesterType.FALL);
        Assertions.assertEquals(semester, adminService.getSemesterByCode(semester.getCode()).get());
        Assertions.assertEquals(1, adminService.getAllSemesters().size());
    }

    @Test
    @Order(2)
    @Transactional
    public void createSemester_sameData_verifyThatThrows() throws SemesterException {
        adminService.createSemester(2023, SemesterType.FALL);
        Assertions.assertThrows(SemesterException.class, () -> {
            adminService.createSemester(2023, SemesterType.FALL);
        });
    }

    @Test
    @Order(3)
    @Transactional
    public void setActiveSemester_noneActiveBefore_verifyThatIsSetToActive() throws SemesterException {
        Semester semester = adminService.createSemester(2023, SemesterType.FALL);
        adminService.setActiveSemester(semester);
        Assertions.assertEquals(semester, adminService.getActiveSemester().get());
    }

    @Test
    @Order(4)
    @Transactional
    public void setActiveSemester_oneActiveBefore_verifyThatThePreviousHasBeenSetAsInactive() throws SemesterException {
        Semester previousActiveSemester = adminService.createSemester(2023, SemesterType.FALL);
        adminService.setActiveSemester(previousActiveSemester);
        Semester newlyActiveSemester = adminService.createSemester(2024, SemesterType.SPRING);
        adminService.setActiveSemester(newlyActiveSemester);
        Assertions.assertEquals(false, previousActiveSemester.getActive());
    }

    @Test
    @Order(5)
    @Transactional
    public void createClassroom_basic_verifyThatGettable() throws ClassroomException {
        Classroom classroom = adminService.createClassroom("A-209", 170);
        Assertions.assertEquals(classroom, adminService.getClassroomByCode("A-209").get());
        Assertions.assertEquals(1, adminService.getAllClassrooms().size());
    }

    @Test
    @Order(6)
    @Transactional
    public void createClassroom_duplicateCode_verifyThatThrows() throws ClassroomException {
        adminService.createClassroom("A-209", 170);
        Assertions.assertThrows(ClassroomException.class, () -> {
            adminService.createClassroom("A-209", 170);
        });
    }

    @Test
    @Order(7)
    @Transactional
    public void createClassroom_wrongCapacity_verifyThatThrows() throws ClassroomException {
        Assertions.assertThrows(ClassroomException.class, () -> {
            adminService.createClassroom("A-209", 201);
        });
    }
}