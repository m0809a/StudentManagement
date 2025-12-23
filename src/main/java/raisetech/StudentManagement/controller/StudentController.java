package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

/**
 * 受験生の検索や登録、更新などを行うREST　APIとして受け付けるControllerです。
 */
@Validated
@RestController
@Tag(name = "Student API", description = "受講生管理API")
public class StudentController {

  private final StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生一覧取得
   */
  @Operation(
      summary = "受講生一覧取得",
      description = "キャンセルされていない受講生の一覧を取得します。"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "取得成功"),
      @ApiResponse(responseCode = "500", description = "サーバーエラー")
  })
  @GetMapping("/students")
  public List<StudentDetail> getStudentList() {
    return service.getAllStudent();
  }

  /**
   * 受講生詳細取得
   */
  @Operation(
      summary = "受講生詳細取得",
      description = "受講生IDを指定して、受講生の詳細情報を取得します。"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "取得成功"),
      @ApiResponse(responseCode = "400", description = "ID形式不正"),
      @ApiResponse(responseCode = "404", description = "受講生が存在しない")
  })
  @GetMapping("student/{id}")
  public StudentDetail getStudent(
      @Parameter(description = "受講生ID（S + 6桁）", example = "S000001")
      @PathVariable
      @NotBlank(message = "IDは必須です。")
      @Pattern(regexp = "S\\d{6}", message = "IDはS + 6桁で入力してください。")
      String id) {

    return service.getStudentDetail(id);
  }

  /**
   * 受講生登録
   */
  @Operation(
      summary = "受講生登録",
      description = "受講生を新規登録します。IDは自動採番されます。"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "登録成功"),
      @ApiResponse(responseCode = "400", description = "入力チェックエラー")
  })
  @PostMapping("student/register")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {

    StudentDetail response = service.registerStudentWithNewId(studentDetail);
    return ResponseEntity.ok(response);
  }

  /**
   * 受講生更新
   */
  @Operation(
      summary = "受講生更新",
      description = "受講生情報を更新します。キャンセル（論理削除）も含みます。"
  )
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "更新成功"),
      @ApiResponse(responseCode = "400", description = "入力チェックエラー"),
      @ApiResponse(responseCode = "404", description = "受講生が存在しない")
  })
  @PutMapping("student/update")
  public ResponseEntity<String> updateStudent(
      @RequestBody @Valid StudentDetail studentDetail) {

    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }
}
