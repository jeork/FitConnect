package com.mysite.sbb.chat;

import java.time.LocalDateTime;

import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private SiteUser sender;

    @Column(nullable = false)
    private String senderUsername;
    private String content;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private SiteUser receiver;

    @Enumerated(EnumType.STRING)
    private MessageType type;
    private LocalDateTime sentAt;

    public enum MessageType {
        CHAT,
        JOIN,
        LEAVE
    }

    @PrePersist
    @PreUpdate
    private void ensureSenderUsername() {
        if (this.sender != null && (this.senderUsername == null || this.senderUsername.isEmpty())) {
            this.senderUsername = this.sender.getUsername();
        }
    }
}
