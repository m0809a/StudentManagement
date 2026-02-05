package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;

@MybatisTest
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること(){
    List<Student> actual = sut.findAllActiveStudents();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void IDによる受講生検索が行えること(){
    Student student = sut.findStudentById("S000001");
    assertThat(student.getId()).isEqualTo("S000001");
  }

  @Test
  void 受講生の登録が行えること(){
    Student student = new Student();
    student.setId("S999999");
    student.setName("てすと");
    student.setKanaName("テスト");
    student.setNickname("テス");
    student.setEmail("test@example.com");
    student.setAddress("東京都");
    student.setAge(33);
    student.setGender("男");
    student.setRemark("");
    student.setDeleted(false);

    sut.insertStudent(student);

    List<Student> actual = sut.findAllActiveStudents();

    assertThat(actual.size()).isEqualTo(6);
  }
}