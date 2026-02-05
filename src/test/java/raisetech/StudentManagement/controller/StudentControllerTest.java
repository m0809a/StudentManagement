package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.exception.StudentNotFoundException;
import raisetech.StudentManagement.service.StudentService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;


  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストがかえってくること() throws Exception {
    when(service.getAllStudent()).thenReturn(List.of());

    mockMvc.perform(get("/students"))
        .andExpect(status().isOk())
        .andExpect(content().json("[]"));

    verify(service, times(1)).getAllStudent();
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生詳細の受講生でIDに字以外を用いた時に入力チェックに掛かること() {
    Student student = new Student();
    student.setId("テストです");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(1);
    assertThat(violations).extracting("message")
        .containsOnly("IDはS+6桁で入力してください。");
  }

  @Test
  void 受講生詳細のidによる検索が実行できてStudentdetailが返ってくること() throws Exception {
    StudentDetail detail = new StudentDetail();
    when(service.getStudentDetail("S999999")).thenReturn(detail);

    mockMvc.perform(get("/student/S999999"))
        .andExpect(status().isOk())
        .andExpect(content().json("{}"));

    verify(service, times(1)).getStudentDetail("S999999");
  }


  @Test
  void 受講生登録が実行できてStudentDetailが返ること() throws Exception {
    // リクエスト側
    Student requestStudent = new Student();
    requestStudent.setName("てすと");
    requestStudent.setKanaName("テスト");
    requestStudent.setEmail("test@example.com");

    StudentDetail request = new StudentDetail();

    Student responseStudent = new Student();
    responseStudent.setId("S000001");
    responseStudent.setName("てすと");
    responseStudent.setKanaName("テスト");
    responseStudent.setEmail("test@example.com");

    StudentDetail response = new StudentDetail(responseStudent, List.of());

    when(service.registerStudentWithNewId(any(StudentDetail.class))).thenReturn(response);

    mockMvc.perform(
            post("/student/register")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().json("{}"))
        .andExpect(jsonPath("$.student.id").value("S000001"));

  }

  @Test
  void 受講生更新が実行できてレ成功レスポンスがかえってくること() throws Exception {
    // Arrange（更新用のリクエスト）
    Student student = new Student();
    student.setId("S999999");
    student.setName("更新後の名前");
    student.setKanaName("コウシンゴノナマエ");
    student.setEmail("updated@example.com");

    StudentDetail request = new StudentDetail(student, List.of());

    // Act & Assert
    mockMvc.perform(
            put("/student/update")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isOk())
        .andExpect(content().string("更新処理が成功しました。"));

    verify(service, times(1)).updateStudent(any(StudentDetail.class));
  }


  @Test
  void 受講生更新_名前が空の場合入力チェックエラーが返ること() throws Exception {
    // Arrange（name空でNotBlank違反）
    Student student = new Student();
    student.setId("S000001");
    student.setName(""); // エラー
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");

    StudentDetail request = new StudentDetail(student, List.of());

    // Act & Assert
    mockMvc.perform(
            put("/student/update")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(request))
        )
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("名前は必須です。")));

    // バリデーションで弾かれるため中止
    verify(service, never()).updateStudent(any());
  }

  @Test
  void 受講生_名前が空のときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("");
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("名前は必須です。");
  }

  @Test
  void 受講生_フリガナが空のときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName(""); // NG
    student.setEmail("test@example.com");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("フリがなは必須です。");
  }

  @Test
  void 受講生_メールが空のときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("メールアドレスは必須です。");
  }

  @Test
  void 受講生_メール形式が不正のときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("testexample");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("メールアドレスの形式が正しくありません。");
  }

  @Test
  void 受講生_年齢がマイナスのときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");
    student.setAge(-1);

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("年齢は0以上で入力してください。");
  }

  @Test
  void 受講生_年齢が120歳以上のときエラーになること() {
    Student student = new Student();
    student.setId("S999999");
    student.setName("あいうえお");
    student.setKanaName("アイウエオ");
    student.setEmail("test@example.com");
    student.setAge(121);

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations).extracting("message")
        .contains("年齢が不正です。");
  }


}