package cz.cvut.fel.ear.sis.model;

import cz.cvut.fel.ear.sis.utils.enums.SemesterType;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Objects;

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

    public Semester(int year, SemesterType semesterType) {
        this.startDate = LocalDate.of(year, semesterType.getStartDate().getMonth(), semesterType.getStartDate().getDayOfMonth());
        this.endDate = LocalDate.of(year, semesterType.getEndDate().getMonth(), semesterType.getEndDate().getDayOfMonth());
        this.semesterType = semesterType;
        this.code = semesterType.name() + year;
        this.isActive = false;
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
