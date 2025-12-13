package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.CreateStudentDTO;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.service.StudentService;

import java.util.Optional;

@RestController
@RequestMapping("/student")
@Validated
@Tag(name = "Student Controller", description = "Operations with students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public Student createStudent(@Valid @RequestBody CreateStudentDTO studentDTO) {
        return studentService.create(toCreateEntity(studentDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable @Min(value = 1, message = "{student.id.min}") Long id) {
        Optional<Student> student = studentService.read(id);
        if (student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(
            @PathVariable @Min(value = 1, message = "{student.id.min}") Long id,
            @Valid @RequestBody CreateStudentDTO studentDTO) {
        Student editStudent = studentService.update(toUpdateEntity(id, studentDTO));
        if (editStudent == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delStudentById(@PathVariable @Min(value = 1, message = "{student.id.min}") Long id) {
        studentService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<?> getStudentsByAge(@PathVariable @Min(value = 18, message = "{student.age.min18}") int age) {
        return ResponseEntity.ok(studentService.getAllStudentsByAge(age));
    }

    private Student toCreateEntity(CreateStudentDTO dto) {
        Student s = new Student();
        s.setName(dto.getName());
        s.setFamily(dto.getFamily());
        s.setAge(dto.getAge());
        return s;
    }

    private Student toUpdateEntity(long id, CreateStudentDTO dto) {
        return new Student(id, dto.getName(), dto.getFamily(), dto.getAge());
    }

}
