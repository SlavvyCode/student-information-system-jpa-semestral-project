package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

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

    //grade Enum inside this calss
    private enum Grade {
        A, B, C, D, E, F
    }
    private enum Status{
        IN_PROGRESS,
        PASSED,
        FAILED
    }
    private Grade grade;

    private boolean finished;

}
