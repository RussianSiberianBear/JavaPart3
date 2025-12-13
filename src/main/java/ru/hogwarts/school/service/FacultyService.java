package ru.hogwarts.school.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.mapper.FacultyMapper;
import ru.hogwarts.school.model.FacultyFromDto;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FacultyToDto;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository repository;
    private final FacultyMapper mapper;

    public FacultyService(FacultyRepository repository, FacultyMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
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

    public Collection<Faculty> getAllFaculties() {
        return repository.findAll(Sort.by(
                Sort.Order.asc("name"),
                Sort.Order.asc("color")
        ));
    }

    public Collection<Faculty> getAllFacultyByColor(String color) {
        return repository.findByColor(color, Sort.by(
                Sort.Order.asc("name")
        ));
    }
}
