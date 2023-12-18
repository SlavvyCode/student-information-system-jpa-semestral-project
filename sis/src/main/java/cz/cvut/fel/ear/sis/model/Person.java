package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.enums.Role;
import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PERSON_ROLE", discriminatorType = DiscriminatorType.STRING)
public abstract class Person{
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String phoneNumber;
    @Column(nullable = false, updatable = false)
    private LocalDate birthDate;
    @Column(nullable = false, unique = true)
    private String userName;
    @Column(nullable = false)
    private String password;


//
//    //todo make sure it's ok
//    @Enumerated(EnumType.STRING)
//    @Column(name = "ROLE", nullable = false, insertable = false)
//    private Role role;

//    public Role getRole() {
//        return role;
//    }
//
//    public void setRole(Role role) {
//        this.role = role;
//    }

    public Person() {

    }

    public Person(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.userName = userName;
        this.password = password;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public void setId(long l) {
        id = l;
    }
}
