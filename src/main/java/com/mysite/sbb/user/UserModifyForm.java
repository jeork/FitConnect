package com.mysite.sbb.user;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserModifyForm {

    // 사용자 ID: 영문자와 숫자로만 구성되며, 5자 이상 15자 이하로 제한
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "사용자 ID는 영문자와 숫자만 사용할 수 있으며, 5자 이상 15자 이하로 입력해주세요.")
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String username;

    // 이메일: 정규 표현식으로 유효한 이메일 형식 검증
    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9][a-zA-Z0-9-_.]*@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)?$", 
             message = "유효한 이메일 주소를 입력해주세요.")
    private String email;


    private String password;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상이어야 하며, 영문자, 숫자, 특수문자를 각각 하나 이상 포함해야 합니다.")
    private String password2;
}
