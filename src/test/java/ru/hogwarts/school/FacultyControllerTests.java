package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.controller.FacultyController;
import ru.hogwarts.school.model.Faculty;
import ru.hogwarts.school.model.FacultyFromDto;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FacultyControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private FacultyController facultyController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/faculty";
    }

    @Test
    void contextLoadsFacultyController() throws Exception {
        assertThat(facultyController).isNotNull();
    }

    @Test
    void getFacultyById() throws Exception {

        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Факультет иностранных языков");
        faculty.setColor("Красный");

        assertThat(this.restTemplate.getForObject(baseUrl() + "/" + faculty.getId(), Faculty.class).equals(faculty));
    }

    @Test
    void getFacultyByIdNotFound() throws Exception {

        long id = 100000L;
        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                baseUrl() + "/" + id,
                Faculty.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getFacultyByColor() throws Exception {
        Faculty faculty = new Faculty();
        faculty.setId(1L);
        faculty.setName("Факультет иностранных языков");
        faculty.setColor("Красный");

        assertThat(this.restTemplate.getForObject(baseUrl() + "/color/Красный", Faculty.class).equals(faculty));
    }

    @Test
    @Transactional
    void createFacultyFromDtoAndDeleteFaculty() throws Exception {
        FacultyFromDto dto = new FacultyFromDto();
        dto.setName("Наименование");
        dto.setColor("Цвет");

        // Попробуем все же с удалением, а не просто так:
        //    Assertions
        //        .assertThat(this.restTemplate.postForObject("http://localhost:" + port + "/faculty",dto,FacultyFromDto.class))
        //        .isNotNull();

        ResponseEntity<Faculty> response = restTemplate.postForEntity(
                baseUrl(),
                dto,
                Faculty.class
        );

        Long createdId = response.getBody().getId();
        assertThat(createdId).isNotNull();

        // Удаляем созданную сущность
        restTemplate.delete(baseUrl() + "/" + createdId);

        // Проверяем, что сущность удалена
        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                baseUrl() + "/" + createdId,
                Faculty.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    void updateFacultyFromDto() throws Exception {
        FacultyFromDto dto = new FacultyFromDto();
        dto.setName("Наименование");
        dto.setColor("Цвет");
        long id = 1;

        ResponseEntity<Faculty> getResponse = restTemplate.getForEntity(
                baseUrl() + "/" + id,
                Faculty.class
        );
        // Сохраняем старые значения
        String oldName = getResponse.getBody().getName();
        String oldColor = getResponse.getBody().getColor();
        // Обновляем
        restTemplate.put(baseUrl() + "/" + id, dto, Faculty.class);
        // Проверяем
        getResponse = restTemplate.getForEntity(
                baseUrl() + "/" + id,
                Faculty.class
        );
        assertThat(getResponse.getBody().getName()).isEqualTo(dto.getName());
        assertThat(getResponse.getBody().getColor()).isEqualTo(dto.getColor());

        // Восстанавливаем старые значения
        dto.setColor(oldColor);
        dto.setName(oldName);
        restTemplate.put(baseUrl() + "/" + id, dto, Faculty.class);

    }

}
