package com.wt.walkietalkie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.socket.config.annotation.EnableWebSocket;

@SpringBootApplication
@EnableWebSocket
public class WalkieTalkieApplication {

    public static void main(String[] args) {
        SpringApplication.run(WalkieTalkieApplication.class, args);
    }

}
