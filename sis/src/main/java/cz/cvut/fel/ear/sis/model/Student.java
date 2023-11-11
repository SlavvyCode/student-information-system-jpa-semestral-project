package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Student extends Person{


    //enrollment relationship one to many

    //todo consider adding cascade = CascadeType.ALL
    @OneToMany(mappedBy = "student")
    private List<Enrollment> enrollmentList;


}
