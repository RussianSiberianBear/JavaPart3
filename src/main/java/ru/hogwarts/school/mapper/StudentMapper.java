package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentToDto;

@Mapper(componentModel = "spring")
public interface StudentMapper {

    Student toEntity(StudentFromDto dto);

    StudentToDto toDto(Student student);

    void updateStudentFromDto(
            StudentFromDto dto,
            @MappingTarget Student student
    );

}

