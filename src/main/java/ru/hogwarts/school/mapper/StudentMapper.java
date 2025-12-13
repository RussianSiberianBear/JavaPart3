package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.hogwarts.school.model.CreateStudentDTO;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentDTO;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toEntity(CreateStudentDTO dto);

    StudentDTO toDto(Student student);

    void updateStudentFromDto(
            CreateStudentDTO dto,
            @MappingTarget Student student
    );

}

