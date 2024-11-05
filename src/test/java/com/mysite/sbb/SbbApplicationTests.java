package com.mysite.sbb;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.mysite.sbb.answer.Answer;
import com.mysite.sbb.answer.AnswerRepository;
import com.mysite.sbb.question.Question;
import com.mysite.sbb.question.QuestionRepository;
import com.mysite.sbb.question.QuestionService;

import jakarta.transaction.Transactional;

@SpringBootTest
class SbbApplicationTests {

    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private AnswerRepository answerRepository;
    @Autowired
    private QuestionService questionService;



//    @Test
//    @DisplayName("저장 테스터")
//    void testJpa1() {
//    	
//        Question q1 = new Question();
//        q1.setSubject("sbb가 무엇인가요?");
//        q1.setContent("sbb에 대해서 알고 싶습니다.");
//        q1.setCreateDate(LocalDateTime.now());
//
//        // questionRepository.save()를 통해 question 테이블의 첫 번째 행에 들어갈 데이터로 위 코드에 작성한 데이터들을 저장합니다.
//        this.questionRepository.save(q1);
//
//        Question q2 = new Question();
//        q2.setSubject("스프링 부트 모델 질문입니다!");
//        q2.setContent("id는 자동으로 생성되나요?");
//        q2.setCreateDate(LocalDateTime.now());
//
//        // questionRepository.save()를 통해 question 테이블의 두 번째 행에 들어갈 데이터로 위 코드에 작성한 데이터들을 저장합니다.
//        this.questionRepository.save(q2);
//    }

//    @Test
//    @DisplayName("목록 테스터")
//    void testJpa2() {
//        // findAll : jpa가 제공해주는 레퍼지토리 메서드 
//        // => this.questionRepository.findAll(); (전체 데이터를 조회 )
//        // => 데이터가 2개 들어가있음 이걸 list로 가져온다
//        List<Question> all = this.questionRepository.findAll();
//        // list의 사이즈를 all.size()가 2가 맞는지
//        // => all.size() == 2가 맞는지 assertEquals
//        assertEquals(2, all.size());
//
//        Question q = all.get(0);
//        // 0번에 있는 인덱스 데이터의 제목이 "sbb가 무엇인가요?"와 일치하는지 확인
//        assertEquals("sbb가 무엇인가요?", q.getSubject());
//    }

//    @Test
//    @DisplayName("id 조회 테스트")
//    void testJpa3() {
//        // findById 메서드는 Optional로 가져오게 됨
//        // => Optional는 java util에서 제공되는 타입
//        // => null 체크를 한 번 더 하라는 안전장치
//        Optional<Question> oq = this.questionRepository.findById(29); // ID를 29로 수정
//        // isPresent를 이용해서 여기에 값이 있는지를 먼저 확인하고
//        if (oq.isPresent()) {
//            Question q = oq.get(); // 값이 있다면 꺼내라
//            // 제목이 "sbb가 무엇인가요?"와 일치하는지 확인
//            assertEquals("sbb가 무엇인가요?", q.getSubject());
//        }
//    }
//
//    @Test
//    @DisplayName("제목 조회 테스트")
//    void testJpa4() {
//        // 제목이 "sbb가 무엇인가요?" 조회를 하고 그 데이터의 id값이 29인지 확인
//        Question q = this.questionRepository.findBySubject("sbb가 무엇인가요?");
//        assertEquals(29, q.getId()); // ID를 29로 수정
//    }
//
//    @Test
//    @DisplayName("제목&내용 테스트")
//    void testJpa5() {
//        // 제목이 "sbb가 무엇인가요?"이고 내용이 "sbb에 대해서 알고 싶습니다."인 데이터를 조회하고
//        // 그 데이터의 id값이 29인지 확인
//        Question q = this.questionRepository.findBySubjectAndContent("sbb가 무엇인가요?", "sbb에 대해서 알고 싶습니다.");
//        assertNotNull(q, "The question should not be null");
//        assertEquals(29, q.getId(), "The question ID should be 29"); // ID를 29로 수정
//    }
//
//    @Test
//    @DisplayName("제목으로 LIKE 검색 테스트")
//    void testJpa6() {
//        // findBySubjectLike 함수를 사용할 때 데이터 조회를 위한 조건이 되는 문자열로 "sbb%"와 같이 %를
//        // => 적어 주어야 한다. %는 표기하는 위치에 따라 의미가 달라진다
//        // => sbb%  : 'sbb'로 시작하는 문자열
//        // => %sbb  : 'sbb'로 끝나는 문자열
//        // => %sbb% : 'sbb'를 포함하는 문자열
//        List<Question> qList = this.questionRepository.findBySubjectLike("sbb%");
//        Question q = qList.get(0);
//        assertEquals("sbb가 무엇인가요?", q.getSubject());
//    }
//
//    @Test
//    @DisplayName("수정 테스트")
//    void testJpa7() {
//        // ID가 29인 데이터를 조회하여 제목을 수정하고 저장합니다.
//        Optional<Question> oq = this.questionRepository.findById(29); // ID를 29로 수정
//        assertTrue(oq.isPresent());
//        Question q = oq.get();
//        q.setSubject("수정된 제목");
//        this.questionRepository.save(q);
//    }
//
//    @Test
//    @DisplayName("삭제 테스트")
//    void testJpa8() {
//        // 전체 글 갯수를 가져와서 삭제 후 글 갯수를 확인합니다.
//        // 초기 글 개수는 2개라고 가정합니다.
//        assertEquals(2, this.questionRepository.count()); // 초기 데이터 개수에 맞게 수정
//        Optional<Question> oq = this.questionRepository.findById(29); // ID를 29로 수정
//        assertTrue(oq.isPresent());
//        Question q = oq.get();
//        this.questionRepository.delete(q);
//        assertEquals(1, this.questionRepository.count()); // 삭제 후 1개로 감소
//    }
//
//    @Test
//    @DisplayName("답변 등록 테스트")
//    void testJpa9() {
//        // 답변은 질문처럼 단독으로 저장되는 게 아니라 어떤 질문에 대한 답변인지 설정합니다.
//        // 먼저 질문을 가져오고
//        Optional<Question> oq = this.questionRepository.findById(2); // QUESTION_ID를 30으로 수정
//        assertTrue(oq.isPresent());
//        Question q = oq.get();
//
//        // 답변 객체를 만들어서 답변 엔티티에 Question 속성을 추가합니다.
//        // 그 다음에는 save() 메서드를 호출하여 저장합니다.
//        Answer a = new Answer();
//        a.setContent("네 자동으로 생성됩니다");
//        a.setQuestion(q);
//        a.setCreateDate(LocalDateTime.now());
//        this.answerRepository.save(a);
//    }
//
//    @Test
//    @DisplayName("답변 조회 테스트")
//    void testJpa10() {
//        // ID가 8인 답변을 조회하고 해당 답변의 질문 ID가 30인지 확인합니다.
//        Optional<Answer> oa = this.answerRepository.findById(8); // ID를 8로 수정
//        assertTrue(oa.isPresent());
//        Answer a = oa.get();
//        assertEquals(30, a.getQuestion().getId()); // QUESTION_ID를 30으로 수정
//    }
//
//    @Transactional
//    @Test
//    @DisplayName("질문 데이터로 답변 데이터 조회 테스트")
//    void testJpa11() {
//        // ID가 30인 질문을 조회하고, 그 질문의 답변 리스트를 가져옵니다.
//        // findById 메서드를 통해 Question 객체를 조회하면 DB 세션이 종료되지 않아서 오류가 발생할 수 있습니다.
//        Optional<Question> oq = this.questionRepository.findById(30); // ID를 30으로 수정
//        assertTrue(oq.isPresent());
//        Question q = oq.get();
//
//        List<Answer> answerList = q.getAnswerList();
//
//        // 답변의 개수와 내용을 확인합니다.
//        assertEquals(2, answerList.size()); // 예상 답변 개수에 맞게 수정
//        assertEquals("네 자동으로 생성됩니다", answerList.get(0).getContent());
//    }
//    @Test
//    void testJpa() {
//        for (int i = 1; i <= 300; i++) {  // i <= 300으로 수정
//            String subject = String.format("테스트 데이터입니다:[%03d]", i);
//            String content = "내용 없음";
//            this.questionService.create(subject, content,null);
//        }
//    }
}





























