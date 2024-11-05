package com.mysite.sbb.message;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysite.sbb.user.SiteUser;
import com.mysite.sbb.user.UserService;
import com.mysite.sbb.util.EncryptionUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final EncryptionUtil encryptionUtil;

    @ModelAttribute("encryptedMessageUrl")
    public String encryptedMessageUrl() {
        return new String();
    }

    public String createEncryptedMessageUrl(String username) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName().equals(username)) {
            return null; // 자기 자신인 경우 null 반환
        }
        String encryptedUsername = encryptionUtil.encrypt(username);
        return "/messages?encryptedReceiverName=" + encryptedUsername;
    }

    @GetMapping("/messages")
    public String messageForm(
            @RequestParam(name = "encryptedReceiverName", required = false) String encryptedReceiverName,
            @RequestParam(name = "receiverName", required = false) String receiverName,
            Model model) {
        if (encryptedReceiverName != null) {
            try {
                receiverName = encryptionUtil.decrypt(encryptedReceiverName);
            } catch (EncryptionUtil.EncryptionException e) {
                return "error/400";
            }
        }

        if (receiverName != null) {
            model.addAttribute("receiverName", receiverName);
            model.addAttribute("encryptedReceiverName", encryptionUtil.encrypt(receiverName));
        }

        return "message/message_send";
    }

    @GetMapping("/messages/list")
    public String listReceivedMessages(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/login";
        }

        String username = authentication.getName();
        SiteUser receiver = userService.findByUsername(username);
        List<Message> receivedMessages = messageService.getReceivedMessages(receiver.getId());

        // 각 메시지를 읽음 상태로 업데이트
        for (Message message : receivedMessages) {
            if (!message.isRead()) {
                message.setRead(true);
                messageService.save(message);
            }

            SiteUser sender = message.getSender();
            boolean hasMessageHistory = messageService.hasMessageHistory(sender, receiver);
            message.setHasMessageHistory(hasMessageHistory);
        }

        model.addAttribute("messages", receivedMessages);
        return "message/message_list";
    }

    @GetMapping("/messages/sent")
    public String listSentMessages(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/login";
        }

        String username = authentication.getName();
        SiteUser sender = userService.findByUsername(username);
        List<Message> sentMessages = messageService.getSentMessages(sender.getId());

        for (Message message : sentMessages) {
            SiteUser receiver = message.getReceiver();
            boolean hasMessageHistory = messageService.hasMessageHistory(sender, receiver);
            message.setHasMessageHistory(hasMessageHistory);
        }

        model.addAttribute("messages", sentMessages);
        return "message/message_sent_list";
    }

    @PostMapping("/messages/send")
    public String sendMessage(
            @RequestParam(name = "encryptedReceiverName", required = false) String encryptedReceiverName,
            @RequestParam(name = "receiverName", required = false) String receiverName,
            @RequestParam(name = "content") String content) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return "redirect:/user/login";
            }

            String senderUsername = authentication.getName();

            if (encryptedReceiverName != null) {
                receiverName = encryptionUtil.decrypt(encryptedReceiverName);
            }

            if (receiverName == null) {
                return "error/400";
            }

            SiteUser sender = userService.findByUsername(senderUsername);
            SiteUser receiver = userService.findByUsername(receiverName);
            if (receiver == null) {
                return "error/404";
            }
            messageService.sendMessage(sender, receiver, content);
            return "redirect:/messages/sent";
        } catch (EncryptionUtil.EncryptionException e) {
            return "error/400";
        }
    }

    @GetMapping("/messages/chat/{receiverId}")
    public String initiateChat(@PathVariable(name = "receiverId") Long receiverId, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/user/login";
        }

        String senderUsername = authentication.getName();
        SiteUser sender = userService.findByUsername(senderUsername);
        SiteUser receiver = userService.getUserById(receiverId);

        boolean hasMessageHistory = messageService.hasMessageHistory(sender, receiver);

        if (!hasMessageHistory) {
            return "redirect:/messages/list";
        }

        model.addAttribute("sender", sender);
        model.addAttribute("receiver", receiver);
        model.addAttribute("chatUrl", "/chat/" + receiverId);
        return "chat/chat_room";
    }
}
