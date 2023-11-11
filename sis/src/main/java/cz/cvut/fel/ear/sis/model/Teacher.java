package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Teacher extends Person{
    @OneToMany(mappedBy = "teacher")
    private List<Course> courseList;
}
