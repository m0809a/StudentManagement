package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;



@Mapper
public interface StudentRepository {


  @Select("SELECT id FROM students ORDER BY id DESC LIMIT 1")
  String findMaxStudentId();

  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchByCourse();

  @Insert("""
    INSERT INTO students(id, name, kana_name, nickname, email, address, age, gender, remark, is_deleted)
    VALUES (#{id}, #{name}, #{kanaName}, #{nickname}, #{email}, #{address}, #{age}, #{gender}, #{remark}, #{isDeleted})
    """)
  void insertStudent(Student student);

  @Insert("""
      INSERT INTO students_courses(id, student_id, course_name, course_start_at, course_end_at)
      VALUES (#{id}, #{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})
      """)
  void insertStudentCourses(StudentCourse studentCourse);



}
