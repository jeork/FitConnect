package com.mysite.sbb.UserMatching;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@PreAuthorize("isAuthenticated()")
public class UserMatchingController {

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/matching")
    public String showMatchingPage(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName(); // 현재 로그인한 사용자의 ID를 가져옵니다.

        // FastAPI 서버 URL
        String fastApiUrl = "http://localhost:8000/matching-form";

        // user_id를 포함한 요청 본문 생성
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("user_id", userId);

        // RestTemplate을 사용하여 FastAPI 서버에 POST 요청을 보냅니다.
        ResponseEntity<String> response = restTemplate.postForEntity(
                fastApiUrl,
                requestBody,
                String.class);

        String matchingForm = response.getBody();
        model.addAttribute("matchingForm", matchingForm);
        model.addAttribute("user_id", userId);

        return "UserMatchingPage";
    }
}