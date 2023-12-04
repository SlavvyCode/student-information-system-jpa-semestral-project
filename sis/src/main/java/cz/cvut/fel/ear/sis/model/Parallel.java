package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.enums.DayOfWeek;
import cz.cvut.fel.ear.sis.utils.enums.TimeSlot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Parallel {

    @Id
    @GeneratedValue
    private Long id;

    @Column(nullable = false, updatable = false)
    private int capacity;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private TimeSlot timeSlot;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private DayOfWeek dayOfWeek;
    @ManyToOne(fetch = FetchType.LAZY)
    private Semester semester;
    @ManyToOne(fetch = FetchType.LAZY)
    private Classroom classroom;
    @ManyToOne(fetch = FetchType.LAZY)
    private Course course;
    @OneToMany
    @JoinColumn(name = "parallel_id")
    private List<Enrollment> enrollments = new ArrayList<>();
    @ManyToMany(cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "parallel_student",
            joinColumns = @JoinColumn(name = "parallel_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    private List<Student> students = new ArrayList<>();

    public Parallel(){

    }

    public Parallel(int capacity, TimeSlot timeSlot, DayOfWeek dayOfWeek, Semester semester, Classroom classroom, Course course){
        this.capacity = capacity;
        this.timeSlot = timeSlot;
        this.dayOfWeek = dayOfWeek;
        this.semester = semester;
        this.classroom = classroom;
        this.course = course;
    }

    public Long getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public TimeSlot getTimeSlot() {
        return timeSlot;
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public Semester getSemester() {
        return semester;
    }

    public Classroom getClassroom() {
        return classroom;
    }

    public Course getCourse() {
        return course;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<Student> getStudents() {
        return students;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setTimeSlot(TimeSlot timeSlot) {
        this.timeSlot = timeSlot;
    }

    public void setDayOfWeek(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setSemester(Semester semester) {
        this.semester = semester;
    }

    public void setClassroom(Classroom classroom) {
        this.classroom = classroom;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public void setEnrollments(List<Enrollment> enrollments) {
        this.enrollments = enrollments;
    }

    public void setStudents(List<Student> students) {
        this.students = students;
    }
}
