package raisetech.StudentManagement.controller.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

class StudentConverterTest {

  private StudentConverter sut;

  @BeforeEach
  void before(){sut = new StudentConverter();}

  @Test
  void StudentとStudentCourseがStudentDetailに変換されて正しくかえること() throws Exception{
    Student student = new Student();
    student.setId("S999999");
    student.setName("テスト");

    StudentCourse course = new StudentCourse();
    course.setId("C999999");
    course.setStudentId("S999999");

    List<StudentDetail> actual = sut.convertStudentDetails(
        List.of(student),
        List.of(course)
    );

    assertEquals(1, actual.size());

    StudentDetail detail = actual.get(0);
    assertEquals(student, detail.getStudent());
    assertEquals(1, detail.getStudentsCourseList().size());
    assertEquals(course, detail.getStudentsCourseList().get(0));


  }

}