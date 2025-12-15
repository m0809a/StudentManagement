package raisetech.StudentManagement.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

/**
 * 受験生の検索や登録、更新などを行うREST　APIとして受け付けるControllerです。
 */
@RestController
public class StudentController {

  private StudentService service;


  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生一覧検索です。
   * 全件検索を行うので、条件指定は行いません。
   *
   * @return　受講生一覧（全件）
   *
   */
  @GetMapping("/students")
  public List<StudentDetail>getStudentList() {
    return service.getAllStudent();
  }


  /**
   * 受講生検索です。
   * IDに紐付く任意の受講生の情報を取得します。
   *
   * @param id　受講生ID
   * @return　受講生
   */
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(@PathVariable String id){
    return service.getStudentDetail(id);
  }


  /**
   * 受講生登録です。
   *
   * @param studentDetail　受講生情報+受講コース情報
   *
   * @return　受講生情報+受講コース情報
   */
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(@RequestBody StudentDetail studentDetail){


    StudentDetail responseStudentDetail = service.registerStudentWithNewId(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }


  /**
   * 受講生更新です。
   *
   */
  @PostMapping("/student/update")
  public ResponseEntity<String> updateStudent(@RequestBody StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }



}
