package com.mysite.sbb.question;

// 리포지토리 관련
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, Integer> {

    // 제목 검색
    @Query("select "
            + "distinct q "
            + "from Question q "
            + "where q.subject like %:kw%"
    )
    Page<Question> findBySubjectKeyword(@Param("kw") String kw, Pageable pageable);

    // 작성자 username 검색
    @Query("select "
            + "distinct q "
            + "from Question q "
            + "left outer join SiteUser u on q.author=u "
            + "where u.username like %:kw%"
    )
    Page<Question> findByUserNameKeyword(@Param("kw") String kw, Pageable pageable);
}
