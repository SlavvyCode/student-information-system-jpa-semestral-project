package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.List;

@Entity
public class Classroom {

    @Id
    @GeneratedValue
    private Long id;
    private String code;
    private int capacity;


    @OneToMany
    private List<Parallel> parallelList;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
