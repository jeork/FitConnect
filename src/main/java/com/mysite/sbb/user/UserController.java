package com.mysite.sbb.user;

import java.security.Principal;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm, HttpSession session, Principal principal, Model model) {
        if (principal != null) {
            return "redirect:/";
        }
        String email = (String) session.getAttribute("naverEmail");
        if (email != null) {
            userCreateForm.setEmail(email);
        }
        model.addAttribute("userCreateForm", userCreateForm);
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, HttpSession session) {
        if (bindingResult.hasErrors()) {
            return "signup_form";
        }

        if (!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 비밀번호가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getUsername(), userCreateForm.getEmail(), userCreateForm.getPassword1());
            session.removeAttribute("naverEmail");
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            String errorMessage = e.getMessage().toLowerCase();
            if (errorMessage.contains("username")) {
                bindingResult.rejectValue("username", "duplicate.username", "이미 사용 중인 사용자명입니다.");
            } else if (errorMessage.contains("email")) {
                bindingResult.rejectValue("email", "duplicate.email", "이미 등록된 이메일 주소입니다.");
            } else {
                bindingResult.reject("signupFailed", "회원 가입 중 오류가 발생했습니다.");
            }
            return "signup_form";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(HttpSession session) {
        String email = (String) session.getAttribute("naverEmail");
        if (email != null) {
            return "redirect:/user/signup?email=" + email;
        }
        return "login_form";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify")
    public String userModifyForm(UserModifyForm userModifyForm, Principal principal, Model model) {
        SiteUser siteUser = this.userService.getUser(principal.getName());

        // 로그로 Principal 확인
        System.out.println("Logged in user: " + principal.getName());

        if (siteUser == null) {
            model.addAttribute("errorMessage", "사용자 정보를 찾을 수 없습니다.");
            return "redirect:/user/login";
        }

        userModifyForm.setUsername(siteUser.getUsername());
        userModifyForm.setEmail(siteUser.getEmail());
        return "user_modify_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify")
    public String userModify(@Valid UserModifyForm userModifyForm,
            BindingResult bindingResult,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            return "user_modify_form";
        }

        // 비밀번호 변경을 시도하는 경우에만 검증
        if (userModifyForm.getPassword() != null && !userModifyForm.getPassword().trim().isEmpty()) {
            // 비밀번호 확인 검증
            if (!userModifyForm.getPassword().equals(userModifyForm.getPassword2())) {
                bindingResult.rejectValue("password2", "passwordInCorrect",
                        "2개의 비밀번호가 일치하지 않습니다.");
                return "user_modify_form";
            }
        }

        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.userService.modify(siteUser,
                userModifyForm.getUsername(),
                userModifyForm.getEmail(),
                userModifyForm.getPassword());  // 비밀번호 추가

        return "redirect:/user/info";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete")
    public String userDeleteForm() {
        return "user_delete_confirm";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/delete")
    public String userDelete(Principal principal) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.userService.delete(siteUser);
        return "redirect:/user/logout";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/info")
    public String userInfo(Model model, Principal principal) {
        SiteUser siteUser = this.userService.getUser(principal.getName());
        model.addAttribute("user", siteUser);
        return "user_info";
    }
}
