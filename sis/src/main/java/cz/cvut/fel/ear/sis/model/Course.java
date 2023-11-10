package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
public class Course {

    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private int ECTS;
    private Locale language;



    @OneToMany
    private List<Course> prerequisiteCourseList;



    @ManyToOne
//    @JoinColumn
    //todo OneToMany musi mit vzdy mapped by.
    private Teacher teacher;






    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
