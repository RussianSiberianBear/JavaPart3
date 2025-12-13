package ru.hogwarts.school.model;

public record StudentToDto(
        Long id,
        String name,
        String family,
        int age
) {}
