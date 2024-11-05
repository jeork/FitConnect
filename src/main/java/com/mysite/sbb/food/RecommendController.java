package com.mysite.sbb.food;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.service.RestClientService;

@RequestMapping("/food")
@Controller
public class RecommendController {

    private final RestClientService restClientService;

    public RecommendController(RestClientService restClientService) {
        this.restClientService = restClientService;
    }

    // 프로필 여부 확인 후 페이지 리다이렉트
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/check_profile")
    public String checkProfileAndRedirect(@RequestParam("username") String userId) {
        try {
            String response = restClientService.sendPostRequest("/check_profile", "username=" + userId);
            Map<String, Object> result = restClientService.parseJsonToMap(response);

            // 프로필 존재 여부에 따라 페이지 리다이렉트
            if ((boolean) result.get("profile_exists")) {
                return "redirect:/food/recommend";
            } else {
                return "redirect:/food/enter_profile";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/error";
        }
    }

    // GET 요청을 처리하는 메서드 추가
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/recommend")
    public String showRecommendationPage() {
        return "food_recommend";  // 추천 페이지 템플릿을 반환
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/recommend")
    public String getRecommendations(@RequestParam("username") String userId, Model model) {
        try {
            String response = restClientService.sendPostRequest("/recommend", "username=" + userId);
            Map<String, Object> recommendations = restClientService.parseJsonToMap(response);

            model.addAttribute("recommendations", recommendations);
            model.addAttribute("username", userId);
        } catch (Exception e) {
            e.printStackTrace(); // 오류 발생 시 처리
        }
        return "food_recommend";
    }

    @PostMapping("/submit_rating")
    public String submitRating(
        @RequestParam("username") String userId,
        @RequestParam("lunch_food_code[]") String[] lunchFoodCodes,
        @RequestParam("lunch_food_number[]") String[] lunchFoodNumbers,
        @RequestParam("lunch_rating[]") String[] lunchRatings,
        @RequestParam("dinner_food_code[]") String[] dinnerFoodCodes,
        @RequestParam("dinner_food_number[]") String[] dinnerFoodNumbers,
        @RequestParam("dinner_rating[]") String[] dinnerRatings) {

        String requestBody = buildRatingRequestBody(userId, lunchFoodCodes, lunchFoodNumbers, lunchRatings, dinnerFoodCodes, dinnerFoodNumbers, dinnerRatings);

        restClientService.sendPostRequest("/submit_rating", requestBody);
        return "redirect:/"; // 평점 제출 후 메인 페이지로 리다이렉트
    }

    // 평점 제출 요청 바디 빌드
    private String buildRatingRequestBody(String userId, String[] lunchFoodCodes, String[] lunchFoodNumbers, String[] lunchRatings,
                                          String[] dinnerFoodCodes, String[] dinnerFoodNumbers, String[] dinnerRatings) {
        StringBuilder requestBody = new StringBuilder("username=" + userId);

        appendFoodDetails(requestBody, "lunch_food_code[]", lunchFoodCodes);
        appendFoodDetails(requestBody, "lunch_food_number[]", lunchFoodNumbers);
        appendFoodDetails(requestBody, "lunch_rating[]", lunchRatings);

        appendFoodDetails(requestBody, "dinner_food_code[]", dinnerFoodCodes);
        appendFoodDetails(requestBody, "dinner_food_number[]", dinnerFoodNumbers);
        appendFoodDetails(requestBody, "dinner_rating[]", dinnerRatings);

        return requestBody.toString();
    }

    // 식사 정보 추가
    private void appendFoodDetails(StringBuilder requestBody, String paramName, String[] paramValues) {
        for (String value : paramValues) {
            requestBody.append("&").append(paramName).append("=").append(value);
        }
    }
}
