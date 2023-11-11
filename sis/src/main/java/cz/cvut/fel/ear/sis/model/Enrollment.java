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
    @Column(name = "parallel_id")
    private Parallel parallel;
    //todo relationships

    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;






}
