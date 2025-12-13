package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.StudentToDto;
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
    public StudentToDto createStudent(@Valid @RequestBody StudentFromDto dto) {
        return studentService.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable @Min(value = 1, message = "{student.id.min}") Long id) {
        Optional<StudentToDto> student = studentService.read(id);
        if (student.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentToDto> updateStudent(
            @PathVariable @Min(value = 1, message = "{student.id.min}") Long id,
            @Valid @RequestBody StudentFromDto dto) {
        return studentService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delStudentById(@PathVariable @Min(value = 1, message = "{student.id.min}") Long id) {
        if (studentService.delete(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/age/{age}")
    public ResponseEntity<?> getStudentsByAge(@PathVariable @Min(value = 18, message = "{student.age.min18}") int age) {
        return ResponseEntity.ok(studentService.getAllStudentsByAge(age));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

}
