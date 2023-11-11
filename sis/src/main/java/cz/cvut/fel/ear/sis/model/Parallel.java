package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.TimeSlot;
import jakarta.persistence.*;

import java.util.ArrayList;
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
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne
    private Semester semester;
    public Parallel(int capacity, Classroom classroom, DayOfWeek dayOfWeek, TimeSlot timeSlot) {
        this.classroom = classroom;

//        if(capacity > classroom.getCapacity())
//            throw new IllegalArgumentException("Capacity of parallel cannot be greater than capacity of classroom");


        this.capacity = classroom.getCapacity();


        this.dayOfWeek = dayOfWeek;
        this.timeSlot = timeSlot;
        studentsEnrolledInParallel = new ArrayList<>();
    }
    @Enumerated(EnumType.STRING)
    private DayOfWeek dayOfWeek;

    @Enumerated(EnumType.STRING)
    private TimeSlot timeSlot;

    @OneToMany
    List<Student> studentsEnrolledInParallel;

    public Parallel() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    public void addStudent(Student student){
        if(studentsEnrolledInParallel.size() < capacity){
            studentsEnrolledInParallel.add(student);
        }
        else{
            throw new IllegalArgumentException("Parallel is full!");
        }
    }
}
