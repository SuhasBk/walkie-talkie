package com.wt.walkietalkie.wsconfig;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Log4j2
public class WebSocketHandler extends BinaryWebSocketHandler {

    private ConcurrentHashMap<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        log.info("New connection established - Local: {}. Remote: {}", session.getLocalAddress(), session.getRemoteAddress());
        this.sessions.put(session.getId(), session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return true;
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        String sessionId = session.getId();

        this.sessions.forEach((k, v) -> {
            if (v.isOpen()) {
                try {
                    if (k.equals(sessionId)) {
                        v.sendMessage(new BinaryMessage("OK".getBytes()));
                        log.info("ACK sent to - {}", sessionId);
                    } else {
                        v.sendMessage(message);
                        log.info("Audio sent to recipient - {}", k);
                    }
                } catch (IOException e) {
                    this.sessions.remove(k);
                    log.error("Error sending message to session {}", k, e);
                }
            } else {
                log.warn("Clearing stale session {}", k);
                this.sessions.remove(k);
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) {
        if (closeStatus.equals(CloseStatus.NORMAL)) {
            System.out.println("Normal close status");
        } else if (closeStatus.equals(CloseStatus.GOING_AWAY)) {
            System.out.println("Going away");
        } else if (closeStatus.equals(CloseStatus.PROTOCOL_ERROR)) {
            System.out.println("Protocol error");
        } else if (closeStatus.equals(CloseStatus.NOT_ACCEPTABLE)) {
            System.out.println("Not acceptable");
        } else if (closeStatus.equals(CloseStatus.NO_STATUS_CODE)) {
            System.out.println("No status code");
        } else if (closeStatus.equals(CloseStatus.NO_CLOSE_FRAME)) {
            System.out.println("No close frame");
        } else if (closeStatus.equals(CloseStatus.BAD_DATA)) {
            System.out.println("Bad data");
        } else if (closeStatus.equals(CloseStatus.POLICY_VIOLATION)) {
            System.out.println("Policy violation");
        } else if (closeStatus.equals(CloseStatus.TOO_BIG_TO_PROCESS)) {
            System.out.println("Too big to process");
        } else if (closeStatus.equals(CloseStatus.REQUIRED_EXTENSION)) {
            System.out.println("Required extension");
        } else if (closeStatus.equals(CloseStatus.SERVER_ERROR)) {
            System.out.println("Server error");
        } else if (closeStatus.equals(CloseStatus.SERVICE_RESTARTED)) {
            System.out.println("Service restarted");
        } else if (closeStatus.equals(CloseStatus.SERVICE_OVERLOAD)) {
            System.out.println("Service overload");
        } else if (closeStatus.equals(CloseStatus.TLS_HANDSHAKE_FAILURE)) {
            System.out.println("TLS handshake failure");
        } else if (closeStatus.equals(CloseStatus.SESSION_NOT_RELIABLE)) {
            System.out.println("Session not reliable: " + closeStatus.getReason());
        } else {
            System.out.println("Unknown close status");
        }
        log.info("Connection closed due to - {}. Code: {}", closeStatus.getReason(), closeStatus.getCode());
        this.sessions.remove(session.getId());
    }
}
