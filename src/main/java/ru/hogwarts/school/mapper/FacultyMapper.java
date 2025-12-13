package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.hogwarts.school.model.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    Faculty toEntity(CreateFacultyDTO dto);

    FacultyDTO toDto(Faculty faculty);

    void updateFacultyFromDto(
            CreateFacultyDTO dto,
            @MappingTarget Faculty faculty
    );
}
