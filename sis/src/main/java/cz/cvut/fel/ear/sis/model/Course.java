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


    @OneToMany(mappedBy = "course")
    private List<Parallel> parallelsList;

    @ManyToOne
    @JoinColumn(name = "teacher_id")
    private Teacher teacher;


    public Course(String name, int ECTS, Locale language, Teacher teacher) {
        this.name = name;
        this.ECTS = ECTS;
        this.language = language;
        this.parallelsList = new ArrayList<>();
        this.teacher = teacher;
    }

    public Course() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
