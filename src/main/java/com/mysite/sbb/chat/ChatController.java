package com.mysite.sbb.chat;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/chat/{receiverId}")
    public String chat(@PathVariable("receiverId") Long receiverId, Principal principal, Model model) {
        SiteUser sender = userService.findByUsername(principal.getName());
        SiteUser receiver = userService.getUserById(receiverId);

        List<ChatMessage> chatHistory = chatService.getChatHistory(sender, receiver);

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        // model.addAttribute("receiverUsername", receiver.getUsername());
        model.addAttribute("chatHistory", chatHistory);

        return "message/chat_room";
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        if (message.getSender() == null || message.getReceiver() == null) {
            System.out.println("Sender 또는 Receiver가 설정되지 않았습니다.");
            return;
        }

        message.setSentAt(LocalDateTime.now());

        chatService.saveMessage(message);

        String destination = "/topic/chat/" + message.getReceiver().getId();
        messagingTemplate.convertAndSend(destination, message);
    }
}