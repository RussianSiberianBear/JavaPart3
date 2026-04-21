package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.InvalidFileSizeException;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.StudentToDto;
import ru.hogwarts.school.service.StudentService;

import java.io.IOException;
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

    @GetMapping("/age")
    public ResponseEntity<?> getStudentsByAgeBetween(
            @RequestParam(value = "min")
            @Min(value = 10, message = "{student.age.min10}") int min,

            @RequestParam(value = "max")
            @Max(value = 24, message = "{student.age.max24}") int max) {

        return ResponseEntity.ok(studentService.getAllStudentsByAgeBetween(min, max));
    }

    @GetMapping("/faculty/{name}")
    public ResponseEntity<?> getFacultyNameByStudentName(@PathVariable @Size(min = 3, message = "{student.name.min3}") String name) {
        return ResponseEntity.ok(studentService.getFacultyNameByStudentName(name));
    }

    @GetMapping("/students/{color}")
    public ResponseEntity<?> getStudentsByFacultyColor(@PathVariable @Size(min = 3, message = "{faculty.color.min3}") String color) {
        return ResponseEntity.ok(studentService.findStudentsByFacultyColorContainingIgnoreCase(color));
    }

    @PostMapping(value = "/{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadAvatar(@PathVariable Long id, @RequestParam MultipartFile avatar) throws IOException {

        if (avatar.getSize() > 1024 *300){
           throw new InvalidFileSizeException("Размер файла не должен превышать 300КБ");
        }

        if (studentService.uploadAvatar(id, avatar)) {
            return ResponseEntity.ok().build();
        }else {
            return ResponseEntity.badRequest().build();
        }

    }

}
