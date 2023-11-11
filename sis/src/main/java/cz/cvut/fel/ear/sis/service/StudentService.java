package cz.cvut.fel.ear.sis.service;

import cz.cvut.fel.ear.sis.dao.PersonRepository;
import cz.cvut.fel.ear.sis.model.Course;
import cz.cvut.fel.ear.sis.model.Parallel;
import cz.cvut.fel.ear.sis.model.Person;
import cz.cvut.fel.ear.sis.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    PersonRepository personRepository;

    @Autowired
    public StudentService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }



    public List<Student> getAllStudents() {
        Iterable<Person> students = personRepository.findAll();
        ArrayList<Student> studentsList = new ArrayList<>();

        for (Person student : students) {
            if (student instanceof Student) {
                System.out.println(student);
                studentsList.add((Student) student);
            }
        }

        return studentsList;
    }
    public void enrollStudentToParallel(Student student, Parallel parallel) {

        Optional<Person> studentOptional = personRepository.findById(student.getId());

        //check if person is a student
        if (!(studentOptional.get() instanceof Student)) {
            throw new IllegalArgumentException("Person is not a student!");
        }

        if (studentOptional.isPresent()) {
            Student student1 = (Student) studentOptional.get();

            //tady mi neni uplne jasne na ktere vrstve implmentovat
            // - myslim 2 ruzna videa a cvicici delaji trosku jinak
            // jedni upravuji v repository, ostatni primo v dao, mylim se?
            //toto je v dao

            parallel.addStudent(student1);

            personRepository.save(student1);
        }
        else{
            throw new IllegalArgumentException("Student not found!");
        }
    }

}
