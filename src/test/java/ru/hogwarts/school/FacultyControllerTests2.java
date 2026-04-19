package ru.hogwarts.school;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.FacultyFromDto;
import ru.hogwarts.school.model.FacultyToDto;
import ru.hogwarts.school.service.FacultyService;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(FacultyController.class)
public class FacultyControllerTests2 {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FacultyService facultyService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void getFacultyByIdNotFound() throws Exception {
        // Настраиваем поведение мока
        Mockito.when(facultyService.read(99999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/faculty/99999"))
                .andExpect(status().isNotFound());

        // Проверяем, что метод был вызван
        Mockito.verify(facultyService).read(99999L);
    }

    @Test
    void getFacultyByIdFound() throws Exception {
        String name = "Факультет иностранных языков";
        String color = "Красный";
        FacultyToDto dto = new FacultyToDto(1L, name, color);

        Mockito.when(facultyService.read(1L))
                .thenReturn(Optional.of(dto));

        mockMvc.perform(get("/faculty/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.color").value(color));
    }

    @Test
    void createFaculty() throws Exception {
        FacultyFromDto dto = new FacultyFromDto();
        dto.setColor("qwerty");
        dto.setName("asd");

        FacultyToDto toDto = new FacultyToDto(2L, dto.getName(), dto.getColor());

        Mockito.when(facultyService.create(any(FacultyFromDto.class)))
                .thenReturn(toDto);

        mockMvc.perform(post("/faculty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toDto.getId()))
                .andExpect(jsonPath("$.name").value(toDto.getName()))
                .andExpect(jsonPath("$.color").value(toDto.getColor()));
    }

    @Test
    void updateFaculty() throws Exception {
        String name = "Факультет иностранных языков";
        String color = "Красный";
        Long id = 1L;
        FacultyFromDto fromDto = new FacultyFromDto();
        fromDto.setName(name);
        fromDto.setColor(color);
        FacultyToDto toDto = new FacultyToDto(id, name, color);

        Mockito.when(facultyService.update(Mockito.eq(id), any(FacultyFromDto.class)))
                .thenReturn(Optional.of(toDto));

        mockMvc.perform(put("/faculty/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fromDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(toDto.getId()))
                .andExpect(jsonPath("$.name").value(toDto.getName()))
                .andExpect(jsonPath("$.color").value(toDto.getColor()));
    }

    @Test
    void deleteFaculty() throws Exception {
        Mockito.when(facultyService.delete(1L))
                .thenReturn(true);

        mockMvc.perform(delete("/faculty/1"))
                .andExpect(status().isOk());
    }

}
