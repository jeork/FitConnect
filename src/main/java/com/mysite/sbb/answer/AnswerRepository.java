package com.mysite.sbb.answer;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mysite.sbb.user.SiteUser;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    // 사용자가 작성한 모든 답변 삭제

    void deleteByAuthor(SiteUser author);

    @Query("select distinct a from Answer a where a.content like %:kw%")
    Page<Answer> findByContentKeyword(@Param("kw") String kw, Pageable pageable);
}
