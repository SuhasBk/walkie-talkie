package com.wt.walkietalkie.wsconfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.config.annotation.WebSocketTransportRegistration;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
public class WebSocketConfig implements WebSocketConfigurer {
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketHandler(), "/communicate")
                .setAllowedOrigins("*");
    }


    @Bean
    public WebSocketTransportRegistration webSocketTransportRegistration() {
        return new WebSocketTransportRegistration()
                .setSendTimeLimit(15 * 1000)
                .setSendBufferSizeLimit(4096 * 4096)
                .setMessageSizeLimit(4096 * 4096);
    }

    @Bean
    public ServletServerContainerFactoryBean createWebSocketContainer() {
        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
        container.setMaxTextMessageBufferSize(4096 * 4096);
        container.setMaxBinaryMessageBufferSize(4096 * 4096);
        return container;
    }

}
