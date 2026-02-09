package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "受講生コース情報")
@Getter
@Setter

public class StudentCourse {

  private String id;
  private String studentId;
  private String courseId;
  private String courseName;
  private LocalDate courseStartAt;
  private LocalDate courseEndAt;
  private boolean deleted;


  @Override
  public boolean equals(Object o){

    if(this == o)
      return true;

    if(this == null)
      return false;

    if(o == null)
      return false;

    if (getClass() != o.getClass()) return false;

    StudentCourse other = (StudentCourse) o;

    return Objects.equals(id, other.id)
        && Objects.equals(studentId, other.studentId)
        && Objects.equals(courseName, other.courseName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, studentId, courseName);
  }



}
