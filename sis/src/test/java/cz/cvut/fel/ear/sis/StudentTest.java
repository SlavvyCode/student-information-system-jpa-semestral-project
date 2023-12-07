package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.h2.server.Service;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager

@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class StudentTest {


    @Autowired
    PersonService personService;

    @Autowired
    PersonRepository personRepository;
    @Autowired
    StudentRepository studentRepository;

    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);

    @Test
    @Transactional
    public void studentTestLong() throws PersonException {

        Person student1 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz",
                "1254456789", LocalDate.of(2000,3,3), "Abc12345",
                "studentKeyPass");


        personService.getAllStudents();
        Assertions.assertEquals(1, personService.getAllStudents().size());
        Assertions.assertEquals(student1, personRepository.findById(student1.getId()).get());

        //check that student1 is a student
        Assertions.assertEquals("Student", student1.getClass().getSimpleName());


        //check same thing but through the repository
        Assertions.assertTrue(studentRepository.existsById(student1.getId()));


        //check that student1's parameters all match
        Assertions.assertEquals("Jan", student1.getFirstName());
        Assertions.assertEquals("Novak", student1.getLastName());
        Assertions.assertEquals("jn1@fel.cz", student1.getEmail());
        Assertions.assertEquals("1254456789", student1.getPhoneNumber());
        Assertions.assertEquals(LocalDate.of(2000,3,3), student1.getBirthDate());
        Assertions.assertEquals("1JanNovak", student1.getUserName());
        Assertions.assertEquals("Abc12345", student1.getPassword());


        //try to insert the same person again and expect a throw
        Assertions.assertThrows(PersonException.class, () -> {
            personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
        });


    }


    @Test
    @Transactional
    public void checkIfStudentRepoReachesOtherUserRoles() throws PersonException {


        Person admin = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "adminKeyPass");
        Person student = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person teacher = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");

        studentRepository.findAll();
        Assertions.assertEquals(1, studentRepository.findAll().size());

    }

}
