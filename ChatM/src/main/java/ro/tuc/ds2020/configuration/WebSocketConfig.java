package ro.tuc.ds2020.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import ro.tuc.ds2020.services.ChatServices;
import ro.tuc.ds2020.services.WebSocketService;

import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatServices chatServices;

    public WebSocketConfig(ChatServices chatServices) {
        this.chatServices = chatServices;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new WebSocketService(chatServices), "/conexiuneClient/{clientId}")
                .addInterceptors(new HandshakeInterceptor() {
                    @Override
                    public boolean beforeHandshake(
                            org.springframework.http.server.ServerHttpRequest request,
                            org.springframework.http.server.ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Map<String, Object> attributes) throws Exception {
                        // Extrage clientId din URL
                        String clientId = (String) request.getURI().getPath().split("/")[3];
                        attributes.put("clientId", clientId);
                        return true;
                    }

                    @Override
                    public void afterHandshake(
                            org.springframework.http.server.ServerHttpRequest request,
                            org.springframework.http.server.ServerHttpResponse response,
                            WebSocketHandler wsHandler,
                            Exception exception) {
                        // Nu avem nevoie să facem nimic aici
                    }
                })
                .setAllowedOrigins("*");
    }
}
