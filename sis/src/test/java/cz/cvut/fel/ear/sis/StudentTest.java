package cz.cvut.fel.ear.sis;

import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.service.PersonService;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@SpringBootTest
@Transactional
public class StudentTest {


    @Autowired
    PersonService personService;

    @MockBean
    PersonRepository personRepository;
    @MockBean
    StudentRepository studentRepository;


    @Test
    public void studentTestLong() throws PersonException {

        Person student1 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.of(2000,3,3), "jnovak", "studentKeyPass");


        personService.getAllStudent();
        Assertions.assertEquals(1, personService.getAllStudent().size());
        Assertions.assertEquals(student1, personService.getAllStudent().get(0));

        //check that student1 is a student
        Assertions.assertEquals("Student", student1.getClass().getName());


        //check same thing but through the repository
        Assertions.assertTrue(studentRepository.existsById(student1.getId()));


        //check that student1's parameters all match
        Assertions.assertEquals("Jan", student1.getFirstName());
        Assertions.assertEquals("Novak", student1.getLastName());
        Assertions.assertEquals("jn1@fel.cz", student1.getEmail());
        Assertions.assertEquals("1254456789", student1.getPhoneNumber());
        Assertions.assertEquals(LocalDate.of(2000,3,3), student1.getBirthDate());
        Assertions.assertEquals("JanNovak", student1.getUserName());
        Assertions.assertEquals("jnovak", student1.getPassword());


        //try to insert the same person again and expect a throw
        Assertions.assertThrows(PersonException.class, () -> {
            personService.createANewPerson("Jan", "Novak", "jn1@fel.cz", "1254456789", LocalDate.now(), "jnovak", "studentKeyPass");
        });


    }
}
