package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;



@Mapper
public interface StudentRepository {



  @Select("SELECT * FROM students")
  List<Student> search();

  @Select("SELECT * FROM students_courses")
  List<StudentCourse> searchByCourse();

  //studentId自動生成用
  @Select("SELECT id FROM students ORDER BY id DESC LIMIT 1")
  String findMaxStudentId();

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


  // 更新用、idを指定して受講生をとってくる
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student findStudentById(String id);

  // 更新用、その受講生のコース一覧を取ってくる
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> findCoursesByStudentId(String studentId);

  @Update("""
      UPDATE students
      SET name = #{name},
          kana_name = #{kanaName},
          nickname = #{nickname},
          email = #{email},
          address = #{address},
          age = #{age},
          gender = #{gender},
          remark = #{remark},
          is_deleted = #{isDeleted}
      WHERE id = #{id}
      """)
  void updateStudent(Student student);


  @Update("""
      UPDATE students_courses
      SET course_name = #{courseName},
          course_start_at = #{courseStartAt},
          course_end_at = #{courseEndAt}
      WHERE student_id=#{studentId}
      """)
  void updateStudentCourse(StudentCourse course);
}




