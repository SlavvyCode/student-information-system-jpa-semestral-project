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
    @ManyToOne(fetch = FetchType.LAZY)
    private Teacher teacher;
    @Column(nullable = false, unique = true, updatable = false)
    private String name;
    @Column(nullable = false, unique = true, updatable = false)
    private String code;
    @Column(nullable = false, updatable = false)
    private int ECTS;
    @Column(nullable = false, updatable = false)
    private Locale language;
    @OneToMany
    @JoinColumn(name = "course_id")
    private List<Parallel> parallelsList = new ArrayList<>();

    public Course() {

    }

    public Course(Teacher teacher, String name, String code, int ECTS, Locale language) {
        this.teacher = teacher;
        this.name = name;
        this.code = code;
        this.ECTS = ECTS;
        this.language = language;
    }

    public void setParallelsList(List<Parallel> parallelsList) {
        this.parallelsList = parallelsList;
    }

    public Long getId() {
        return id;
    }

    public Teacher getTeacher() {
        return teacher;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public int getECTS() {
        return ECTS;
    }

    public Locale getLanguage() {
        return language;
    }

    public List<Parallel> getParallelsList() {
        return parallelsList;
    }

}
