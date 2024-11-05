package com.mysite.sbb.service;

import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class RestClientService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String FLASK_BASE_URL = "http://127.0.0.1:5000"; // Flask 서버 주소

    // 공통 헤더 설정
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return headers;
    }

    // Flask 서버로 POST 요청 보내기
    public String sendPostRequest(String endpoint, String requestBody) {
        HttpEntity<String> request = new HttpEntity<>(requestBody, createHeaders());
        return restTemplate.postForEntity(FLASK_BASE_URL + endpoint, request, String.class).getBody();
    }

    // JSON 데이터를 Map으로 변환
    public Map<String, Object> parseJsonToMap(String jsonString) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
    }
}
