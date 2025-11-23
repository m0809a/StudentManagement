package raisetech.StudentManagement.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  private String id;
  private String name;
  private String kanaName;
  private String nickname;
  private String email;
  private String address;
  private Integer age;
  private String gender;
  private String remark;
  private boolean isDeleted;   //論理削除　SQL:update  altertableでテーブル更新可能
}
