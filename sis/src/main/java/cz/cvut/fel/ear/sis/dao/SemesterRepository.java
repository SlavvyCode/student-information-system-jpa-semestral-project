package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Semester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterRepository extends JpaRepository<Semester, Long> {
}
