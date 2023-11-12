package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Classroom {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String code;
    @Column(nullable = false, updatable = false)
    private int capacity;


    public Classroom() {

    }

    public Classroom(String code, int capacity) {
        this.code = code;
        this.capacity = capacity;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public int getCapacity() {
        return capacity;
    }

}
