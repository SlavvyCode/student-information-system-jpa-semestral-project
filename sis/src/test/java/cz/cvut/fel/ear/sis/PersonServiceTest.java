package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.model.Admin;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.model.Teacher;
import cz.cvut.fel.ear.sis.repository.AdminRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.repository.TeacherRepository;
import cz.cvut.fel.ear.sis.service.AdminService;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.utils.enums.Role;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class PersonServiceTest {


    @Autowired
    PersonService personService;


    //todo mockbean or autowired
    @MockBean
    PersonRepository personRepository;
    @MockBean
    StudentRepository studentRepository;
    @MockBean
    TeacherRepository teacherRepository;
    @MockBean
    AdminRepository adminRepository;


    @Test
    @Transactional
    public void addingAllRolesTest() throws PersonException {
        Person person1 = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", LocalDate.now(), "jnovak", "adminKeyPass");
        Person person2 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
        Person person3 = personService.createANewPerson("Petr", "Fifka", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "teacherKeyPass");
        Assertions.assertEquals(3, personService.getAllPeople().size());
        Assertions.assertEquals(1, personService.getAllAdmins().size());
        Assertions.assertEquals(1, personService.getAllStudents().size());
        Assertions.assertEquals(1, personService.getAllTeachers().size());
        Assertions.assertEquals(Admin.class, person1.getClass());
        Assertions.assertEquals(Student.class, person2.getClass());
        Assertions.assertEquals(Teacher.class, person3.getClass());
        Assertions.assertEquals(Role.ADMIN, personService.getPersonRoleById(person1.getId()));
        Assertions.assertEquals(Role.STUDENT, personService.getPersonRoleById(person2.getId()));
        Assertions.assertEquals(Role.TEACHER, personService.getPersonRoleById(person3.getId()));

    }

//
//    @Test
//    @Transactional
//    public void addingSamePersonTwiceFails() throws PersonException {
//        //3 osoby -1 kazde role, pak getallpeople, check ze delka testu
//
//
//        personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
//        Assertions.assertThrows()
//
//        personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
//
//        Assertions.assertEquals(1, personService.getAllPeople().size());
//    }


}
