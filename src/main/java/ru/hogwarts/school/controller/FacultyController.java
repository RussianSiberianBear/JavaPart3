package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.FacultyFromDto;
import ru.hogwarts.school.model.FacultyToDto;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

@RestController
@RequestMapping("/faculty")
@Validated
@Tag(name = "Faculty Controller", description = "Operations with faculties")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @PostMapping
    public FacultyToDto createFaculty(@Valid @RequestBody FacultyFromDto dto) {
        return facultyService.create(dto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacultyById(
            @PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id) {
        Optional<FacultyToDto> faculty = facultyService.read(id);
        return faculty.<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<FacultyToDto> updateFaculty(
            @PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id,
            @Valid @RequestBody FacultyFromDto dto) {
        return facultyService.update(id, dto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaculty(
            @PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id) {
        if (facultyService.delete(id)) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/color/{color}")
    public ResponseEntity<?> getFacultiesByColor(
            @PathVariable
            @Size(min = 1, message = "{faculty.color.notblank}")
            String color) {
        return ResponseEntity.ok(facultyService.getAllFacultyByColor(color));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllFaculties() {
        return ResponseEntity.ok(facultyService.getAllFaculties());
    }

    @GetMapping("/findByNameOrByColor/{nameOrColor}")
    public ResponseEntity<?> getFacultiesByNameOrByColor(
            @PathVariable
            @Size(min = 1, message = "{faculty.nameOrColor.notblank}")
            String nameOrColor) {
        return ResponseEntity.ok(facultyService.getAllFacultyByNameOrByColor(nameOrColor, nameOrColor));
    }

    @GetMapping("/students/{id}")
    public ResponseEntity<?> getStudentsByFacultyId(@PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id) {
        return ResponseEntity.ok(facultyService.getAllStudentsByFacultyId(id));
    }

    @GetMapping("/getLongestFacultyName")
    public ResponseEntity<String> getLongestFacultyName() {
        return ResponseEntity.ok(facultyService.getLongestFacultyName());
    }

    @GetMapping("/getSum")
    public ResponseEntity<?> getSum() {
        return ResponseEntity.ok(facultyService.getSum());
    }
}
