package ru.hogwarts.school.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.*;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FacultyService {

    private final FacultyRepository repository;
    private final FacultyMapper mapper;
    private final StudentMapper studentMapper;


    public FacultyService(FacultyRepository repository, FacultyMapper mapper, StudentMapper studentMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.studentMapper = studentMapper;
    }

    public FacultyToDto create(FacultyFromDto dto) {
        Faculty faculty = mapper.toEntity(dto);
        faculty = repository.save(faculty);
        return mapper.toDto(faculty);
    }

    public Optional<FacultyToDto> read(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<FacultyToDto> update(Long id, FacultyFromDto dto) {
        return repository.findById(id)
                .map(student -> {
                    mapper.updateFacultyFromDto(dto, student);
                    return mapper.toDto(student);
                });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Collection<FacultyToDto> getAllFaculties() {
        Collection<Faculty> faculties = repository.findAll(Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.asc("color")
        ));
        return  faculties.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<FacultyToDto> getAllFacultyByColor(String color) {
        return repository.findByColorContainingIgnoreCase(color)
                .map(mapper::toDto);
    }

    public Collection<FacultyToDto> getAllFacultyByNameOrByColor(String name, String color) {
        return repository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(name, color)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByFacultyId(Long facultyId) {
        Faculty faculty = repository.findById(facultyId).orElseThrow(() -> new FacultyNotFoundException(facultyId));
        Collection<Student> students = faculty.getStudents();
        return students.stream().map(studentMapper::toDto).collect(Collectors.toList());
    }
}
