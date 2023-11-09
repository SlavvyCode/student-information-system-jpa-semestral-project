package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int ECTS;
    private Locale language;




    //prerequisite relationship with itself with annotation
//    @OneToMany
//    private ArrayList<Course> prerequisite;
//



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
