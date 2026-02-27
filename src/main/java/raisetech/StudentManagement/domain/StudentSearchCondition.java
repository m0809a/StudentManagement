package raisetech.StudentManagement.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StudentSearchCondition {
  private String name; // 部分一致
  private String courseId; // コースID
  private String status; // TEMP/FORMAL/TAKING/DONE
  private Boolean includeCanceledStudents; // trueならキャンセル済も含める
  private String gender; // 性別
  private String ageGroup; //~19,21~29,30~39・・・・

}
