package cz.cvut.fel.ear.sis.repository;

import cz.cvut.fel.ear.sis.model.Parallel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParallelRepository extends JpaRepository<Parallel, Long> {

}
