package cz.cvut.fel.ear.sis.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends Person {

    @JsonManagedReference("teacher_courses")
    @OneToMany
    @JoinColumn(name = "teacher_id")
    @OrderBy("name ASC")
    private List<Course> myCourses = new ArrayList<>();

    public Teacher() {
        super();
    }

    public Teacher(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password,"ROLE_TEACHER");
    }

    public List<Course> getMyCourses() {
        return myCourses;
    }

    public void setMyCourses(List<Course> myCourses) {
        this.myCourses = myCourses;
    }

    public void addCourse(Course course) {
        this.myCourses.add(course);
    }
    public void removeCourse(Course course) {
        this.myCourses.remove(course);
    }
}
