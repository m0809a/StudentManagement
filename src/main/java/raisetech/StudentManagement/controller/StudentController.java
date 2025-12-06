package raisetech.StudentManagement.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;
import org.springframework.ui.Model;


@Controller
public class StudentController {

  private StudentService service;
  private StudentConverter converter;

  @Autowired
  public StudentController(StudentService service, StudentConverter converter) {
    this.service = service;
    this.converter = converter;
  }

  @GetMapping("/studentList")
  public String getStudentList(Model model) {
    List<Student> students = service.searchStudentList();
    List<StudentCourse> studentsCourses = service.getStudentCourseList();

    model.addAttribute("studentList", converter.convertStudentDetails(students, studentsCourses));
    return "studentList";
  }

  @GetMapping("/studentCourseList")
  public List<StudentCourse> getStudentCourseList() {
    return service.getStudentCourseList();
  }

  //　登録処理
  @GetMapping("/newStudent")
  public String newStudent(Model model) {
    StudentDetail detail = new StudentDetail();
    detail.setStudent(new Student());

    List<StudentCourse> list = new ArrayList<>();
    list.add(new StudentCourse());
    detail.setStudentCourses(list);

    model.addAttribute("studentDetail", detail);
    return "registerStudent";
  }

  @PostMapping("/registerStudent")
  public String registerStudent(@ModelAttribute("studentDetail") StudentDetail studentDetail,
      BindingResult result) {

    String newId = service.createStudentId();
    studentDetail.getStudent().setId(newId);

    if (result.hasErrors()) {
      return "registerStudent";
    }
    service.registerStudent(studentDetail);
    return "redirect:/studentList";
  }

  // 更新処理
  @GetMapping("/student/edit/{id}")
  public String editStudent(@PathVariable("id") String id, Model moodel) {
    StudentDetail detail = service.findStudentDetail(id);
    moodel.addAttribute("studentDetail", detail);
    return "editStudent";
  }

  @PostMapping("/student/update")
  public String updateStudent(@ModelAttribute("studentDetail") StudentDetail detail) {
    service.updateStudent(detail);
    return "redirect:/studentList";
  }


  // キャンセルした受講生情報の一覧を表示
  @GetMapping("/studentList/deleted")
  public String getDeletedStudent(Model model) {
    List<Student> students = service.searchDeletedStudents();
    List<StudentCourse> courses = service.searchDeletedStudentCourses();

    model.addAttribute("deletedStudentList", converter.convertStudentDetails(students, courses));

    return "deletedStudentList";
  }


}
