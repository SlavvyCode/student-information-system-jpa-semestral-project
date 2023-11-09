package cz.cvut.fel.ear.sis.model;

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

    private enum TimeSlot{
        SLOT1,
        SLOT2,
        SLOT3,
        SLOT4,
        SLOT5,
        SLOT6,
        SLOT7,
    }
    public void setId(Long id) {
        this.id = id;
    }


    public Long getId() {
        return id;
    }
}
