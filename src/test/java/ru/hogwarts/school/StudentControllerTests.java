package ru.hogwarts.school;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import ru.hogwarts.school.controller.StudentController;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.StudentToDto;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StudentControllerTests {

    @LocalServerPort
    private int port;

    @Autowired
    private StudentController studentController;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl() {
        return "http://localhost:" + port + "/student";
    }

    @Test
    void contextLoadsStudentController() throws Exception {
        assertThat(studentController).isNotNull();
    }

    @Test
    void getStudentByIdNotFound() throws Exception {
        Long id = 1000000L;
        ResponseEntity<Student> getResponse = restTemplate.getForEntity(
                baseUrl() + "/{id}", Student.class, id
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @Transactional
    void createStudentFromDtoAndDeleteStudent() throws Exception {
        StudentFromDto dto = new StudentFromDto();
        dto.setAge(21);
        dto.setFamily("qwerty");
        dto.setName("asd");

        ResponseEntity<Student> response = restTemplate.postForEntity(
                baseUrl(),
                dto,
                Student.class
        );

        Long createdId = response.getBody().getId();
        assertThat(createdId).isNotNull();
        assertThat(response.getBody().getName()).isEqualTo(dto.getName());
        assertThat(response.getBody().getFamily()).isEqualTo(dto.getFamily());
        assertThat(response.getBody().getAge()).isEqualTo(dto.getAge());

        // Удаляем созданную сущность
        restTemplate.delete(baseUrl() + "/" + createdId);

        // Проверяем, что сущность удалена
        ResponseEntity<Student> getResponse = restTemplate.getForEntity(baseUrl() + "/" + createdId,
                Student.class
        );
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getStudentById() throws Exception {
        Student student = new Student();
        student.setId(2L);
        student.setName("Иван");
        student.setFamily("Иванов");
        student.setAge(19);

        ResponseEntity<Student> response = this.restTemplate.getForEntity(
                baseUrl() + "/" + student.getId(),
                Student.class
        );
        assertThat(response.getStatusCode())
                .isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(student);
    }

    @Test
    void getStudentsByAge() throws Exception {
        int age = 19;
        assertThat(restTemplate.getForEntity(baseUrl() + "/age/{age}", Collection.class, age)).isNotNull();
    }

    @Test
    void getStudentsByAgeBetween() throws Exception {
        int ageMin = 19;
        int ageMax = 21;
        assertThat(restTemplate.getForEntity(baseUrl() + "/age?min={ageMin}&max={ageMax}", Collection.class, ageMin, ageMax)).isNotNull();
    }

    @Test
    void getAllStudents() throws Exception {
        assertThat(restTemplate.getForEntity(baseUrl() + "/all", StudentToDto[].class)).isNotNull();
    }

    @Test
    void getFacultyNameByStudentName() throws Exception {
        String name = "Иван";
        String facultyName = "Факультет иностранных языков";
        String[] f = restTemplate.getForEntity(baseUrl() + "/faculty/{name}", String[].class, name).getBody();
        assertThat(f[0]).isEqualTo(facultyName);
    }

    @Test
    void getStudentsByFacultyColor() throws Exception {
        String color = "Красный";
        assertThat(restTemplate.getForEntity(baseUrl() + "/faculty/{name}", StudentToDto[].class, color)).isNotNull();
    }
}
