package ru.hogwarts.school.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repository;

    public StudentService(StudentRepository repository) {
        this.repository = repository;
    }

    public Student create(Student student) {
    }

    public Optional<Student> read(Long id) {
        return repository.findById(id);
    }

    public Student update(Student student) {
     }

    public void delete(long id) {
        repository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return repository.findAll(Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
    }

    public Collection<Student> getAllStudentsByAge(int age) {
        return repository.findByAge(age, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
    }
}
