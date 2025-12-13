package ru.hogwarts.school.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class CreateStudentDTO {
    @NotBlank(message = "{student.name.notblank}")
    private String name;

    @NotBlank(message = "{student.family.notblank}")
    private String family;

    @Min(value = 18, message = "{student.age.min18}")
    private int age;

    public CreateStudentDTO(String name, String family, int age) {
        this.name = name;
        this.family = family;
        this.age = age;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamily() {
        return this.family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public int getAge() {
        return this.age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
