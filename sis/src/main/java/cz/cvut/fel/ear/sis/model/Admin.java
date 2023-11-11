package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity

public class Admin extends Person{

    public Admin(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate) {
        super(firstName, lastName, email, phoneNumber, birthDate);
        this.classroomList = new ArrayList<>();
        this.semesterList = new ArrayList<>();
    }


    public Admin() {
    }

    @ManyToMany
    List<Classroom> classroomList;

    @ManyToMany
    List<Semester> semesterList;


}
