package com.mysite.sbb.message;

import java.time.LocalDateTime;

import com.mysite.sbb.user.SiteUser;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private SiteUser sender;

    @ManyToOne
    private SiteUser receiver;

    private String content;
    private LocalDateTime sentAt;

    private boolean isRead = false;

    @Transient
    private boolean hasMessageHistory;
}