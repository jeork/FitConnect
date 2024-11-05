package com.mysite.sbb.chat;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // '/ws'는 클라이언트가 WebSocket 연결을 수립할 때 사용하는 엔드포인트입니다.
        registry.addEndpoint("/ws").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // '/app'으로 시작하는 메시지는 서버로 전달됩니다.
        config.setApplicationDestinationPrefixes("/app");

        // '/topic'으로 시작하는 메시지는 구독자들에게 브로커를 통해 전달됩니다.
        config.enableSimpleBroker("/topic");
    }
}
