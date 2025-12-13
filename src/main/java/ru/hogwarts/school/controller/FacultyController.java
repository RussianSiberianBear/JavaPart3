package ru.hogwarts.school.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.hogwarts.school.model.CreateFacultyDTO;
import ru.hogwarts.school.model.Faculty;
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
    public Faculty createFaculty(@Valid @RequestBody CreateFacultyDTO facultyDTO) {
        return facultyService.create(toCreateEntity(facultyDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFacultyById(@PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id) {
        Optional<Faculty> faculty = facultyService.read(id);
        if (faculty.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(faculty);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Faculty> updateFaculty(
            @PathVariable @Min(value = 1, message = "{faculty.id.min}") Long id,
            @Valid @RequestBody CreateFacultyDTO facultyDTO) {
        Faculty editFaculty = facultyService.update(toUpdateEntity(id, facultyDTO));
        if (editFaculty == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editFaculty);
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

    private Faculty toCreateEntity(CreateFacultyDTO facultyDTO) {
        Faculty f = new Faculty();
        f.setName(facultyDTO.getName());
        f.setColor(facultyDTO.getColor());
        return f;
    }

    private Faculty toUpdateEntity(Long id, CreateFacultyDTO facultyDTO) {
        return new Faculty(id, facultyDTO.getName(), facultyDTO.getColor());
    }
}
