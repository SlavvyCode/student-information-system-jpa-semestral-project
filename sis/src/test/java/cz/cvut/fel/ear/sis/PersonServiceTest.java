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
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureTestEntityManager
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class PersonServiceTest {

    @Autowired
    PersonRepository personRepository;
    @Autowired
    StudentRepository studentRepository;
    @Autowired
    TeacherRepository teacherRepository;
    @Autowired
    AdminRepository adminRepository;

    @Autowired
    PersonService personService;


    private LocalDate ageOver18 = LocalDate.of(2000, 2, 2);

    @Test
    @Transactional
    public void addingAllRolesTest() throws PersonException {
        Person person1 = personService.createANewPerson("Jirka", "Velebil", "jv@fel.cz", "1254456789", ageOver18, "Jnovak125984", "adminKeyPass");
        Person person2 = personService.createANewPerson("Jan", "Novak", "jn4544@fel.cz", "123456789", ageOver18, "Jnovak125984", "studentKeyPass");
        Person person3 = personService.createANewPerson("Petr", "Fifka", "velebil@fel.cz", "123688788", ageOver18, "Jnovak125984", "teacherKeyPass");
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


    @Test
    @Transactional
    public void addingSamePersonTwiceThrows() throws PersonException {

        Person person1 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz",
                "1254456789", ageOver18, "jnovak12345H", "studentKeyPass");
        assertThrows(PersonException.class, () -> {
            Person person2 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz",
                    "1254456789", ageOver18, "jnovak12345H", "studentKeyPass");

        });

        Assertions.assertEquals(1, personService.getAllPeople().size());

        Assertions.assertTrue(personRepository.existsById(person1.getId()));

    }


    @Test
    @Transactional
    public void updateContactDetailsTest() throws PersonException {
        Person person1 = personService.createANewPerson("Jan", "Novak", "jn1@fel.cz",
                "1254456789", ageOver18, "jnovak12345H", "studentKeyPass");

        String newEmail = "Tigole@Bitties.co.uk";
        String newPhoneNumber = "3141592653";
        personService.updateContactDetails(person1.getId(), newEmail, newPhoneNumber);

        Assertions.assertEquals(newEmail, personRepository.findById(person1.getId()).get().getEmail());
        Assertions.assertEquals(newPhoneNumber, personRepository.findById(person1.getId()).get().getPhoneNumber());

        //changing details to bad values should throw
        assertThrows(PersonException.class, () -> {
            personService.updateContactDetails(person1.getId(), "badEmail", newPhoneNumber);
        });
        assertThrows(PersonException.class, () -> {
            personService.updateContactDetails(person1.getId(), newEmail, "badPhoneNumber");
        });


        //changing details to someone else's details should throw
        Person person2 = personService.createANewPerson("Faaafafafa", "afafafafa", "sasfasfasf@fel.cz",
                "132151365", ageOver18, "asfasfas225F", "studentKeyPass");
        assertThrows(PersonException.class, () -> {
            personService.updateContactDetails(person1.getId(), person2.getEmail(), person2.getPhoneNumber());
        });

    }


    @Test
    @Transactional
    public void updateNameAndUsernameTest() throws PersonException {
        Person person1 = personService.createANewPerson("Franta", "Omacka", "fomacka123@gm.com",
                "1254456789", ageOver18, "FFF2macka123", "studentKeyPass");

        Assertions.assertEquals("1FrantaOmacka", personRepository.findById(person1.getId()).get().getUserName());


        personService.updateNameAndUsername(person1.getId(), "Ferdinand", "Omackovnik");

        Assertions.assertEquals("Ferdinand", personRepository.findById(person1.getId()).get().getFirstName());
        Assertions.assertEquals("Omackovnik", personRepository.findById(person1.getId()).get().getLastName());
        Assertions.assertEquals("1FerdinandOmackovnik", personRepository.findById(person1.getId()).get().getUserName());


        //check that trying to input faulty data fails
        assertThrows(PersonException.class, () -> {
            personService.updateNameAndUsername(person1.getId(), "Ferdinand8984984%!@%!@#!^@&&",
                    "Ferdinand8984984%!@%!@#!^@&&");
        });


        //check that trying to input someone else's data DOESNT fail
        Person person2 = personService.createANewPerson("Faaafafafa", "afafafafa", "sasfasfasf@fel.cz",
                "132151365", ageOver18, "asfasfas225F", "studentKeyPass");



        assertDoesNotThrow(() -> {
            personService.updateNameAndUsername(person1.getId(), person2.getFirstName(), person2.getLastName());
        });

    }

    //czech symbols test
    @Test
    @Transactional
    public void czechSymbolsTest() throws PersonException {
        Person person1 = personService.createANewPerson("Fíěřžýáďrtiškaďovčák", "Omačěščřžýáíéččka", "fomacka123@gm.com",
                "1254456789", ageOver18, "FFF2macka123", "studentKeyPass");

        Assertions.assertEquals(1+"FíěřžýáďrtiškaďovčákOmačěščřžýáíéččka", personRepository.findById(person1.getId()).get().getUserName());

    }

}
