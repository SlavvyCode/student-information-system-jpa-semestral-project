package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.Grade;
import cz.cvut.fel.ear.sis.utils.Status;
import jakarta.persistence.*;

@Entity
public class Enrollment {

    @Id
    @GeneratedValue
    private Long id;


    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }


    @Enumerated(EnumType.STRING)
    private Grade grade;
    @Enumerated(EnumType.STRING)
    private Status status;
    private boolean finished;
    @ManyToOne
    @JoinColumn(name = "parallel_id")
    private Parallel parallel;

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;



    public Enrollment(Parallel parallel, Student student) {
        this.status = Status.IN_PROGRESS;
        this.parallel = parallel;
        this.student = student;
        this.finished = false;
    }



    public Enrollment() {

    }



}
