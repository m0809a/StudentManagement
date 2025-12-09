package raisetech.StudentManagement.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

    @Service
    public class StudentService {

      private StudentRepository repository;

      @Autowired
      public StudentService(StudentRepository repository) {
        this.repository = repository;
      }

      //全件取得
      public List<Student> searchStudentList() {
        return repository.search();
      }

      public List<StudentCourse> getStudentCourseList() {
        return repository.searchByCourse();
      }


      /// 単一検索
      public StudentDetail findStudentDetail(String id) {
        Student student = repository.findStudentById(id);
        List<StudentCourse> courses = repository.findCoursesByStudentId(student.getId());

        StudentDetail studentDetail = new StudentDetail();
        studentDetail.setStudent(student);
        studentDetail.setStudentCourses(courses);

        return studentDetail;

      }



      /// 登録
      @Transactional
      public void registerStudent(StudentDetail studentDetail) {
        repository.insertStudent(studentDetail.getStudent());

        for (StudentCourse course : studentDetail.getStudentCourses()) {
          if (course.getCourseName() == null || course.getCourseName().isBlank()) {
            continue;
          }
          String fixedCourseId = getCourseIdByName(course.getCourseName());
          course.setId(fixedCourseId);
          course.setStudentId(studentDetail.getStudent().getId());
          course.setCourseStartAt(LocalDate.now());
          course.setCourseEndAt(LocalDate.now().plusYears(1));
          repository.insertStudentCourses(course);
        }

      }

      ///登録用
      //id（Sから始まる６桁）を自動生成
      public String createStudentId() {
        String maxId = repository.findMaxStudentId(); // ex: "S000010"
        if (maxId == null) {
          return "S000001";
        }
        int num = Integer.parseInt(maxId.substring(1));
        num++;
        return String.format("S%06d", num);
      }
      //コース名に対応するIDを挿入

      public String getCourseIdByName(String courseName) {
        switch (courseName) {
          case "Java入門コース":
            return "C000001";
          case "Webアプリ開発コース":
            return "C000002";
          case "AWS基礎コース":
            return "C000003";
          case "DB設計コース":
            return "C000004";
          case "python基礎コース":
            return "C000005";
          default:
            return "C-OTHER"; // その他コース
        }
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




      public List<Student> searchDeletedStudents() {
        return repository.searchDeletedStudent();
      }

      public List<StudentCourse> searchDeletedStudentCourses() {
        return repository.searchCoursesOfDeletedStudents();
      }

    }



