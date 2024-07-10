package dbp.connect.Configuracion;

import dbp.connect.User.Domain.UserService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.HandshakeInterceptor;

import org.slf4j.Logger;

import java.util.Map;

@Configuration
public class HandShakeInterceptor implements HandshakeInterceptor, WebSocketHandler {
    @Autowired
    private UserService userService;

    private final Logger log = LoggerFactory.getLogger(HandShakeInterceptor.class);

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String authToken = request.getHeaders().getFirst("Authorization");
        if (authToken == null || authToken.isEmpty()) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return false;
        }
        return true;
    }
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception){
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    }
    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    }
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        log.info("Sesión cerrada : " + session.getId());
    }
    @Override
    public boolean supportsPartialMessages() {
        return false;
    }
}
