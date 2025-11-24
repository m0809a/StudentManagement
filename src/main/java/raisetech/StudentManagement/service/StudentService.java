package raisetech.StudentManagement.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
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


  public List<Student> searchStudentList() {
    return repository.search();
  }

  public List<StudentCourse> getStudentCourseList(){
    return repository.searchByCourse();
  }

  public void registerStudent(StudentDetail studentDetail){
    repository.insertStudent(studentDetail.getStudent());

    for (StudentCourse course : studentDetail.getStudentCourses()){
      if (course.getCourseName() == null || course.getCourseName().isBlank()){
        continue;
      }
      course.setStudentId(studentDetail.getStudent().getId());
      repository.insertStudentCourses(course);
    }
}
}
