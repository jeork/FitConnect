package com.mysite.sbb.chat;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.mysite.sbb.user.SiteUser;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    // 메시지 저장
    public void saveMessage(ChatMessage chatMessage) {
        // if (chatMessage.getSender() != null) {
        //     chatMessage.setSenderUsername(chatMessage.getSender().getUsername());
        // }
        chatMessage.setSentAt(LocalDateTime.now());
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getChatHistory(SiteUser sender, SiteUser receiver) {
        List<ChatMessage> chatHistory = chatMessageRepository.findBySenderAndReceiver(sender, receiver);
        chatHistory.addAll(chatMessageRepository.findBySenderAndReceiver(receiver, sender));

        return chatHistory.stream()
                .sorted((m1, m2) -> m1.getSentAt().compareTo(m2.getSentAt()))
                .peek(this::ensureSenderUsername)
                .collect(Collectors.toList());
    }

    private void ensureSenderUsername(ChatMessage message) {
        if (message.getSenderUsername() == null || message.getSenderUsername().isEmpty()) {
            if (message.getSender() != null) {
                message.setSenderUsername(message.getSender().getUsername());
                chatMessageRepository.save(message);
            }
        }
    }
}
