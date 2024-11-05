package com.mysite.sbb.message;

import com.mysite.sbb.user.SiteUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MessageService {

    private final MessageRepository messageRepository;

    // 메시지 생성
    public void sendMessage(SiteUser sender, SiteUser receiver, String content) {
        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setSentAt(LocalDateTime.now());
        messageRepository.save(message);
    }

    // 받은 메시지 목록 조회
    public List<Message> getReceivedMessages(Long receiverId) {
        return messageRepository.findByReceiverId(receiverId);  // 수신자의 ID로 메시지 조회
    }

    // 보낸 메시지 목록 조회
    public List<Message> getSentMessages(Long senderId) {
        return messageRepository.findBySenderId(senderId);  // 발신자의 ID로 메시지 조회
    }

    // 메시지 주고받은 기록 확인
    public boolean hasMessageHistory(SiteUser sender, SiteUser receiver) {
        List<Message> sentMessages = messageRepository.findBySenderIdAndReceiverId(sender.getId(), receiver.getId());
        List<Message> receivedMessages = messageRepository.findBySenderIdAndReceiverId(receiver.getId(), sender.getId());
        

        return !sentMessages.isEmpty() || !receivedMessages.isEmpty();  // 메시지 이력이 있는지 확인
    }
 // MessageService.java
    public void save(Message message) {
        messageRepository.save(message);  // 메시지 상태 저장
    }

}
