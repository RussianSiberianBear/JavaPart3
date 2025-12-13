package ru.hogwarts.school.model;

public record StudentDTO(
        Long id,
        String name,
        String family,
        int age
) {}
