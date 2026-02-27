package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.StudentCourseStatus;
import raisetech.StudentManagement.domain.StudentCourseInfo;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.domain.StudentSearchCondition;
import raisetech.StudentManagement.exception.StudentNotFoundException;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before() {
    sut = new StudentService(repository, converter);
  }

  @Test
  void 受講生詳細の一覧検索＿RepositoryとConverterの処理が適切に呼び出せていること() {
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
  void 受講生検索＿IDが存在する場合＿受講生とコースを取得しStudentDetailを返すこと() {
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

    verify(repository, times(1)).findStudentById(id);
    verify(repository, never()).findCoursesByStudentId(any());
  }


  @Test
  void 受講生詳細の登録_既存IDが無い場合_受け取った受講生情報とコース情報を登録しStudentDetailを返すこと() {
    // Arrange
    Student student = new Student();
    StudentDetail detail = new StudentDetail(student, new ArrayList<>());
    StudentCourse course = new StudentCourse();
    course.setCourseName("Java入門コース");
    detail.getStudentsCourseList().add(course);

    when(repository.findMaxStudentId()).thenReturn(null);

    // Act
    StudentDetail actual = sut.registerStudentWithNewId(detail);

    // Assert
    assertEquals("S000001", actual.getStudent().getId());
    assertEquals("S000001", course.getStudentId());

    // insertStudent
    verify(repository, times(1)).insertStudent(student);

    // insertStudentCoursesを検証（idはUUID、courseIdがC000001）
    ArgumentCaptor<StudentCourse> courseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    verify(repository, times(1)).insertStudentCourses(courseCaptor.capture());

    StudentCourse insertedCourse = courseCaptor.getValue();
    assertEquals("S000001", insertedCourse.getStudentId());
    assertEquals("Java入門コース", insertedCourse.getCourseName());
    assertEquals("C000001", insertedCourse.getCourseId());
    assertNotNull(insertedCourse.getId());
    assertNotNull(insertedCourse.getCourseStartAt());
    assertNotNull(insertedCourse.getCourseEndAt());
    assertEquals(insertedCourse.getCourseStartAt().plusYears(1), insertedCourse.getCourseEndAt());

    // insertCourseStatus の検証
    ArgumentCaptor<StudentCourseStatus> statusCaptor = ArgumentCaptor.forClass(
        StudentCourseStatus.class);
    verify(repository, times(1)).insertCourseStatus(statusCaptor.capture());

    StudentCourseStatus insertedStatus = statusCaptor.getValue();
    assertNotNull(insertedStatus.getStatusId());
    assertEquals(insertedCourse.getId(), insertedStatus.getStudentCourseId());
    assertEquals("TEMP", insertedStatus.getStatus());
    assertFalse(insertedStatus.isDeleted());


  }

  @Test
  void コース申込み情報検索＿正常＿受講生コース情報の申し込み状況を取得しstudentCourseInfoを返すこと(){
    StudentCourseInfo info = new StudentCourseInfo("S999999","テスト","C000001","Java入門コース","TEMP");
    when(repository.findStudentCourseInfo("S999999","C000001")).thenReturn(info);

    StudentCourseInfo actual = sut.getStudentCourseInfo("S999999", "C000001");

    assertEquals(info, actual);
    verify(repository, times(1)).findStudentCourseInfo("S999999", "C000001");
  }

  @Test
  void 受講生コース情報取得_infoがnullの場合_StudentNotFoundExceptionが発生すること() {
    // Arrange
    when(repository.findStudentCourseInfo("S888888", "C000001")).thenReturn(null);

    // Act & Assert
    StudentNotFoundException ex = assertThrows(
        StudentNotFoundException.class,
        () -> sut.getStudentCourseInfo("S888888", "C000001")
    );
    assertTrue(ex.getMessage().contains("該当する受講生/コースが見つかりません"));

    verify(repository, times(1)).findStudentCourseInfo("S888888", "C000001");
  }

  @Test
  void 受講生コース情報取得_statusがnullの場合_StudentNotFoundExceptionが発生すること() {
    // Arrange（statusだけnull）
    StudentCourseInfo info =
        new StudentCourseInfo("S000001", "山田太郎", "C000001", "Java入門コース", null);
    when(repository.findStudentCourseInfo("S000001", "C000001")).thenReturn(info);

    // Act  Assert
    StudentNotFoundException ex = assertThrows(
        StudentNotFoundException.class,
        () -> sut.getStudentCourseInfo("S000001", "C000001")
    );
    assertTrue(ex.getMessage().contains("申込状況が未登録です"));

    verify(repository, times(1)).findStudentCourseInfo("S000001", "C000001");
  }

  @Test
  void 受講生検索＿コースIDまたはステータス条件あり＿searchStudentCoursesが呼ばれること() {
    // Arrange
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setCourseId("C000001"); // コースID条件あり

    List<Student> students = List.of(new Student());
    List<StudentCourse> courses = List.of(new StudentCourse());
    List<StudentDetail> converted = List.of(new StudentDetail());

    when(repository.searchStudents(cond)).thenReturn(students);
    when(repository.searchStudentCourses(cond)).thenReturn(courses);
    when(converter.convertStudentDetails(students, courses)).thenReturn(converted);

    // Act
    List<StudentDetail> actual = sut.searchStudents(cond);

    // Assert
    assertEquals(converted, actual);
    verify(repository, times(1)).searchStudents(cond);
    verify(repository, times(1)).searchStudentCourses(cond);
    verify(repository, never()).findAllActiveCourses();
    verify(converter, times(1)).convertStudentDetails(students, courses);
  }

  @Test
  void 受講生検索＿コースIDもステータスも未指定＿findAllActiveCoursesが呼ばれること() {
    // Arrange
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setCourseId("");  // blank
    cond.setStatus(null);  // null

    List<Student> students = List.of(new Student());
    List<StudentCourse> courses = List.of(new StudentCourse());
    List<StudentDetail> converted = List.of(new StudentDetail());

    when(repository.searchStudents(cond)).thenReturn(students);
    when(repository.findAllActiveCourses()).thenReturn(courses);
    when(converter.convertStudentDetails(students, courses)).thenReturn(converted);

    // Act
    List<StudentDetail> actual = sut.searchStudents(cond);

    // Assert
    assertEquals(converted, actual);
    verify(repository, times(1)).searchStudents(cond);
    verify(repository, never()).searchStudentCourses(cond);
    verify(repository, times(1)).findAllActiveCourses();
    verify(converter, times(1)).convertStudentDetails(students, courses);
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
    assertEquals("S000011", actual.getStudent().getId());
    assertEquals("S000011", course.getStudentId());

    // insertStudent
    verify(repository, times(1)).insertStudent(student);

    // insertStudentCourses の中身を検証（idはUUID、courseIdがC000001）
    ArgumentCaptor<StudentCourse> courseCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    verify(repository, times(1)).insertStudentCourses(courseCaptor.capture());

    StudentCourse insertedCourse = courseCaptor.getValue();
    assertEquals("S000011", insertedCourse.getStudentId());
    assertEquals("Java入門コース", insertedCourse.getCourseName());
    assertEquals("C000001", insertedCourse.getCourseId());
    assertNotNull(insertedCourse.getId());
    assertNotNull(insertedCourse.getCourseStartAt());
    assertNotNull(insertedCourse.getCourseEndAt());
    assertEquals(insertedCourse.getCourseStartAt().plusYears(1), insertedCourse.getCourseEndAt());

    // insertCourseStatus
    ArgumentCaptor<StudentCourseStatus> statusCaptor = ArgumentCaptor.forClass(
        StudentCourseStatus.class);
    verify(repository, times(1)).insertCourseStatus(statusCaptor.capture());

    StudentCourseStatus insertedStatus = statusCaptor.getValue();
    assertNotNull(insertedStatus.getStatusId());
    assertEquals(insertedCourse.getId(), insertedStatus.getStudentCourseId());
    assertEquals("TEMP", insertedStatus.getStatus());
    assertFalse(insertedStatus.isDeleted());
  }


  @Test
  void コース名がJava入門コースの場合＿対応するコースIDを返すこと() {
    // Act
    String actual = sut.getCourseIdByName("Java入門コース");

    // Assert
    assertEquals("C000001", actual);
  }

  @Test
  void 受講生詳細の更新＿受講生キャンセルの場合＿受講生情報とそれに紐付くコース情報を論理削除すること() {
    // Arrange
    Student student = new Student();
    student.setId("S999999");
    student.setDeleted(true);

    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentsCourseList(List.of());

    StudentCourse existingCourse = new StudentCourse();
    existingCourse.setId("C999999");

    when(repository.findCoursesByStudentId("S999999")).thenReturn(List.of(existingCourse));

    //Act
    sut.updateStudent(detail);

    // Assert
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).findCoursesByStudentId("S999999");
    verify(repository, times(1)).updateStudentCourseDeleted(any(StudentCourse.class));
    verify(repository, times(1)).updateCourseStatusDeleted("C999999");

    verify(repository, never()).insertStudentCourses(any());
    verify(repository, never()).insertCourseStatus(any());
  }


  @Test
  void 受講生詳細の更新＿コース指定なし＿受講生情報のみ更新すること() {
    // Arrange
    Student student = new Student();
    student.setId("S999999");
    student.setDeleted(false);

    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentsCourseList(List.of());

    when(repository.findCoursesByStudentId("S999999")).thenReturn(List.of());

    // Act
    sut.updateStudent(detail);

    // Assert
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).findCoursesByStudentId("S999999");

    verify(repository, never()).updateStudentCourseDeleted(any());
    verify(repository, never()).insertStudentCourses(any());
  }

  @Test
  void 受講生詳細の更新＿コース変更の場合＿既存コースと申込状況を論理削除し新コースとTEMPを登録すること() {
    // Arrange
    Student student = new Student();
    student.setId("S999999");
    student.setDeleted(false);

    // 既存コース
    StudentCourse existingCourse = new StudentCourse();
    existingCourse.setId("C999999");
    existingCourse.setStudentId("S999999");
    existingCourse.setDeleted(false);

    when(repository.findCoursesByStudentId("S999999")).thenReturn(List.of(existingCourse));

    // 新規コース
    StudentCourse newCourse = new StudentCourse();
    newCourse.setCourseName("Java入門コース");

    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentsCourseList(List.of(newCourse));

    // Act
    sut.updateStudent(detail);

    // Assert
    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).findCoursesByStudentId("S999999");

    // 既存コースがdeleted=trueにされて渡されたか
    ArgumentCaptor<StudentCourse> deletedCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    verify(repository, times(1)).updateStudentCourseDeleted(deletedCaptor.capture());

    StudentCourse deletedArg = deletedCaptor.getValue();
    assertEquals("C999999", deletedArg.getId());
    assertTrue(deletedArg.isDeleted());
    verify(repository, times(1)).updateCourseStatusDeleted("C999999");

    // 新コースが正しい形でinsertされたか（idはUUID、courseIdがC000001）
    ArgumentCaptor<StudentCourse> insertCaptor = ArgumentCaptor.forClass(StudentCourse.class);
    verify(repository, times(1)).insertStudentCourses(insertCaptor.capture());

    StudentCourse inserted = insertCaptor.getValue();
    assertEquals("S999999", inserted.getStudentId());
    assertEquals("Java入門コース", inserted.getCourseName());
    assertEquals("C000001", inserted.getCourseId()); // ★ここを見る（idじゃない）
    assertNotNull(inserted.getId());
    assertNotNull(inserted.getCourseStartAt());
    assertNotNull(inserted.getCourseEndAt());
    assertEquals(inserted.getCourseStartAt().plusYears(1), inserted.getCourseEndAt());
    assertFalse(inserted.isDeleted());

    ArgumentCaptor<StudentCourseStatus> statusCaptor = ArgumentCaptor.forClass(
        StudentCourseStatus.class);
    verify(repository, times(1)).insertCourseStatus(statusCaptor.capture());

    StudentCourseStatus insertedStatus = statusCaptor.getValue();
    assertNotNull(insertedStatus.getStatusId());
    assertEquals(inserted.getId(), insertedStatus.getStudentCourseId());
    assertEquals("TEMP", insertedStatus.getStatus());
    assertFalse(insertedStatus.isDeleted());
  }

  @Test
  void コースステータス更新＿不正ステータスの場合＿IllegalArgumentExceptionが発生すること() {
    IllegalArgumentException ex = assertThrows(
        IllegalArgumentException.class,
        () -> sut.updateCourseStatus("S000001", "C000001", "NG")
    );
    assertTrue(ex.getMessage().contains("ステータスが不正です"));

    // validateStatusで落ちるのでRepositoryは呼ばれない
    verify(repository, never()).findStudentCourseId(any(), any());
    verify(repository, never()).updateCourseStatus(any(), any());
  }

  @Test
  void コースステータス更新＿studentCourseIdが見つからない場合＿StudentNotFoundExceptionが発生すること() {
    when(repository.findStudentCourseId("S000001", "C000001")).thenReturn(null);

    StudentNotFoundException ex = assertThrows(
        StudentNotFoundException.class,
        () -> sut.updateCourseStatus("S000001", "C000001", "TEMP")
    );
    assertTrue(ex.getMessage().contains("該当コースが存在しません"));

    verify(repository, times(1)).findStudentCourseId("S000001", "C000001");
    verify(repository, never()).updateCourseStatus(any(), any());
  }

  @Test
  void コースステータス更新＿正常＿updateCourseStatusが呼ばれること() {
    // Arrange
    when(repository.findStudentCourseId("S000001", "C000001")).thenReturn("UUID-123");

    // Act
    sut.updateCourseStatus("S000001", "C000001", "FORMAL");

    // Assert
    verify(repository, times(1)).findStudentCourseId("S000001", "C000001");
    verify(repository, times(1)).updateCourseStatus("UUID-123", "FORMAL");
  }

}