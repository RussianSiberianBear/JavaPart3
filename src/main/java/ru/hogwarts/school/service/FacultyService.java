package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(FacultyService.class);

    public FacultyService(FacultyRepository repository, FacultyMapper mapper, StudentMapper studentMapper) {
        this.repository = repository;
        this.mapper = mapper;
        this.studentMapper = studentMapper;
    }

    public FacultyToDto create(FacultyFromDto dto) {
        logger.info("Was invoked method for create faculty");
        Faculty faculty = mapper.toEntity(dto);
        logger.debug("Записываем новый факультет в базу данных");
        faculty = repository.save(faculty);
        logger.debug("Успешно записали новый факультет в базу данных");
        return mapper.toDto(faculty);
    }

    public Optional<FacultyToDto> read(Long id) {
        logger.info("Was invoked method for read faculty");
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<FacultyToDto> update(Long id, FacultyFromDto dto) {
        logger.info("Was invoked method for update faculty");
        Optional<Faculty> faculty = repository.findById(id);
        if (faculty.isEmpty()) {
            logger.error("There is not faculty with id = " + id);
        }
        return faculty
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
            logger.warn("There is not faculty with id = " + id);
            return false;
        }
    }

    public Collection<FacultyToDto> getAllFaculties() {
        logger.info("Was invoked method for getAllFaculties");
        Collection<Faculty> faculties = repository.findAll(Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.asc("color")
        ));
        return faculties.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Optional<FacultyToDto> getAllFacultyByColor(String color) {
        logger.info("Was invoked method for getAllFacultyByColor");
        Optional<Faculty> faculty = repository.findByColorContainingIgnoreCase(color);
        if (faculty.isEmpty()) {
            logger.error("There is not faculty with color = " + color);
        }
        return faculty
                .map(mapper::toDto);
    }

    public Collection<FacultyToDto> getAllFacultyByNameOrByColor(String name, String color) {
        logger.info("Was invoked method for getAllFacultyByNameOrByColor");
        return repository.findByNameContainingIgnoreCaseOrColorContainingIgnoreCase(name, color)
                .stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByFacultyId(Long facultyId) {
        logger.info("Was invoked method for getAllStudentsByFacultyId");
        Optional<Faculty> faculty = repository.findById(facultyId);
        if (faculty.isEmpty()) {
            logger.error("There is not faculty with facultyId ={}", facultyId);
            throw new FacultyNotFoundException(facultyId);
        }
        Collection<Student> students = faculty.get().getStudents();
        return students.stream().map(studentMapper::toDto).collect(Collectors.toList());
    }
}
