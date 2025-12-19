package raisetech.StudentManagement.data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Student {

  @NotBlank
  @Pattern(regexp = "S\\d{6}")
  private String id;

  @NotBlank(message = "名前は必須です。")
  private String name;

  @NotBlank(message = "フリがなは必須です。")
  private String kanaName;

  private String nickname;

  @NotBlank(message = "メールアドレスは必須です。")
  @Email(message = "メールアドレスの形式が正しくありません。")
  private String email;

  private String address;

  @Min(value = 0, message = "年齢は0以上で入力してください。")
  @Max(value = 120, message = "年齢が不正です。")
  private Integer age;

  private String gender;

  private String remark;

  private boolean deleted;   //論理削除　SQL:update  altertableでテーブル更新可能
}
