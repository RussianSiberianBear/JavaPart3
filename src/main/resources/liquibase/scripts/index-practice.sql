-- liquibase formatted sql

-- changeset abaturin:1

CREATE INDEX idx_student_name ON student(name);

-- changeset abaturin:2

CREATE INDEX idx_faculty_name_color ON faculty(name,color);