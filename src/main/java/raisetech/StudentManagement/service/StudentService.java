package raisetech.StudentManagement.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。
 * 受講生の検索や登録・更新処理を行います。
 */
    @Service
    public class StudentService {

      private StudentRepository repository;
      private StudentConverter converter;

      @Autowired
      public StudentService(StudentRepository repository, StudentConverter converter) {
        this.repository = repository;
        this.converter = converter;
      }

  /**
   * 受講生詳細の一覧検索です。
   * 全件検索を行うので、条件指定は行いません。
   *
   * @return　受講生詳細一覧（全件）
   */
  public List<StudentDetail> getAllStudent() {
    List<Student> studentList = repository.findAllActiveStudents();
    List<StudentCourse> studentsCoursesList = repository.findAllActiveCourses();
    return converter.convertStudentDetails(studentList, studentsCoursesList);
      }


  /**
   * 受講生検索です。
   * IDに紐付く受講生情報を取得した後、その受講生に紐付く受講生コース情報を取得して設定します。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  public StudentDetail getStudentDetail(String id) {
        Student student = repository.findStudentById(id);
        List<StudentCourse> courses = repository.findCoursesByStudentId(student.getId());
        return  new StudentDetail(student, courses);
      }


  /**
   * 受講生詳細の登録を行います。
   * 受講生IDが新規作成されます。（その時点でのMAXに+1）
   * 受け取った受講生情報を登録します。
   * 受講生に紐付いたコース情報（値や日付情報）を登録します。
   *
   * @param studentDetail　受講生詳細
   * @return　登録した受講生詳細
   */
  @Transactional
      public StudentDetail registerStudentWithNewId(StudentDetail studentDetail) {

        String newId = createStudentId();
        studentDetail.getStudent().setId(newId);
        repository.insertStudent(studentDetail.getStudent());
        for (StudentCourse course : studentDetail.getStudentsCourseList()) {
          insertCourse(studentDetail, course);
        }
        return studentDetail;
      }

  /**
   * 受講生IDを自動採番します
   * @return　S+6桁の受講生ID
   */
      private String createStudentId() {
        String maxId = repository.findMaxStudentId(); // ex: "S000010"
        if (maxId == null) {
          return "S000001";
        }
        int num = Integer.parseInt(maxId.substring(1));
        num++;
        return String.format("S%06d", num);
      }

      /** 入力されたコース名からコースIDを検索し返します*/
      public String getCourseIdByName(String courseName) {
        return switch (courseName) {
          case "Java入門コース" -> "C000001";
          case "Webアプリ開発コース" -> "C000002";
          case "AWS基礎コース" -> "C000003";
          case "DB設計コース" -> "C000004";
          case "python基礎コース" -> "C000005";
          default -> "C-OTHER"; // その他コース
        };
      }

      /**コース情報の詳細を設定して登録します*/
      private void insertCourse(StudentDetail studentDetail, StudentCourse course){
          if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            return;
          }

          String fixedCourseId = getCourseIdByName(course.getCourseName());
          course.setId(fixedCourseId);
          course.setStudentId(studentDetail.getStudent().getId());
          LocalDate now = LocalDate.now();
          course.setCourseStartAt(now);
          course.setCourseEndAt(now.plusYears(1));
          repository.insertStudentCourses(course);

      }


  /**
   * 受講生情報を更新します。
   * キャンセルせずコース情報を変更する場合は、受講生IDに紐付く受講コース情報を更新します。
   * キャンセルせず新規にコース情報を登録する場合も、受講生IDに紐付く受講コース情報を更新します。
   * 受講生がキャンセルされる場合は、受講生IDに紐付く受講コース情報もキャンセルします。
   *
   * @param studentDetail　受講生情報+受講生IDに紐付く受講コース情報
   */
  @Transactional
      public void updateStudent(StudentDetail studentDetail) {
        repository.updateStudent(studentDetail.getStudent());

        List<StudentCourse> existingCourses =
            repository.findCoursesByStudentId(studentDetail.getStudent().getId());

        if (studentDetail.getStudent().isDeleted()) {
          for (StudentCourse course : existingCourses) {
            course.setDeleted(true);
            repository.updateStudentCourseDeleted(course);
          }
          return;
        }

        if (studentDetail.getStudentsCourseList() != null) {
          List<StudentCourse> requestCourses = studentDetail.getStudentsCourseList();
            boolean hasExistingCourses = !existingCourses.isEmpty();
            for (StudentCourse course : studentDetail.getStudentsCourseList()) {

              if (course.getCourseName() == null || course.getCourseName().isBlank()) {
                continue;
              }
              String newCourseId = getCourseIdByName(course.getCourseName());
              course.setId(newCourseId);
              course.setStudentId(studentDetail.getStudent().getId());
              course.setCourseStartAt(LocalDate.now());
              course.setCourseEndAt(LocalDate.now().plusYears(1));

              if (!hasExistingCourses) {
                repository.insertStudentCourses(course);
              } else {
                repository.updateStudentCourse(course);
              }
            }
          }

        }
      }





