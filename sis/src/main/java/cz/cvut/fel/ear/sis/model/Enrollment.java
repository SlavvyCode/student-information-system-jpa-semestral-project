package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.enums.Grade;
import cz.cvut.fel.ear.sis.utils.enums.Status;
import jakarta.persistence.*;

@Entity
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;
    @Enumerated(EnumType.STRING)
    private Grade grade;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;
    @ManyToOne(fetch = FetchType.LAZY)
    private Student student;
    @ManyToOne(fetch = FetchType.LAZY)
    private Parallel parallel;

    public Enrollment() {

    }

    public Enrollment(Parallel parallel, Student student) {
        this.grade = null;
        this.status = Status.IN_PROGRESS;
        this.student = student;
        this.parallel = parallel;
    }
//

    public void setGrade(Grade grade) {
        if(grade==Grade.F){
            setStatus(Status.FAILED);
        } else {
            setStatus(Status.PASSED);
        }
        this.grade = grade;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Grade getGrade() {
        return grade;
    }

    public Status getStatus() {
        return status;
    }

    public Student getStudent() {
        return student;
    }

    public Parallel getParallel() {
        return parallel;
    }








//for mocks only
    public void setId(Long id) {
        this.id = id;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public void setParallel(Parallel parallel) {
        this.parallel = parallel;
    }

}
