package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.TimeSlot;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Parallel {

    @Id
    @GeneratedValue
    private Long id;

    private int capacity;

    @ManyToOne
    private Classroom classroom;

    @ManyToOne
    private Course course;

    @ManyToOne
    private Semester semester;
    public Parallel(int capacity, Classroom classroom, DayOfWeek dayOfWeek, TimeSlot timeSlot) {
        this.capacity = capacity;
        this.classroom = classroom;
        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
    }
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    //TODO RELATIONSHIPS IF POSSIBLE
    //TODO ENUMS CLASSES AND ADD THEM AS FIELDS/COLS AND ANNOTATE THEM AS ENUMS

    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;


    @OneToMany(mappedBy = "parallel")
    List<Student> studentsEnrolledInParallel;

    public Parallel() {

    }


    //todo pridat many to many relationship student a arraylist of students
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
