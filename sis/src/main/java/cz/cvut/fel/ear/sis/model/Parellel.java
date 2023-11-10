package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.TimeSlot;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

import java.time.DayOfWeek;

@Entity
public class Parellel {

    @Id
    @GeneratedValue
    private Long id;

    private int capacity;

    private DayOfWeek dayOfWeek;

    //TODO RELATIONSHIPS IF POSSIBLE
    //TODO ENUMS CLASSES AND ADD THEM AS FIELDS/COLS
    private TimeSlot timeSlot;

    //todo pridat many to many relationship student a arraylist of students
    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
