package ru.hogwarts.school.service;

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

    public StudentService(StudentMapper mapper, StudentRepository repository, AvatarRepository avatarRepository) {
        this.mapper = mapper;
        this.repository = repository;
        this.avatarRepository = avatarRepository;
    }

    public StudentToDto create(StudentFromDto dto) {
        Student student = mapper.toEntity(dto);
        student = repository.save(student);
        return mapper.toDto(student);
    }

    public Optional<StudentToDto> read(Long id) {
        return repository.findById(id)
                .map(mapper::toDto);
    }

    @Transactional
    public Optional<StudentToDto> update(Long id, StudentFromDto dto) {
        return repository.findById(id)
                .map(student -> {
                    mapper.updateStudentFromDto(dto, student);
                    return mapper.toDto(student);
                });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }

    public Collection<StudentToDto> getAllStudents() {

        Collection<Student> students = repository.findAll(Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));

        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByAge(int age) {
        Collection<Student> students = repository.findByAge(age, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<StudentToDto> getAllStudentsByAgeBetween(int ageMin, int ageMax) {
        Collection<Student> students = repository.findByAgeBetween(ageMin, ageMax, Sort.by(
                Sort.Order.asc("family"),
                Sort.Order.asc("name"),
                Sort.Order.desc("age")
        ));
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    public Collection<String> getFacultyNameByStudentName(String name) {
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
        Collection<Student> students = repository.findByFacultyColorContainingIgnoreCase(color, Sort.by(
                Sort.Order.asc("name")));
        if (students.isEmpty()) {
            throw new FacultyNotFoundException(color);
        }

        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }

    @Transactional
    public Avatar findAvatar(long studentId) {
        return avatarRepository.findByStudentId(studentId).orElseThrow(() -> new StudentNotFoundException("Аватар с таким id студента не найден!"));
    }

    @Transactional
    public boolean uploadAvatar(Long studentId, MultipartFile file) throws IOException {

        Student student = repository.findById(studentId).orElse(null);
        if (student == null) return false;

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
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private byte[] generateImagePreview(Path filePath) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(filePath.toFile());
        int height = bufferedImage.getHeight()/(bufferedImage.getWidth()/100);
        int width = 100;
        BufferedImage resizedImage = new BufferedImage(width, height, bufferedImage.getType());
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.drawImage(bufferedImage, 0, 0, width, height, null);
        g2d.dispose();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, getExtension(filePath.getFileName().toString()), os);
        return os.toByteArray();
    }

    public String countStudents(){
        return repository.countStudents();
    }

    public String avgAgeStudents(){
        return repository.avgAgeStudents();
    }

    public Collection<StudentToDto>getLast5Student(){
        Collection<Student> students = repository.find5LastStudents();
        return students.stream().map(mapper::toDto).collect(Collectors.toList());
    }
}
