package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Teacher extends Person{
    @OneToMany(mappedBy = "teacher")
    private List<Course> courseList;

    public Teacher(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate) {
        super(firstName, lastName, email, phoneNumber, birthDate);
        this.courseList = new ArrayList<>();
    }

    public Teacher() {
        super();
    }
}
