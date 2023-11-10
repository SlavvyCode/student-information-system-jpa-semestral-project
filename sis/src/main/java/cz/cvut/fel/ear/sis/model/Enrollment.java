package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.Grade;
import cz.cvut.fel.ear.sis.utils.Status;
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
    private Grade grade;
    private Status status;


    private boolean finished;

}
