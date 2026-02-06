package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

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
  void 受講コースの全件検索が行えること(){
    List<StudentCourse> actual = sut.findAllActiveCourses();
    assertThat(actual.size()).isEqualTo(5);
  }

  @Test
  void IDによる受講生検索が行えること(){
    Student student = sut.findStudentById("S000001");
    assertThat(student.getId()).isEqualTo("S000001");


  }

  @Test
  void 受講生IDに紐付く受講コース情報が取得できること(){
    List<StudentCourse> actual = sut.findCoursesByStudentId("S000001");
    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).getStudentId()).isEqualTo("S000001");
  }

  @Test
  void 存在しない受講生IDではコース情報が0件で返ること(){
    List<StudentCourse> actual = sut.findCoursesByStudentId("S999999");
    assertThat(actual).isEmpty();
  }


  @Test
  void 最大の受講生IDとしてS000005が取得出来ること(){
    String actual = sut.findMaxStudentId();
    assertThat(actual).isEqualTo("S000005");
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

  @Test
  void 受講生IDに紐付くコース情報が登録できること(){
    StudentCourse course = new StudentCourse();
    course.setId("C999999");
    course.setStudentId("S000001");
    course.setCourseName("テストコース");

    sut.insertStudentCourses(course);

    List<StudentCourse> actual = sut.findCoursesByStudentId("S000001");
    assertThat(actual).anySatisfy(c -> {
      assertThat(c.getId()).isEqualTo("C999999");
      assertThat(c.getCourseName()).isEqualTo("テストコース");
      assertThat(c.getStudentId()).isEqualTo("S000001");
    });
  }

  @Test
  void 受講生情報の更新が出来ること(){
    Student student = new Student();
    student.setId("S000001");
    student.setName("更新テスト");
    student.setKanaName("コウシンテスト");
    student.setNickname("タロ");
    student.setEmail("ymd@gmail.com");
    student.setAddress("東京都千代田区");
    student.setAge(34);
    student.setGender("男");
    student.setRemark("");
    student.setDeleted(false);

    sut.updateStudent(student);

    Student actual = sut.findStudentById("S000001");
    assertThat(actual.getName()).isEqualTo("更新テスト");
    assertThat(actual.getKanaName()).isEqualTo("コウシンテスト");
  }

  @Test
  void 指定した受講生コース情報をキャンセル出来ること(){
    StudentCourse course = new StudentCourse();
    course.setId("C000001");

    sut.updateStudentCourseDeleted(course);

    List<StudentCourse> actual = sut.findCoursesByStudentId("S000001");
    assertThat(actual).anySatisfy(c -> {
      assertThat(c.getId()).isEqualTo("C000001");
      assertThat(c.isDeleted()).isTrue();
    });

  }





}