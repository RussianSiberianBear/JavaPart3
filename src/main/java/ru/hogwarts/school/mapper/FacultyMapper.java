package ru.hogwarts.school.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import ru.hogwarts.school.model.*;

@Mapper(componentModel = "spring")
public interface FacultyMapper {

    Faculty toEntity(FacultyFromDto dto);

    FacultyToDto toDto(Faculty faculty);

    void updateFacultyFromDto(
            FacultyFromDto dto,
            @MappingTarget Faculty faculty
    );
}
