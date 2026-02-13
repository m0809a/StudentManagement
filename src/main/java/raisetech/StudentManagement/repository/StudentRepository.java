package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.data.StudentCourseStatus;

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
  List<Student> findAllActiveStudents();


  /**
   * 受講生の検索を行います。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  Student findStudentById(String id);


  /**
   * 受講生のコース情報の全件検索を行います。
   *
   * @return　受講生のコース情報（全件）
   */
  List<StudentCourse> findAllActiveCourses();


  /**
   * 受講生IDに紐付く受講生コース情報を検索します。
   *
   * @param studentId　受講生ID
   * @return　受講生IDの紐付く受講生コース情報
   */
  List<StudentCourse> findCoursesByStudentId(String studentId);


  /**
   * 新規登録の受講生に、受講生IDを振り分けます。
   * 現在登録されている最大のIDに、+1したIDが設定されます。
   *
   * @return　受講生ID（新規）
   */
  String findMaxStudentId();

  /**
   * 受講生情報を登録します。
   *
   * @param student　受講生情報
   */
  void insertStudent(Student student);

  /**
   * 受講生IDに紐付く受講コース情報を登録します。
   *
   * @param studentCourse　受講コース情報
   */
  void insertStudentCourses(StudentCourse studentCourse);




  /**
   * 受講生情報を更新します。
   *
   * @param student　受講生情報
   */
  void updateStudent(Student student);



  /**
   * 指定した受講生IDに紐付く受講コース情報をキャンセルします。
   *
   * @param studentCourse　受講コース情報
   */
  void updateStudentCourseDeleted(StudentCourse studentCourse);



  /**
   * 受講生の受講コース状況を取得します
   * @param studentCourseId
   * @return
   */
  String findStatusByStudentCourseId(String studentCourseId);



  /**
   * コース登録時に申し込み状況を登録します。（初期値：TEMP・仮申込）
   * @param status
   */
  void insertCourseStatus(StudentCourseStatus status);



  /**
   * コースの申し込み情報を更新します
   * @param studentCourseId
   * @param status
   */
  void updateCourseStatus(String studentCourseId, String status);




}




