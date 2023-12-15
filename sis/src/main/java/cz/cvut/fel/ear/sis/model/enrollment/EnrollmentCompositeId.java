package cz.cvut.fel.ear.sis.model.enrollment;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;

import java.io.Serializable;
import java.util.Objects;


public class EnrollmentCompositeId implements Serializable {

    private Long studentId;
    private Long parallelId;


    public EnrollmentCompositeId() {
    }

    public EnrollmentCompositeId(Long studentId, Long parallelId) {
        this.studentId = studentId;
        this.parallelId = parallelId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getParallelId() {
        return parallelId;
    }

    public void setParallelId(Long parallelId) {
        this.parallelId = parallelId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EnrollmentCompositeId that)) return false;
        return Objects.equals(studentId, that.studentId) && Objects.equals(parallelId, that.parallelId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, parallelId);
    }
}
