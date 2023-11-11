package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.SemesterType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Semester {

    @Id
    @GeneratedValue
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String code;


    @Enumerated(EnumType.STRING)
    private SemesterType semesterType;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
