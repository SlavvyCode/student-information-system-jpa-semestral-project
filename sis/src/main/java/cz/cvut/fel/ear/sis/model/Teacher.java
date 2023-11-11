package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@PrimaryKeyJoinColumn(name = "teacher_id")
public class Teacher extends Person{





    //teaching relationship one to many

    @OneToMany(mappedBy = "teacher")
    private List<Course> courseList;
}
