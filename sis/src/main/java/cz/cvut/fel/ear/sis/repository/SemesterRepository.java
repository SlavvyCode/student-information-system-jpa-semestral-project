package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {


    public Semester findByStartDate(LocalDate startDate);
}
