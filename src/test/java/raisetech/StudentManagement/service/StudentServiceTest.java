package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.exception.StudentNotFoundException;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private  StudentService sut;

  @BeforeEach
  void before(){
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索＿RepositoryとConverterの処理が適切に呼び出せていること(){
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();

    when(repository.findAllActiveStudents()).thenReturn(studentList);
    when(repository.findAllActiveCourses()).thenReturn(studentCourseList);

    sut.getAllStudent();

    verify(repository, times(1)).findAllActiveStudents();
    verify(repository, times(1)).findAllActiveCourses();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void  受講生検索＿IDが存在する場合＿受講生とコースを取得しStudentDetailを返すこと(){
    // Arrange
    String id = "S999999";
    Student student = new Student();
    student.setId(id);
    List<StudentCourse> courses = new ArrayList<>();

    when(repository.findStudentById(id)).thenReturn(student);
    when(repository.findCoursesByStudentId(id)).thenReturn(courses);

    // Act
    StudentDetail actual = sut.getStudentDetail(id);

    // Check
    assertEquals(student, actual.getStudent());
    assertEquals(courses, actual.getStudentsCourseList());

    verify(repository, times(1)).findStudentById(id);
    verify(repository, times(1)).findCoursesByStudentId(id);
  }

  @Test
  void 受講生検索_IDが存在しない場合_StudentNotFoundExceptionが発生すること() {
    // Arrange
    String id = "S999999";
    when(repository.findStudentById(id)).thenReturn(null);

    // Act & Assert
    assertThrows(StudentNotFoundException.class,
        () -> sut.getStudentDetail(id));

    // Assert（後続処理が呼ばれないこと）
    verify(repository, times(1)).findStudentById(id);
    verify(repository, never()).findCoursesByStudentId(any());
  }



  @Test
  void 受講生詳細の登録_既存IDが無い場合_受け取った受講生情報とコース情報を登録しStudentDetailを返すこと(){
    // Arrange
    Student student = new Student();
    StudentDetail detail = new StudentDetail(student, new ArrayList<>());
    StudentCourse course = new StudentCourse();
    course.setCourseName("Java入門コース");
    detail.getStudentsCourseList().add(course);

    when(repository.findMaxStudentId()).thenReturn(null);

    // Act
    StudentDetail actual = sut.registerStudentWithNewId(detail);

     // Check
    assertEquals("S000001", student.getId());
    assertEquals("S000001", actual.getStudent().getId());
    assertEquals("S000001", course.getStudentId());
    assertEquals("C000001", course.getId());
    assertNotNull(course.getCourseStartAt());
    assertNotNull(course.getCourseEndAt());

    verify(repository, times(1)).findMaxStudentId();
    verify(repository, times(1)).insertStudent(student);
    verify(repository, times(1)).insertStudentCourses(course);

  }

  @Test
  void 受講生詳細の登録_既存IDがある場合_受け取った受講生情報とコース情報を登録しStudentDetailを返すこと() {
    // Arrange
    Student student = new Student();
    StudentDetail detail = new StudentDetail(student, new ArrayList<>());
    StudentCourse course = new StudentCourse();
    course.setCourseName("Java入門コース");
    detail.getStudentsCourseList().add(course);

    when(repository.findMaxStudentId()).thenReturn("S000010");

    // Act
    StudentDetail actual = sut.registerStudentWithNewId(detail);

    // Assert
    assertEquals("S000011", student.getId());
    assertEquals("S000011", actual.getStudent().getId());
    assertEquals("S000011", course.getStudentId());
    assertEquals("C000011", course.getId());
    assertNotNull(course.getCourseStartAt());
    assertNotNull(course.getCourseEndAt());

    verify(repository, times(1)).findMaxStudentId();
    verify(repository, times(1)).insertStudent(student);
    verify(repository, never()).insertStudentCourses(any());
  }


  @Test
  void コース名がJava入門コースの場合＿対応するコースIDを返すこと() {
    // Act
    String actual = sut.getCourseIdByName("Java入門コース");

    // Assert
    assertEquals("C000001", actual);
  }



}