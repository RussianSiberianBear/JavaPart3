package ru.hogwarts.school.exception;

public class FacultyNotFoundException extends RuntimeException {
    public FacultyNotFoundException(Long id) {
        super("Факультет с id " + id + " не найден");
    }

    public FacultyNotFoundException(String color) {
        super("Факультет с цветом " + color + " не найден");
    }
}