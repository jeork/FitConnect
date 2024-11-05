package com.mysite.sbb.answer;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.mysite.sbb.DataNotFoundException;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    // AnswerService 는 AnswerRepository answerRepository; 를 주입 받음

    // 답변 내용 추가
    public void create(Question question, String content, SiteUser author) {
        // question 객체 받아오고 String content 내용을 받아옴

        // 내용 , 작성 시간 Question를 등록을 해야하는데
        // => Question 번호나 그런 게 아니라 Question 객체를 담는다
        // => 그리고 answerRepository에 save로 전달함
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setCreateDate(LocalDateTime.now());
        answer.setQuestion(question);
        // jsp를 썼을 때 중의할 점은 항상 연관관계에 있는 엔티티를 저장할땐
        // => 엔티티 자체를 객체로 담아줘야 함
        answer.setAuthor(author); // 사이트 유저
        this.answerRepository.save(answer);

    }

    public Answer getAnswer(Integer id) {
        Optional<Answer> answer = this.answerRepository.findById(id);
        if (answer.isPresent()) {
            return answer.get();

        } else {
            throw new DataNotFoundException("answer not found");
        }
    }

    public Page<Answer> getList(int page) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());
        return this.answerRepository.findAll(pageable);
    }

    public Page<Answer> getList(int page, String kw) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by("createDate").descending());

        if (kw == null || kw.trim().isEmpty()) {
            return this.answerRepository.findAll(pageable);
        }

        return this.answerRepository.findByContentKeyword(kw, pageable);
    }

    public void modify(Answer answer, String content) {
        answer.setContent(content);
        answer.setModifyDate(LocalDateTime.now());
        this.answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        this.answerRepository.delete(answer);
    }
}
