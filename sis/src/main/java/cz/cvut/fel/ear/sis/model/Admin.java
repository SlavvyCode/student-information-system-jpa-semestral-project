package cz.cvut.fel.ear.sis.model;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;

import java.util.List;

@Entity
public class Admin extends Person{


    @ManyToMany
    List<Classroom> classroomList;

    @ManyToMany
    List<Semester> semesterList;


}
