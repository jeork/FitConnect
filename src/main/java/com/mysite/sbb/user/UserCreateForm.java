package com.mysite.sbb.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter // Lombok을 사용해 getter 메서드를 자동으로 생성합니다.
@Setter // Lombok을 사용해 setter 메서드를 자동으로 생성합니다.
public class UserCreateForm {

    // 사용자 ID: 영문자와 숫자로만 구성되며, 5자 이상 15자 이하로 제한
    @Pattern(regexp = "^[a-zA-Z0-9]{5,15}$", message = "사용자 ID는 영문자와 숫자만 사용할 수 있으며, 5자 이상 15자 이하로 입력해주세요.")
    @NotEmpty(message = "사용자 ID는 필수 항목입니다.")
    private String username;

    // 비밀번호: 최소 8자 이상, 특수 문자 1개 이상 포함
    @Pattern(regexp = "^(?=.*[!@.])[a-zA-Z0-9!@.#$%^&*]{8,}$", message = "비밀번호는 8자 이상이며, !@.특수 문자를 1개 이상 포함해야 합니다.")
    @NotEmpty(message = "비밀번호는 필수 항목입니다.")
    private String password1;

    // 비밀번호 확인: password1과 동일한 유효성 검사를 적용 (개별 비교는 서비스 또는 컨트롤러에서 처리)
    @NotEmpty(message = "비밀번호 확인은 필수 항목입니다.")
    private String password2;

    // 이메일: 정규 표현식으로 유효한 이메일 형식 검증
    // @NotEmpty(message = "이메일은 필수 항목입니다.")
    // @Pattern(regexp =
    // "^[a-zA-Z0-9][a-zA-Z0-9-_.]*@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-_]+(\\.[a-zA-Z0-9-_]+)?$",
    // message = "유효한 이메일 주소를 입력해주세요.")
    // private String email;
    @NotEmpty(message = "이메일은 필수 항목입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

}
