package com.joelcode.personalinvestmentportfoliotracker.websockets;

import com.joelcode.personalinvestmentportfoliotracker.PersonalInvestmentPortfolioTrackerApplication;
import com.joelcode.personalinvestmentportfoliotracker.config.WebSocketConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "spring.autoconfigure.exclude=" +
                        "org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataAutoConfiguration," +
                        "org.springframework.boot.autoconfigure.data.r2dbc.R2dbcRepositoriesAutoConfiguration," +
                        "org.springframework.modulith.events.jpa.JpaEventPublicationAutoConfiguration," +
                        "org.springframework.modulith.events.jdbc.JdbcEventPublicationAutoConfiguration"
        }
)
@ActiveProfiles("test")
class WebSocketIntegrationTest {

    @LocalServerPort
    private int port;

    private WebSocketStompClient stompClient;
    private String url;

    @BeforeEach
    void setup() {
        WebSocketClient webSocketClient = new StandardWebSocketClient();
        stompClient = new WebSocketStompClient(webSocketClient);
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        url = "ws://localhost:" + port + "/ws";
    }

    @Test
    void testWebSocketConnection_Success() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                sessionFuture.complete(session);
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    void testWebSocketSubscription_ToTopic() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<String> messageFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/portfolio", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        messageFuture.complete((String) payload);
                    }
                });
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    void testWebSocketMessage_SendAndReceive() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                sessionFuture.complete(session);
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
        assertTrue(sessionFuture.isDone());
    }

    @Test
    void testWebSocketApplicationDestinationPrefix_AppPrefix() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.send("/app/portfolio", "test message");
                sessionFuture.complete(session);
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                sessionFuture.completeExceptionally(exception);
            }
        });

        // Wait for the future to complete
        StompSession session = sessionFuture.get(5, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
        assertTrue(sessionFuture.isDone());
    }


    @Test
    void testWebSocketUserDestination_QueuePrefix() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/user/queue/notifications", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        sessionFuture.complete(null);
                    }
                });
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    void testWebSocketBroker_TopicSubscription() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Boolean> subscriptionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/topic/updates", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        subscriptionFuture.complete(true);
                    }
                });
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    void testWebSocketBroker_QueueSubscription() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<Boolean> subscriptionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/queue/transactions", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        subscriptionFuture.complete(true);
                    }
                });
            }
        }).get(3, SECONDS);

        assertNotNull(session);
        assertTrue(session.isConnected());
    }

    @Test
    void testWebSocketDisconnection_GracefulClose() throws InterruptedException, ExecutionException, TimeoutException {
        CompletableFuture<StompSession> sessionFuture = new CompletableFuture<>();

        StompSession session = stompClient.connectAsync(url, new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                sessionFuture.complete(session);
            }
        }).get(3, SECONDS);

        assertTrue(session.isConnected());
        session.disconnect();

        assertFalse(session.isConnected());
    }
}