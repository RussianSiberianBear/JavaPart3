package ru.hogwarts.school.model;

import jakarta.validation.constraints.NotBlank;

public class FacultyFromDto {
    @NotBlank(message = "{faculty.name.notblank}")
    private String name;
    @NotBlank(message = "{faculty.color.notblank}")
    private String color;

    public FacultyFromDto(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public FacultyFromDto() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
