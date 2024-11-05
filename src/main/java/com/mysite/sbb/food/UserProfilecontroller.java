package com.mysite.sbb.food;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mysite.sbb.service.RestClientService;

@RequestMapping("/food")
@Controller
public class UserProfilecontroller {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://127.0.0.1:5000"; // Flask 서버 주소

    private final RestClientService restClientService;

    public UserProfilecontroller(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    // 프로필 입력 페이지를 제공하는 GET 매핑
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/enter_profile")
    public String enterUserProfile() {
        return "enter_profile"; // Thymeleaf 템플릿 파일이 "enter_profile.html"임을 가정
    }

    // 유저 프로필 저장 요청 처리
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/save_profile")
    public String saveUserProfile(@RequestParam("username") String userId,
                                  @RequestParam("age") int age,
                                  @RequestParam("height") float height,
                                  @RequestParam("weight") float weight,
                                  @RequestParam("gender") String gender,  // 성별 추가
                                  @RequestParam(value = "preferredCategories", required = false) List<Integer> preferredCategories,
                                  RedirectAttributes redirectAttributes) {

        // 프로필 정보 생성
        String requestBody = buildUserProfileRequestBody(userId, age, height, weight, gender, preferredCategories);

        // Flask 서버로 POST 요청 전송
        restClientService.sendPostRequest("/save_profile", requestBody);

        // 성공적으로 저장한 후 추천 페이지로 리다이렉트
        redirectAttributes.addAttribute("username", userId);
        return "redirect:/food/recommend";
    }

    // 프로필 정보 및 카테고리 요청 바디 생성 메서드
    private String buildUserProfileRequestBody(String userId, int age, float height, float weight, String gender, List<Integer> preferredCategories) {
        StringBuilder requestBody = new StringBuilder("username=" + userId)
                .append("&age=").append(age)
                .append("&height=").append(height)
                .append("&weight=").append(weight)
                .append("&gender=").append(gender);

        // 선택된 카테고리 처리 (리스트로 받아 처리)
        if (preferredCategories != null && !preferredCategories.isEmpty()) {
            preferredCategories.forEach(category -> requestBody.append("&preferredCategories=").append(category));
        }

        return requestBody.toString();
    }
}
