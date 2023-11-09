package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Classroom {

    @Id
    @GeneratedValue
    private Long id;
    private String code;
    private int capacity;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
