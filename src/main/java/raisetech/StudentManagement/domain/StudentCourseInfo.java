package raisetech.StudentManagement.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

@Getter
@AllArgsConstructor
public class StudentCourseInfo {
  private String studentId;
  private String studentName;
  private String courseId;
  private String courseName;
  private String status;

}
