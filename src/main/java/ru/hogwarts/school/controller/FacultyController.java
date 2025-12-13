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
    public ResponseEntity<?> getFacultyById(@PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id) {
        Optional<FacultyToDto> faculty = facultyService.read(id);
        if (faculty.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
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
        this.facultyService.delete(id);
        return ResponseEntity.ok().build();
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
}
