package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Classroom {

    @Id
    @GeneratedValue
    private Long id;


    //todo jake chceme mit columns../
    // @Column(nullable = false, unique = true)


//    private Admin admin;
    private String code;


    private int capacity;



    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
