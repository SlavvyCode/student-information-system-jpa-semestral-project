package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;

import java.util.List;

@Entity
public class Teacher extends Person{

    //teaching relationship one to many
    private List<Course> courseList;
}
