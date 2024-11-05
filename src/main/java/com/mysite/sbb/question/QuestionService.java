package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class QuestionService {

    // 레파지토리를 주입
    private final QuestionRepository questionRepository;

    // getList 추가 레파지토리로 부터 findAll(); 호출 리턴
    // 이 리턴은 어디로 가느냐 QuestionController로 이동
    public Page<Question> getList(int page, String kw, String type) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().isEmpty()) {
            return this.questionRepository.findAll(pageable);
        }

        if ("subject".equals(type)) {
            return this.questionRepository.findBySubjectKeyword(kw, pageable);
        } else if ("username".equals(type)) {
            return this.questionRepository.findByUserNameKeyword(kw, pageable);
        }

        return this.questionRepository.findAll(pageable);
    }

    public Page<Question> getList(int page, String kw) {
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

        if (kw == null || kw.trim().isEmpty()) {
            return this.questionRepository.findAll(pageable);
        }

        return this.questionRepository.findBySubjectKeyword(kw, pageable);
    }

    // 특정 질문을 ID로 조회하는 메서드
    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) {
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    // 질문 내용 추가 제목 내용 시간등 저장
    public void create(String subject, String content, SiteUser user) {
        Question q = new Question();
        q.setSubject(subject);
        q.setContent(content);
        q.setCreateDate(LocalDateTime.now());
        q.setAuthor(user);
        this.questionRepository.save(q);

    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

}
