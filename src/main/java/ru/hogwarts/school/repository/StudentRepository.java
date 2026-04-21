package ru.hogwarts.school.repository;

import org.intellij.lang.annotations.Language;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.hogwarts.school.model.Student;

import java.util.Collection;

public interface StudentRepository extends JpaRepository<Student,Long> {
    Collection<Student> findByAge(int age, Sort by);

    Collection<Student> findByAgeBetween(int min, int max, Sort by);

    Collection<Student> findByNameIgnoreCase(String name, Sort sort);
    Collection<Student> findByFacultyColorContainingIgnoreCase(String color, Sort sort);

    @Query(value = "SELECT CONCAT('Количество студентов ', count(*)) AS result  FROM student", nativeQuery = true)
    String countStudents();

    @Query(value = "SELECT CONCAT('Средний возраст равен ', COALESCE(ROUND(AVG(age), 2), 0.0))  AS result  FROM student", nativeQuery = true)
    String avgAgeStudents();

    @Query(value = "SELECT *  FROM student ORDER BY id desc LIMIT 5", nativeQuery = true)
    Collection<Student> find5LastStudents();


}
