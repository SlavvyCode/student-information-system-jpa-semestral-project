package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.model.Admin;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import cz.cvut.fel.ear.sis.model.Teacher;
import cz.cvut.fel.ear.sis.repository.AdminRepository;
import cz.cvut.fel.ear.sis.repository.PersonRepository;
import cz.cvut.fel.ear.sis.repository.StudentRepository;
import cz.cvut.fel.ear.sis.repository.TeacherRepository;
import cz.cvut.fel.ear.sis.utils.enums.Role;
import cz.cvut.fel.ear.sis.utils.exception.PersonException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Optional;

import static cz.cvut.fel.ear.sis.utils.ServiceUtil.doesNotConformRegex;

@Service
public class PersonService {

    private final AdminRepository adminRepository;
    private final PersonRepository personRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    @Autowired
    public PersonService(AdminRepository adminRepository,
                         PersonRepository personRepository,
                         StudentRepository studentRepository,
                         TeacherRepository teacherRepository) {
        this.adminRepository = adminRepository;
        this.personRepository = personRepository;
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
    }

    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    @Transactional
    public Person createANewPerson(String firstName, String lastName, String email, String phoneNumber,
                                 LocalDate birthDate, String password, String roleKeypass) throws PersonException {
        // inspect input for validity
        checkThatDetailsAreValid(firstName, lastName, email, phoneNumber, birthDate, password);

        // create username
        String userName = generateUniqueUserName(firstName, lastName);

        // createTheRightTypeOfUser
        Person person = switch (roleKeypass) {
            case "studentKeyPass" -> new Student(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "teacherKeyPass" -> new Teacher(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "adminKeyPass" -> new Admin(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            default -> throw new PersonException("KeyPass is not valid");
        };

        personRepository.save(person);
        return person;
    }

    @Transactional
    public void updateContactDetails(Long id, String newEmail, String newPhoneNumber) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatContactDetailsAreValid(newEmail, newPhoneNumber);
        person.setEmail(newEmail);
        person.setPhoneNumber(newPhoneNumber);
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
    public List<Admin> getAllAdmins(){
        return adminRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers(){
        return teacherRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Long id){
        return personRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Role getPersonRoleById(Long id){
        if (adminRepository.existsById(id)){
            return Role.ADMIN;
        } else if (studentRepository.existsById(id)) {
            return Role.STUDENT;
        } else if (teacherRepository.existsById(id)) {
            return Role.TEACHER;
        }
        throw new EntityNotFoundException();
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
        if (Period.between(birthDate, LocalDate.now()).getYears() < 18)
            throw new PersonException("Only users 18 years old and older can sign up.");
        if (doesNotConformRegex(password, "^[A-Za-z0-9]{8,20}$"))
            throw new PersonException("Password is not valid.");
    }

    private void checkThatNameIsValid(String firstName, String lastName) throws PersonException{
        if (doesNotConformRegex(firstName, "[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ]++"))
            throw new PersonException("First name is not valid");
        if (doesNotConformRegex(lastName, "[a-zA-ZáčďéěíňóřšťůúýžÁČĎÉĚÍŇÓŘŠŤŮÚÝŽ]++"))
            throw new PersonException("Last name is not valid");
    }

    private void checkThatContactDetailsAreValid(String email, String phoneNumber) throws PersonException{
        if (doesNotConformRegex(email, "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"))
            throw new PersonException("Email is not valid.");
        if (personRepository.existsByEmail(email))
            throw new PersonException("Account with that email already exists.");
        if (doesNotConformRegex(phoneNumber, "^\\+?\\d[\\d -]{7,12}\\d$"))
            throw new PersonException("Phone number is not valid.");
        if (personRepository.existsByPhoneNumber(phoneNumber))
            throw new PersonException("Account with that phone number already exists.");
    }

//    private boolean doesNotConformRegex(String input, String regexPattern) {
//        return input == null || !input.matches(regexPattern);
//    }

}

