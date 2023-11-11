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


    public Semester(LocalDate startDate, LocalDate endDate, String code, SemesterType semesterType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.code = code;
        this.semesterType = semesterType;
    }

    public Semester() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
