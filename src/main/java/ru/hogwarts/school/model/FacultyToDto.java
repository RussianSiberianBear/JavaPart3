package ru.hogwarts.school.model;

public record FacultyToDto(
        Long id,
        String name,
        String color
) {
    public Object getName() {
        return name;
    }
    public Object getColor() {
        return color;
    }
    public Object getId() {
        return id;
    }
}

