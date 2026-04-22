ALTER TABLE student
    ADD CHECK (age >= 16);

ALTER TABLE student
    ALTER COLUMN name SET NOT NULL,
    ADD UNIQUE (name);

ALTER TABLE faculty
    ADD constraint faculty_color_name UNIQUE (color, name);

ALTER TABLE student
    ALTER COLUMN age SET DEFAULT 20;