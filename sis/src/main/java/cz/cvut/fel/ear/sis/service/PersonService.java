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

    /**
     * Creates a new person with the provided details based on the role specified.
     *
     * @param firstName     The first name of the person.
     * @param lastName      The last name of the person.
     * @param email         The email address of the person.
     * @param phoneNumber   The phone number of the person.
     * @param birthDate     The birth date of the person.
     * @param password      The password for the person's account.
     * @param roleKeypass   The key pass to determine the role of the person.
     * @return              The created person object.
     * @throws PersonException If any validation fails or the key pass is invalid.
     */
    @Transactional
    public Person createANewPerson(String firstName,
                                   String lastName,
                                   String email,
                                   String phoneNumber,
                                   LocalDate birthDate,
                                   String password,
                                   String roleKeypass) throws PersonException {
        checkThatDetailsAreValid(firstName, lastName, email, phoneNumber, birthDate, password);
        String userName = generateUniqueUserName(firstName, lastName);
        Person person = switch (roleKeypass) {
            case "studentKeyPass" -> new Student(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "teacherKeyPass" -> new Teacher(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            case "adminKeyPass" -> new Admin(firstName, lastName, email, phoneNumber, birthDate, userName, passwordEncoder.encode(password));
            default -> throw new PersonException("KeyPass is not valid");
        };

        personRepository.save(person);
        return person;
    }

    /**
     * Updates the contact details (email and phone number) of a person with the specified ID.
     *
     * @param id            The ID of the person to update.
     * @param newEmail      The new email address for the person.
     * @param newPhoneNumber The new phone number for the person.
     * @throws PersonException If the person is not found or the details are invalid.
     */
    @Transactional
    public void updateContactDetails(Long id, String newEmail, String newPhoneNumber) throws PersonException {
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Person not found with id: " + id));
        checkThatContactDetailsAreValid(newEmail, newPhoneNumber);
        person.setEmail(newEmail);
        person.setPhoneNumber(newPhoneNumber);
        personRepository.save(person);
    }

    /**
     * Updates the name and username of a person with the specified ID.
     *
     * @param id        The ID of the person to update.
     * @param firstName The new first name for the person.
     * @param lastName  The new last name for the person.
     * @throws PersonException If the person is not found or the names are invalid.
     */
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

    /**
     * Retrieves a list of all people in the system.
     *
     * @return List of Person objects.
     */
    @Transactional(readOnly = true)
    public List<Person> getAllPeople(){
        return personRepository.findAll();
    }

    /**
     * Retrieves a list of all admins in the system.
     *
     * @return List of Admin objects.
     */
    @Transactional(readOnly = true)
    public List<Admin> getAllAdmins(){
        return adminRepository.findAll();
    }

    /**
     * Retrieves a list of all students in the system.
     *
     * @return List of Student objects.
     */
    @Transactional(readOnly = true)
    public List<Student> getAllStudents(){
        return studentRepository.findAll();
    }

    /**
     * Retrieves a list of all teachers in the system.
     *
     * @return List of Teacher objects.
     */
    @Transactional(readOnly = true)
    public List<Teacher> getAllTeachers(){
        return teacherRepository.findAll();
    }

    /**
     * Retrieves a person by their ID.
     *
     * @param id The ID of the person to retrieve.
     * @return Optional containing the Person object if found, otherwise empty.
     */
    @Transactional(readOnly = true)
    public Optional<Person> getPersonById(Long id){
        return personRepository.findById(id);
    }

    /**
     * Retrieves the role of a person by their ID.
     *
     * @param id The ID of the person.
     * @return Role of the person.
     * @throws EntityNotFoundException If the person is not found.
     */
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

}

