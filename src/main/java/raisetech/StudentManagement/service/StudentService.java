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
   * 受講生の一覧検索です。
   * 全件検索を行うので、条件指定は行いません。
   *
   * @return　受講生一覧（全件）
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
   * 受講生登録です。
   * 受講生IDが新規作成されます。（その時点でのMAXに+1）
   * 受け取った受講生情報を登録します。
   * 受講生に紐付いたコース情報を登録します。
   *
   * @param studentDetail　受講生詳細情報（受講生情報+コース情報）
   * @return　登録された受講生詳細
   */
  @Transactional
      public StudentDetail registerStudentWithNewId(StudentDetail studentDetail) {

        String newId = createStudentId();
        studentDetail.getStudent().setId(newId);
        repository.insertStudent(studentDetail.getStudent());
        for (StudentCourse course : studentDetail.getStudentCourses()) {
          insertCourse(studentDetail, course);
        }
        return studentDetail;
      }

  /**
   * 受講生IDを新規作成します
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
          course.setCourseStartAt(LocalDate.now());
          course.setCourseEndAt(LocalDate.now().plusYears(1));
          repository.insertStudentCourses(course);

      }



      // 更新
      @Transactional
      public void updateStudent(StudentDetail studentDetail) {

        // 受講生を更新
        repository.updateStudent(studentDetail.getStudent());

        // 今持っているコースを全部取得
        List<StudentCourse> existingCourses =
            repository.findCoursesByStudentId(studentDetail.getStudent().getId());

        //  学生がキャンセルされた場合はコースもキャンセルにする
        if (studentDetail.getStudent().isDeleted()) {
          for (StudentCourse course : existingCourses) {
            course.setDeleted(true);
            repository.updateStudentCourseDeleted(course);
          }
          return;
        }

        // ④ コース更新 or 登録（キャンセルじゃないときだけ実行）
        if (studentDetail.getStudentCourses() != null
            && !studentDetail.getStudentCourses().isEmpty()) {

          // その受講生のコースが存在するかチェック
          boolean hasExistingCourses = !existingCourses.isEmpty();

          for (StudentCourse course : studentDetail.getStudentCourses()) {

            // コース名からIDに変換
            String newCourseId = getCourseIdByName(course.getCourseName());
            course.setId(newCourseId);
            course.setStudentId(studentDetail.getStudent().getId());

            //更新＝新規受講コース契約と仮定
            course.setCourseStartAt(LocalDate.now());
            course.setCourseEndAt(LocalDate.now().plusYears(1));

            if (!hasExistingCourses) {
              // コースが登録されていない
              repository.insertStudentCourses(course);
            } else {
              // コースがすでに登録されている
              repository.updateStudentCourse(course);
            }
          }


        }
      }

    }



