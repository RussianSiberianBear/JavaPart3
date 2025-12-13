package ru.hogwarts.school.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.repository.FacultyRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class FacultyService {

    private final FacultyRepository repository;

    public FacultyService(FacultyRepository repository) {
        this.repository = repository;
    }

    public Faculty create(Faculty faculty) {
        return repository.save(faculty);
    }

    public Optional<Faculty> read(Long id) {
        return repository.findById(id);
    }

    public Faculty update(Faculty faculty) {
        return repository.save(faculty);
    }

    public void delete(Long id) {
        repository.deleteById(id);
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
