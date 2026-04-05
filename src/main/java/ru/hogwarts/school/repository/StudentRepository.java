package ru.hogwarts.school.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Collection<Student> findByAge(int age, Sort by);

    Collection<Student> findByAgeBetween(int min, int max, Sort by);

    Collection<Student> findByNameIgnoreCase(String name, Sort sort);
    Collection<Student> findByFacultyColorContainingIgnoreCase(String color, Sort sort);
}
