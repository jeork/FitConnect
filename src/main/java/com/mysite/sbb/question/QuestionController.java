package com.mysite.sbb.question;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

// @RequestMapping("/question") /question으로 생성되는 모든 주소는 Controller가 받아주는 것
// 이후에 /list / detail가 해당하는 메서드가 해당된다!
@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {

    // 생성자 주입 방식! 스프링 객체를 사요할 수 있는 방식
    // QuestionService에서 @Service 애너테이션을 주입 받음
    private final QuestionService questionService;
    private final UserService userService;

    @GetMapping("/list")

    // DB로부터 데이터를 조회하고 Model객체에 저장하고 데이터를 출력해줌
    // Model : 화면에 데이터를 가져갈 때 Model이라는 매개변수 작성해주고
    // 레파지토리로부터 전체 조회를 해서 그것을 list로 가져오고
    // model.addAttribute("questionList" , questionList);
    // => 로 부터 데이터를 담아주고
    // return "question_list"; 화면에 보여준다
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", required = false) String kw,
                       @RequestParam(value = "type", required = false) String type) {
        Page<Question> paging = this.questionService.getList(page, kw, type);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("type", type);
        return "question_list";
    }

    @GetMapping("/detail/{id}")
    public String detail(Model model, @PathVariable("id") Integer id, AnswerForm answerForm) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String questionCreate(QuestionForm questionForm) {
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "question_form";

        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser);
        return "redirect:/question/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    // 질문 버튼을 클릭했을 때 QuestionForm으로 받아준다
    // => BindingResult 같이 활용이 됨 (검증된 결과가 담긴다)
    // => @Valid 입력된 값이 이 form에 정의한 내용대로 잘 들어왔는지 검증
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        // bindingResult.hasErrors : 검증 결과에 에러가 있다
        // => 저장을 하면 안 된다 지정된 형식에 맞지 않으니 입력 화면으로 돌린다
        // => 회원가입 할 때 필수 조건으로 입력해야 하는 것을
        // => 입력하지 않으면 넘어가지 않는 부분에 해당
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정 권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {

        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다.");

        }
        this.questionService.delete(question);
        return "redirect:/question/list";
    }

}
