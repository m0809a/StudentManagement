package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentCourseInfo;
import raisetech.StudentManagement.domain.StudentSearchCondition;

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
    Student student = new Student(
        "S999999",
    "てすと",
    "テスト",
    "テス",
    "test@example.com",
    "東京都",
    33,
    "男",
    "",
    false
    );

    sut.insertStudent(student);

    List<Student> actual = sut.findAllActiveStudents();

    assertThat(actual.size()).isEqualTo(6);
  }

  @Test
  void 受講生IDに紐付くコース情報が登録できること() {
    StudentCourse course = new StudentCourse();
    course.setId("C999999");
    course.setStudentId("S000001");
    course.setCourseName("テストコース");

    sut.insertStudentCourses(course);

    List<StudentCourse> actual = sut.findCoursesByStudentId("S000001");

    StudentCourse expected = new StudentCourse();
    expected.setId("C999999");
    expected.setStudentId("S000001");
    expected.setCourseName("テストコース");

    assertThat(actual).contains(expected);
  }


  @Test
  void 受講生情報の更新が出来ること(){
    Student student = new Student(
    "S000001",
    "更新テスト",
    "コウシンテスト",
    "タロ",
    "ymd@gmail.com",
    "東京都千代田区",
    34,
    "男",
    "",
    false
    );

    sut.updateStudent(student);

    Student actual = sut.findStudentById("S000001");
    assertThat(actual.getName()).isEqualTo("更新テスト");
    assertThat(actual.getKanaName()).isEqualTo("コウシンテスト");
  }


  @Test
  void studentIdとcourseIdからコース情報とステータスが取得できること() {
    StudentCourseInfo actual = sut.findStudentCourseInfo("S000001", "C000001");

    assertThat(actual).isNotNull();
    assertThat(actual.getStudentId()).isEqualTo("S000001");
    assertThat(actual.getCourseId()).isEqualTo("C000001");
    assertThat(actual.getStatus()).isEqualTo("TEMP");
  }

  @Test
  void studentIdとcourseIdからstudentCourseIdが取得できること() {
    String actual = sut.findStudentCourseId("S000001", "C000001");
    assertThat(actual).isEqualTo("SC000001");
  }

  @Test
  void 申込状況が更新できること() {
    sut.updateCourseStatus("SC000001", "FORMAL");

    StudentCourseInfo actual = sut.findStudentCourseInfo("S000001", "C000001");
    assertThat(actual.getStatus()).isEqualTo("FORMAL");
  }

  @Test
  void 申込状況の論理削除ができてcourseInfoのstatusがnullになること() {
    sut.updateCourseStatusDeleted("SC000001");

    StudentCourseInfo actual = sut.findStudentCourseInfo("S000001", "C000001");
    assertThat(actual).isNotNull();
    assertThat(actual.getStatus()).isNull();
  }

  @Test
  void name指定で部分一致する学生が返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setName("山田");

    List<Student> actual = sut.searchStudents(cond);

    assertThat(actual).hasSize(1);
    assertThat(actual.get(0).getName()).contains("山田");
  }

  @Test
  void gender指定で該当学生が返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setGender("女");

    List<Student> actual = sut.searchStudents(cond);

    assertThat(actual)
        .allSatisfy(s -> assertThat(s.getGender()).isEqualTo("女"));
  }

  @Test
  void ageGroup指定で20代が返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setAgeGroup("TWENTIES");

    List<Student> actual = sut.searchStudents(cond);

    assertThat(actual)
        .allSatisfy(s ->
            assertThat(s.getAge()).isBetween(20, 29)
        );
  }

  @Test
  void courseId指定で該当学生が返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setCourseId("C000001");

    List<Student> actual = sut.searchStudents(cond);

    assertThat(actual).anySatisfy(s -> assertThat(s.getId()).isEqualTo("S000001"));
  }

  @Test
  void status指定で該当学生が返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setStatus("TEMP");

    List<Student> actual = sut.searchStudents(cond);

    assertThat(actual).hasSize(5);
  }

  @Test
  void courseId指定で該当コースが返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setCourseId("C000001");

    List<StudentCourse> actual = sut.searchStudentCourses(cond);

    assertThat(actual).anySatisfy(c -> assertThat(c.getCourseId()).isEqualTo("C000001"));
  }

  @Test
  void status指定で該当コースが返ること() {
    StudentSearchCondition cond = new StudentSearchCondition();
    cond.setStatus("TEMP");

    List<StudentCourse> actual = sut.searchStudentCourses(cond);

    assertThat(actual).hasSize(5);
  }




}