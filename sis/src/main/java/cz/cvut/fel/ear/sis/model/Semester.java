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
    @Column(nullable = false, updatable = false)
    private LocalDate startDate;
    @Column(nullable = false, updatable = false)
    private LocalDate endDate;
    @Column(nullable = false, updatable = false, unique = true)
    private String code;
    @Column(nullable = false)
    private Boolean isActive;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private SemesterType semesterType;

    public Semester() {

    }

    public Semester(LocalDate startDate, LocalDate endDate, String code, SemesterType semesterType) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.code = code;
        this.isActive = false;
        this.semesterType = semesterType;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getCode() {
        return code;
    }

    public Boolean getActive() {
        return isActive;
    }

    public SemesterType getSemesterType() {
        return semesterType;
    }

}
