package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentCourseStatus {

  private String statusId;
  private String studentCourseId;
  private String status;
  private boolean deleted;

}
