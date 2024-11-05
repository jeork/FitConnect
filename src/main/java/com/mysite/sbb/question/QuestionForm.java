package com.mysite.sbb.question;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuestionForm {
	@NotEmpty(message= "제목은 필수 입력입니다.")  // NotEnpty : 제목값이 꼭 있어야함
	@Size(max=200)	// 200자 이상은 작성이 불가능
	private String subject;
	
	@NotEmpty(message = "내용은 필수 입력입니다")  // NotEnpty : 내용값이 꼭 있어야함
	private String content;

}
