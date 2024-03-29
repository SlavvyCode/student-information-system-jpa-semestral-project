package cz.cvut.fel.ear.sis.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("STUDENT")
public class Student extends Person {


    ///CASCADE REMOVE
    @OneToMany( cascade = CascadeType.REMOVE, orphanRemoval = true)    @JoinColumn(name = "student_id")
    @JsonManagedReference("student_enrollments")
    @JsonIgnore
    private List<Enrollment> myEnrollments = new ArrayList<>();

    public Student() {
        super();
    }

    public Student(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate, String userName, String password) {
        super(firstName, lastName, email, phoneNumber, birthDate, userName, password, "ROLE_STUDENT");
    }

    public List<Enrollment> getMyEnrollments() {
        return myEnrollments;
    }

    public void setMyEnrollments(List<Enrollment> myEnrollments) {
        this.myEnrollments = myEnrollments;
    }

    public void addEnrollment(Enrollment enrollment) {
        myEnrollments.add(enrollment);
    }
    public void removeEnrollment(Enrollment enrollment) {
        myEnrollments.remove(enrollment);
    }

    @PreRemove
    private void removeStudentFromEnrollments() {
        for (Enrollment enrollment : myEnrollments) {

            enrollment.getParallel().removeStudent(this);
            enrollment.getParallel().removeEnrollment(enrollment);

            enrollment.setStudent(null);
            enrollment.setParallel(null);
        }
        myEnrollments.clear();
    }

}
