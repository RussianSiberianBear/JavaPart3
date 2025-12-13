package ru.hogwarts.school.service;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentToDto;
import ru.hogwarts.school.repository.StudentRepository;

import java.util.Collection;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository repository;
    private final StudentMapper mapper;

    public StudentService(StudentMapper mapper, StudentRepository repository) {
        this.mapper = mapper;
        this.repository = repository;
    }

    public StudentToDto create(StudentFromDto dto) {
        Student student = mapper.toEntity(dto);
        student = repository.save(student);
        return mapper.toDto(student);
    }

    public Optional<StudentToDto> read(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<StudentToDto> update(Long id, StudentFromDto dto) {
        return repository.findById(id)
                .map(student -> {
                    mapper.updateStudentFromDto(dto, student);
                    return mapper.toDto(student);
                });
    }


    public void delete(long id) {
        repository.deleteById(id);
    }

    public Collection<Student> getAllStudents() {
        return repository.findAll(Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
    }

    public Collection<Student> getAllStudentsByAge(int age) {
        return repository.findByAge(age, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
    }
}
