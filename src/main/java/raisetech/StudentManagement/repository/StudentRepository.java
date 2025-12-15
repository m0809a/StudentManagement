package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

/**
 * 受講生テーブルと受講生コース情報テーブルと紐付くRepositoryです。
 */
@Mapper
public interface StudentRepository {

  /**
   * キャンセル以外の受講生の全件検索を行います。
   *
   * @return　受講生一覧（全件）
   */
  @Select("SELECT * FROM students WHERE deleted = false")
  List<Student> findAllActiveStudents();


  /**
   * 受講生の検索を行います。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  @Select("SELECT * FROM students WHERE id = #{id}")
  Student findStudentById(String id);


  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return　受講生のコース情報（全件）
   */
  @Select("SELECT * FROM students_courses WHERE deleted = false")
  List<StudentCourse> findAllActiveCourses();


  /**
   * 受講生IDに紐付く受講生コース情報を検索します。
   *
   * @param studentId　受講生ID
   * @return　受講生IDの紐付く受講生コース情報
   */
  @Select("SELECT * FROM students_courses WHERE student_id = #{studentId}")
  List<StudentCourse> findCoursesByStudentId(String studentId);


  /**
   * 新規登録の受講生に、受講生IDを振り分けます。
   * 現在登録されている最大のIDに、+1したIDが設定されます。
   *
   * @return　受講生ID（新規）
   */
  @Select("SELECT id FROM students ORDER BY id DESC LIMIT 1")
  String findMaxStudentId();

  /**
   * 受講生情報を登録します。
   *
   * @param student　受講生情報
   */
  @Insert("""
      INSERT INTO students(id, name, kana_name, nickname, email, address, age, gender, remark, deleted)
      VALUES (#{id}, #{name}, #{kanaName}, #{nickname}, #{email}, #{address}, #{age}, #{gender}, #{remark}, #{deleted})
      """)
  void insertStudent(Student student);

  /**
   * 受講生IDに紐付く受講コース情報を登録します。
   *
   * @param studentCourse　受講コース情報
   */

  @Insert("""
      INSERT INTO students_courses(id, student_id, course_name, course_start_at, course_end_at)
      VALUES (#{id}, #{studentId}, #{courseName}, #{courseStartAt}, #{courseEndAt})
      """)
  void insertStudentCourses(StudentCourse studentCourse);




  /**
   * 受講生情報を更新します。
   *
   * @param student　受講生情報
   */

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
          deleted = #{deleted}
      WHERE id = #{id}
      """)
  void updateStudent(Student student);


  /**
   * 受講生IDに紐付く受講コース情報を更新します。
   *
   * @param studentCourse　受講コース情報
   */

  @Update("""
      UPDATE students_courses
      SET course_name = #{courseName},
          course_start_at = #{courseStartAt},
          course_end_at = #{courseEndAt}
      WHERE student_id=#{studentId}
      """)
  void updateStudentCourse(StudentCourse studentCourse);

  @Update("""
    UPDATE students_courses
    SET deleted = #{deleted}
    WHERE student_id = #{studentId}
    """)
  void updateStudentCourseDeleted(StudentCourse studentCourse);







}




