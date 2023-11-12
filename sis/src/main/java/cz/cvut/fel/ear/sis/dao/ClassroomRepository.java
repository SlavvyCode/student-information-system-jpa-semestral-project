package cz.cvut.fel.ear.sis.dao;

import cz.cvut.fel.ear.sis.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Long> {
}
