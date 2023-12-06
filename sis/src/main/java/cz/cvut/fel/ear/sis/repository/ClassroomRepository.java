package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

}
