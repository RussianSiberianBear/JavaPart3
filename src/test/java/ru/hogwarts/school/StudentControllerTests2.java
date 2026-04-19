package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.StudentToDto;
import ru.hogwarts.school.service.StudentService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudentController.class)
public class StudentControllerTests2 {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getStudentByIdNotFound() throws Exception {
        // Настраиваем поведение мока
        Mockito.when(studentService.read(99999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/student/99999"))
                .andExpect(status().isNotFound());

        // Проверяем, что метод был вызван
        Mockito.verify(studentService).read(99999L);
    }

    @Test
    public void getStudentByIdFound() throws Exception {

        Long id = 99999L;
        String name = "name";
        String family = "family";
        int age = 18;

        StudentToDto student = new StudentToDto(id, name, family, age);

        // Настраиваем поведение мока
        Mockito.when(studentService.read(99999L))
                .thenReturn(Optional.of(student));

        mockMvc.perform(get("/student/99999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99999L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.family").value("family"))
                .andExpect(jsonPath("$.age").value(18));

    }

    @Test
    public void createStudent() throws Exception {
        String name = "name";
        String family = "family";
        int age = 18;
        Long id = 99999L;
        StudentFromDto student = new StudentFromDto(name, family, age);
        StudentToDto dto = new StudentToDto(id, name, family, age);

        Mockito.when(studentService.create(any(StudentFromDto.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.family").value(family))
                .andExpect(jsonPath("$.age").value(age));

    }

    @Test
    public void updateStudent() throws Exception {
        String name = "name";
        String family = "family";
        int age = 18;
        Long id = 1L;
        StudentFromDto student = new StudentFromDto(name, family, age);
        StudentToDto dto = new StudentToDto(id, name, family, age);

        Mockito.when(studentService.update(Mockito.eq(id),any(StudentFromDto.class)))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(put("/student/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(student)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.family").value(family))
                .andExpect(jsonPath("$.age").value(age));

    }

    @Test
    public void deleteStudent() throws Exception {
        Mockito.when(studentService.delete(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/student/1"))
                .andExpect(status().isOk());

    }
}
