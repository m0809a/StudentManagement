package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

  @Test
  void 受講生詳細の一覧検索が実行できて空のリストがかえってくること() throws Exception {
    when(service.getAllStudent()).thenReturn(List.of(new StudentDetail()));

    mockMvc.perform(get("/students"))
        .andExpect(status().isOk())
        .andExpect(content().json("[{\"student\":null,\"studentsCourseList\":null}]"));

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
    void 受講生詳細の受講生でIDに数字以外を用いた時に入力チェックに掛かること() {
      Student student = new Student();
      student.setId("テストです");
      student.setName("あいうえお");
      student.setKanaName("アイウエオ");
      student.setEmail("test@example.com");

      Set<ConstraintViolation<Student>> violations = validator.validate(student);

      assertThat(violations.size()).isEqualTo(1);
      assertThat(violations).extracting("message")
          .containsOnly("数字のみ入力してください。");
    }



  }