package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Admin;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.model.Teacher;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    @Autowired
    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public void createANewPerson(String firstName, String lastName, String email, String phoneNumber,
                                 LocalDate birthDate, String password, String roleKeypass) throws PersonException {
        // inspect input for validity
        checkThatDetailsAreValid(firstName, lastName, email, phoneNumber, birthDate, password);

        // create username
        String userName = generateUniqueUserName(firstName, lastName);

        // createTheRightTypeOfUser
        Person person = switch (roleKeypass) {
            case "studentKeyPass" -> new Student(firstName, lastName, email, phoneNumber, birthDate, userName, password);
            case "teacherKeyPass" -> new Teacher(firstName, lastName, email, phoneNumber, birthDate, userName, password);
            case "adminKeyPass" -> new Admin(firstName, lastName, email, phoneNumber, birthDate, userName, password);
            default -> throw new PersonException("KeyPass is not valid");
        };
        personRepository.save(person);
    }

    @Transactional
    public void updateContactDetails(Long id, String email, String phoneNumber) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatContactDetailsAreValid(email, phoneNumber);
        person.setEmail(email);
        person.setPhoneNumber(phoneNumber);
        personRepository.save(person);
    }

    @Transactional
    public void updateNameAndUsername(Long id, String firstName, String lastName) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatNameIsValid(firstName, lastName);
        person.setFirstName(firstName);
        person.setLastName(lastName);
        person.setUserName(generateUniqueUserName(firstName, lastName));
        personRepository.save(person);
    }

    @Transactional(readOnly = true)
    public List<Person> getAllPeople(){
        return personRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Long id){
        return personRepository.findById(id);
    }

    private String generateUniqueUserName(String firstName, String lastName){
        List<Person> existingUsersWithSameName = personRepository.findByUserNameEndingWith(firstName+lastName);
        int maxNumber = existingUsersWithSameName.stream()
                .mapToInt(user -> {
                    String username = user.getUserName().replace(firstName+lastName, "");
                    return username.isEmpty() ? 0 : Integer.parseInt(username);
                })
                .max()
                .orElse(0);
        return ++maxNumber + firstName + lastName;
    }

    private void checkThatDetailsAreValid(String firstName, String lastName, String email,
                                         String phoneNumber, LocalDate birthDate, String password) throws PersonException {
        checkThatNameIsValid(firstName, lastName);
        checkThatContactDetailsAreValid(email, phoneNumber);
        if (Period.between(LocalDate.now(), birthDate).getYears() < 18)
            throw new PersonException("Only users 18 years old and older can sign up.");
        if (doesNotConformRegex(password, ""))
            throw new PersonException("Password is not valid.");
    }

    private void checkThatNameIsValid(String firstName, String lastName) throws PersonException{
        if (doesNotConformRegex(firstName, "\"[a-zA-Z]+\""))
            throw new PersonException("First name is not valid");
        if (doesNotConformRegex(lastName, "\"[a-zA-Z]+\""))
            throw new PersonException("Last name is not valid");
    }

    private void checkThatContactDetailsAreValid(String email, String phoneNumber) throws PersonException{
        if (doesNotConformRegex(email, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new PersonException("Email is not valid.");
        if (personRepository.existsByEmail(email))
            throw new PersonException("Account with that email already exists.");
        if (doesNotConformRegex(phoneNumber, "^\\+?(\\d[\\d -]{7,12}\\d)$\n"))
            throw new PersonException("Phone number is not valid.");
        if (personRepository.existsByPhoneNumber(phoneNumber))
            throw new PersonException("Account with that phone number already exists.");
    }

    private boolean doesNotConformRegex(String input, String regexPattern) {
        return input == null || !input.matches(regexPattern);
    }

}

