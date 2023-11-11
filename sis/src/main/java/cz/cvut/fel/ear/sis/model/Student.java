package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student extends Person{


    //enrollment relationship one to many

    //todo only new style of relationship thing
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "student_id")
    private List<Enrollment> enrollmentList;

    public Student(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate) {
        super(firstName, lastName, email, phoneNumber, birthDate);
        this.enrollmentList = new ArrayList<>();
    }

    public Student() {

        super();
    }

}
