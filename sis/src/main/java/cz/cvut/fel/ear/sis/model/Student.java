package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends Person {

    @OneToMany
    @JoinColumn(name = "student_id")
    private List<Enrollment> myEnrollments = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password);
    }

    public List<Enrollment> getMyEnrollments() {
        return myEnrollments;
    }

    public void setMyEnrollments(List<Enrollment> myEnrollments) {
        this.myEnrollments = myEnrollments;
    }

}
