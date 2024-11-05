package com.mysite.sbb.answer;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionService;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/answer")
@RequiredArgsConstructor
@Controller
public class AnswerController {
	
	private final QuestionService questionService;
	private final AnswerService answerService;
	private final UserService userService;
	
	// post매핑
	// QuestionService questionService;를 주입 받고 있음
    // createAnswer 는 모델 객체를 매개변수를 했고 아이디 값을 가져오기 위해
	// => @PathVariable를 사용함
	
	// @RequestParam를 이용해서 content값을 가져온다
	// => value="content" 파라미터의 name값을 작성해준다
	//		=> 그 데이터는 content라는 String 변수에 담는다
	// 답변 데이터를 등록할 때는 등록 객체가 필요함
	// => this.questionService.getQuestion(id);를 question 객체로 부터 가져온다
	//  String.format 를 이용해서 다시 질문 상세페이지로 redirect를 시킴
	// Principal principal : 스프링 시큐리티가 제공하는 객체
	// 	=> createAnswer메서드에 principal 객체를 매개변수로 지정하는 작업
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create/{id}")
	public String createAnswer(Model model, @PathVariable("id") Integer id,
			@Valid AnswerForm answerForm, BindingResult
			bindingResult,Principal principal) {
				Question question = this.questionService.getQuestion(id);
				SiteUser siteUser = this.userService.getUser(principal.getName());
				if(bindingResult.hasErrors()) {
					model.addAttribute("question",question);
					return "question_detail";
				}
				this.answerService.create(question, answerForm.getContent(),siteUser);
				// answerService.create 추가 question 객체와, content를 answerForm으로 부터 보낸다
				
				return String.format("redirect:/question/detail/%s", id);
	}
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String answerModify(AnswerForm answerForm , @PathVariable("id") Integer id
			,Principal principal) {
		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
			
			
		}
		answerForm.setContent(answer.getContent());
		return "answer_form";
 }
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String answerModify(@Valid AnswerForm answerForm,BindingResult bindingResult,
			@PathVariable("id") Integer id, Principal principal) {
		if (bindingResult.hasErrors()) {
			return "answer_form";
			
		}
		Answer answer = this.answerService.getAnswer(id);
		if (!answer.getAuthor().getUsername().equals(principal.getName())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다");
			
		}
		this.answerService.modify(answer, answerForm.getContent());
		return String.format("redirect:/question/detail/%s", answer.getQuestion().getId());
	}
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/delete/{id}")
	public String answerDelete(Principal principal, @PathVariable("id") Integer id) {
	    Answer answer = this.answerService.getAnswer(id);
	    if (!answer.getAuthor().getUsername().equals(principal.getName())) {
	        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");
	    }
	    this.answerService.delete(answer);
	    return String.format("redirect:/question/detail/%s", answer.getQuestion().getId()); // 세미콜론 추가됨
	}

	}
	

























