package com.mysite.sbb.user;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerForm;
import com.mysite.sbb.answer.AnswerService;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionForm;
import com.mysite.sbb.question.QuestionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/manage")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    @GetMapping("")
    public String adminPage(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", required = false) String kw) {
        Page<SiteUser> paging = userService.getPaginatedUsers(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "admin_page";
    }

    @PostMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        SiteUser siteUser = userService.getUserById(id);
        if (siteUser != null) {
            userService.delete(siteUser);
            logger.info("User deleted: {}", id);
        } else {
            logger.warn("Attempt to delete non-existent user: {}", id);
        }
        return "redirect:/manage";
    }

    @GetMapping("/question/list")
    public String questionList(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", required = false) String kw) {
        try {
            logger.info("Fetching question list for page: {}", page);
            Page<Question> paging = questionService.getList(page, kw);
            logger.info("Fetched {} questions", paging.getContent().size());
            model.addAttribute("paging", paging);
            model.addAttribute("kw", kw);
            return "admin_question_list";
        } catch (Exception e) {
            logger.error("Error fetching question list", e);
            model.addAttribute("errorMessage", "질문 목록을 불러오는 중 오류가 발생했습니다.");
            return "error_page";
        }
    }

    @GetMapping("/question/modify/{id}")
    public String questionModify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal) {
        Question question = this.questionService.getQuestion(id);
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PostMapping("/question/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
            Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return "redirect:/manage/question/list";
    }

    @GetMapping("/question/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        this.questionService.delete(question);
        return "redirect:/manage/question/list";
    }

    @GetMapping("/answer/list")
    public String answerList(Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "kw", required = false) String kw) {
        Page<Answer> paging = answerService.getList(page, kw);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        return "admin_answer_list";
    }

    @GetMapping("/answer/detail/{id}")
    public String answerDetail(Model model, @PathVariable("id") Integer id) {
        Answer answer = answerService.getAnswer(id);
        model.addAttribute("answer", answer);
        return "admin_answer_detail";
    }

    @GetMapping("/answer/modify/{id}")
    public String answerModify(@PathVariable("id") Integer id, Model model) {
        Answer answer = answerService.getAnswer(id);
        AnswerForm answerForm = new AnswerForm();
        answerForm.setContent(answer.getContent());
        model.addAttribute("answerForm", answerForm);
        return "answer_form";
    }

    @PostMapping("/answer/modify/{id}")
    public String answerModify(@PathVariable("id") Integer id,
            @Valid AnswerForm answerForm,
            BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "answer_form";
        }
        Answer answer = answerService.getAnswer(id);
        answerService.modify(answer, answerForm.getContent());
        return "redirect:/manage/answer/list";
    }

    @GetMapping("/answer/delete/{id}")
    public String answerDelete(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        Answer answer = answerService.getAnswer(id);
        answerService.delete(answer);
        redirectAttributes.addFlashAttribute("message", "답변이 삭제되었습니다.");
        return "redirect:/manage/answer/list";
    }
}
