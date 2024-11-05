package com.mysite.sbb.question;

import java.time.LocalDateTime;
import java.util.List;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Question {

    @Id // ID= primary key 기본키에 대한 애너테이션

    // mysql  =  AUTO_INCREMENT
    // oracle =  Sequence 요런 역활을 하는 애너테이션
    // integer에 속성 값을 줬음
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 200) // length = 200 테이블에 컬럼의 크기를 주는 것 처럼 컬럼의 크기 값 지정
    private String subject; // 제목

    @Column(columnDefinition = "TEXT") // 글자 수 제한 없이 TEXT형태로 들어감
    private String content; // 내용

    private LocalDateTime createDate; // 작성 시간

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    // 질문의 기준으로 답변이 여러개 달릴 수 있다
    // mappedBy = "question" = Answer에 작성한  ManyToOne Question을 의미
    // cascade = CascadeType.REMOVE 부모가 삭제되면 자식도 삭제되는 속성
    // => 질문를 삭제되면 답변도 같이 삭제 되도록 관계 설정
    private List<Answer> answerList;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private SiteUser author;

    private LocalDateTime modifyDate;

}
