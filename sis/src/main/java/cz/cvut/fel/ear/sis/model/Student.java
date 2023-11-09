package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Student extends Person{


    //enrollment relationship one to many
    private List<Enrollment> enrollments;


}
