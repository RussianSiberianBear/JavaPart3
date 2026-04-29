package ru.hogwarts.school.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.hogwarts.school.exception.FacultyNotFoundException;
import ru.hogwarts.school.exception.StudentNotFoundException;
import ru.hogwarts.school.mapper.StudentMapper;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.StudentFromDto;
import ru.hogwarts.school.model.Student;
import ru.hogwarts.school.model.StudentToDto;
import ru.hogwarts.school.repository.AvatarRepository;
import ru.hogwarts.school.repository.StudentRepository;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
public class StudentService {

    @Value("${path.to.avatars.folder}")
    private String avatarsDir;
    private final StudentRepository repository;
    private final AvatarRepository avatarRepository;
    private final StudentMapper mapper;

    private static final Logger logger = LoggerFactory.getLogger(StudentService.class);

    public StudentService(StudentMapper mapper, StudentRepository repository, AvatarRepository avatarRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.avatarRepository = avatarRepository;
    }

    public StudentToDto create(StudentFromDto dto) {
        logger.info("Was invoked method for create student");
        Student student = mapper.toEntity(dto);
        student = repository.save(student);
        return mapper.toDto(student);
    }

    public Optional<StudentToDto> read(Long id) {
        logger.info("Was invoked method for read student");
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<StudentToDto> update(Long id, StudentFromDto dto) {
        logger.info("Was invoked method for update student");
        return repository.findById(id)
                .map(student -> {
                    mapper.updateStudentFromDto(dto, student);
                    return mapper.toDto(student);
                });
    }

    public boolean delete(Long id) {
        logger.info("Was invoked method for delete student");
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            logger.error("There is not student with id = " + id);
            return false;
        }
    }

    public Collection<StudentToDto> getAllStudents() {
        logger.info("Was invoked method for getAllStudents");
        Collection<Student> students = repository.findAll(Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));

        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByAge(int age) {
        logger.info("Was invoked method for getAllStudentsByAge age={}",age);
        Collection<Student> students = repository.findByAge(age, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByAgeBetween(int ageMin, int ageMax) {
        logger.info("Was invoked method for getAllStudentsByAgeBetween ageMin={}, ageMax={}", ageMin, ageMax);
        Collection<Student> students = repository.findByAgeBetween(ageMin, ageMax, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<String> getFacultyNameByStudentName(String name) {
        logger.info("Was invoked method for getFacultyNameByStudentName student name = " + name);
        Collection<Student> students;
        students = repository.findByNameIgnoreCase(name, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name")));

        return students
                .stream()
                .map(student -> student.getFaculty().getName())  // Из студента получаем его факультет и берем name
                .collect(Collectors.toList());
    }

    public Collection<StudentToDto> findStudentsByFacultyColorContainingIgnoreCase(String color) {
        logger.info("Was invoked method for findStudentsByFacultyColorContainingIgnoreCase color=" + color);
        Collection<Student> students = repository.findByFacultyColorContainingIgnoreCase(color, Sort.by(
                Sort.Order.asc("name")));
        if (students.isEmpty()) {
            logger.error("There is not student with faculty  = " + color);
            throw new FacultyNotFoundException(color);
        }

        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Avatar findAvatar(long studentId) {
        Optional<Avatar> avatar = avatarRepository.findByStudentId(studentId);
        if (avatar.isEmpty()) {
            logger.error("There is not avatar with studentId = " + studentId);
            throw new StudentNotFoundException("Аватар с таким id студента не найден!");
        }
        return avatar.get();
    }

    @Transactional
    public boolean uploadAvatar(Long studentId, MultipartFile file) throws IOException {
        logger.info("Was invoked method for uploadAvatar studentId =" + studentId);
        Student student = repository.findById(studentId).orElse(null);
        if (student == null) {
            logger.error("There is not student with id = " + studentId);
            return false;
        }

        Path filePath = Path.of(avatarsDir, studentId + "." + getExtension(file.getOriginalFilename()));
        Files.createDirectories(filePath.getParent());
        Files.deleteIfExists(filePath);

        try (InputStream is = file.getInputStream();
             OutputStream os = Files.newOutputStream(filePath, CREATE_NEW);
             BufferedInputStream bis = new BufferedInputStream(is, 1024);
             BufferedOutputStream bos = new BufferedOutputStream(os, 1024)
        ) {
            bis.transferTo(bos);
        }

        Avatar avatar = avatarRepository.findByStudentId(studentId).orElseGet(Avatar::new);
        avatar.setStudent(student);
        avatar.setFilePath(filePath.toString());
        avatar.setFileSize(file.getSize());
        avatar.setMediaType(file.getContentType());
        avatar.setData(generateImagePreview(filePath));

        avatarRepository.save(avatar);
        return true;
    }

    private String getExtension(String fileName) {
        logger.info("Was invoked method for getExtension filename = " + fileName);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        logger.info("Was invoked method for generateImagePreview " + filePath.toString());
        BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
        int height = bufferedImage.getHeight() / (bufferedImage.getWidth() / 100);
        int width = 100;
        BufferedImage resizedImage = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, width, height, null);
        g2d.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, getExtension(filePath.getFileName().toString()), os);
        return os.toByteArray();
    }

    public String countStudents() {
        logger.info("Was invoked method for countStudents");
        return repository.countStudents();
    }

    public String avgAgeStudents() {
        logger.info("Was invoked method for avgAgeStudents");
        return repository.avgAgeStudents();
    }

    public Collection<StudentToDto> getLast5Student() {
        logger.info("Was invoked method for getLast5Student");
        Collection<Student> students = repository.find5LastStudents();
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<String> getAllStudentsWithStartnameWithAandSorting() {
        logger.info("Was invoked method for startNameWithAandSorting");
        Collection<Student> students = repository.findAll();
        var а = students
                .parallelStream()
                .map(s -> s.getName().toUpperCase())
                .filter(n -> n.startsWith("А"))
                .sorted()
                .toList();
        return а;
    }

    public int newMethodAvgAgeStudents() {
        Collection<Student> students = repository.findAll();
        // Думаю, что тут не оправдан параллелизм, так как тут по сути всего пара операций-
        // суммирование возраста и деление на количество студентов и выигрыш может быть
        // на очень большом кол-ве студентов
        int аvgAge = (int) students
                .stream()
                .mapToInt(Student::getAge)
                .average().orElse(0);

        return аvgAge;
    }

}
