package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {


    Semester findByStartDate(LocalDate startDate);

    Optional<Semester> findSemesterByCode(String code);

    Optional<Semester> findSemesterByIsActiveIsTrue();

}
