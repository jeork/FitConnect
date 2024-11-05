package com.mysite.sbb.message;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // 수신자 ID로 메시지 목록 조회
    List<Message> findByReceiverId(Long receiverId);
    
    // 발신자 ID로 메시지 목록 조회
    List<Message> findBySenderId(Long senderId);
    
    // 발신자와 수신자 ID로 메시지 조회
    List<Message> findBySenderIdAndReceiverId(Long senderId, Long receiverId);

}
