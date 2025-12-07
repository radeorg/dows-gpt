package org.dows.gpt.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class SseClient {

    // 广播通道（每个 topic 一个 sink）
    private final ConcurrentMap<String, Sinks.Many<ServerSentEvent<?>>> topicSinks = new ConcurrentHashMap<>();

    // 用户私有通道
    private final ConcurrentMap<String, Sinks.Many<ServerSentEvent<?>>> userSinks = new ConcurrentHashMap<>();
    
    // 用于跟踪topic的订阅者数量
    private final ConcurrentMap<String, Integer> topicSubscribers = new ConcurrentHashMap<>();

    /**
     * 获取主题的Flux流（不创建定时器，用于订阅）
     *
     * @param topic
     * @return
     */
    public Flux<ServerSentEvent<?>> getTopicFlux(String topic) {
        Sinks.Many<ServerSentEvent<?>> sink = topicSinks.computeIfAbsent(topic, t -> {
            log.info("Creating new topic sink for: {}", t);
            return Sinks.many().multicast().onBackpressureBuffer(1000, false); // 设置合理的缓冲区大小
        });

        // 增加订阅者计数
        topicSubscribers.compute(topic, (k, v) -> v == null ? 1 : v + 1);
        log.debug("New subscriber for topic {}, current count: {}", topic, topicSubscribers.get(topic));

        return sink.asFlux()
                .doFinally(signalType -> {
                    // 清理资源，减少订阅者计数
                    topicSubscribers.compute(topic, (k, v) -> v != null && v > 0 ? v - 1 : 0);
                    int count = topicSubscribers.getOrDefault(topic, 0);
                    log.debug("Subscriber disconnected from topic {}, remaining count: {}", topic, count);
                    
                    // 当没有订阅者时，清理sink资源
                    if (count == 0 && topicSinks.containsKey(topic)) {
                        log.info("No subscribers left, cleaning up topic sink: {}", topic);
                        topicSinks.remove(topic);
                        topicSubscribers.remove(topic);
                    }
                })
                .onErrorResume(error -> {
                    // 区分不同类型的错误
                    if (isClientDisconnectedError(error)) {
                        log.debug("Client disconnected for topic: {}", topic);
                    } else if (isSseConnectionError(error)) {
                        log.warn("SSE connection error for topic {}: {}", topic, error.getMessage());
                    } else {
                        log.error("Unexpected SSE stream error for topic: {}", topic, error);
                    }
                    return Flux.empty();
                })
                // 添加心跳消息，保持连接活跃
                .mergeWith(Flux.interval(Duration.ofSeconds(30))
                        .map(seq -> createHeartbeatEvent(topic, seq)))
                // 使用更灵活的背压控制
                .limitRate(10, 5) // 预取10个，下一批次在剩余5个时触发
                // 增加超时处理
                .timeout(Duration.ofSeconds(60), Flux.just(createTimeoutEvent(topic)));
    }

    /**
     * 向主题发送单条消息（用于推送）
     * @param topic
     * @param msg
     */
    public void publishToTopic(String topic, SseMessage msg) {
        if (msg == null) {
            log.warn("Attempted to publish null message to topic: {}", topic);
            return;
        }

        // 检查是否有订阅者，避免创建不必要的sink
        if (!topicSinks.containsKey(topic) && topicSubscribers.getOrDefault(topic, 0) == 0) {
            log.debug("No subscribers for topic {}, skipping message", topic);
            return;
        }

        Sinks.Many<ServerSentEvent<?>> sink = topicSinks.computeIfAbsent(topic, t ->
                Sinks.many().multicast().onBackpressureBuffer(1000, false)
        );

        try {
            ServerSentEvent<?> event = createEvent(msg, null);
            // 使用tryEmitNext更健壮，自动处理背压
            Sinks.EmitResult result = sink.tryEmitNext(event);

            // 处理不同的发送结果
            switch (result) {
                case OK:
                    log.debug("Successfully published message to topic: {}", topic);
                    break;
                case FAIL_NON_SERIALIZED:
                    // 如果是非序列化访问，尝试使用retry策略
                    log.warn("Non-serialized access for topic {}, will retry", topic);
                    try {
                        TimeUnit.MILLISECONDS.sleep(10); // 短暂延迟后重试
                        result = sink.tryEmitNext(event);
                        log.debug("Retry result for topic {}: {}", topic, result);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        log.warn("Retry interrupted for topic: {}", topic);
                    }
                    break;
                case FAIL_ZERO_SUBSCRIBER:
                    log.debug("No subscribers for topic: {}", topic);
                    break;
                case FAIL_OVERFLOW:
                    log.warn("Buffer overflow for topic {}, consider increasing buffer size or reducing message rate", topic);
                    break;
                default:
                    log.warn("Failed to publish message to topic {}: {}", topic, result);
            }
        } catch (Exception e) {
            // 捕获所有异常，防止影响其他操作
            log.error("Unexpected error publishing to topic {}: {}", topic, e.getMessage(), e);
        }
    }

    /**
     * 广播（多订阅者）
     *
     * @param topic
     * @param msg
     * @return
     */
    public Flux<ServerSentEvent<?>> broadcast(String topic, SseMessage msg) {
        Sinks.Many<ServerSentEvent<?>> sink = topicSinks.computeIfAbsent(topic, t ->
                Sinks.many().multicast().onBackpressureBuffer(1000, false)
        );

        // 自动推送数据（按 retry 时间间隔推送）
        Disposable disposable = Flux.interval(msg.getRetry())
                .map(seq -> createEvent(msg, seq))
                .doOnNext(event -> {
                    try {
                        Sinks.EmitResult result = sink.tryEmitNext(event);
                        if (result != Sinks.EmitResult.OK) {
                            log.warn("Emit result: {} for topic: {}", result, topic);
                        }
                    } catch (Exception e) {
                        log.warn("Error emitting event: {}", e.getMessage());
                    }
                })
                .onErrorContinue((e, o) -> log.warn("SSE broadcast interval error: {}", e.getMessage()))
                .retry(3) // 限制重试次数，避免无限重试
                .subscribe();

        // 注册关闭钩子，在应用关闭时清理资源
        Runtime.getRuntime().addShutdownHook(new Thread(disposable::dispose));

        // 返回订阅流，添加错误处理
        return getTopicFlux(topic);
    }

    /**
     * 发送给某个用户
     * @param userId
     * @param msg
     */
    public void sendToUser(String userId, SseMessage msg) {
        if (userId == null || msg == null) {
            log.warn("Invalid parameters for sendToUser");
            return;
        }

        // 检查是否有用户订阅
        if (!userSinks.containsKey(userId)) {
            log.debug("No user sink found for userId: {}, skipping message", userId);
            return;
        }

        try {
            userSinks.get(userId).tryEmitNext(createEvent(msg, null));
        } catch (Exception e) {
            log.error("Error sending message to user {}: {}", userId, e.getMessage(), e);
        }
    }


    /**
     * 用户订阅自己的通道
     * @param userId
     * @return
     */
    public Flux<ServerSentEvent<?>> subscribeToUserChannel(String userId) {
        if (userId == null) {
            return Flux.error(new IllegalArgumentException("User ID cannot be null"));
        }

        Sinks.Many<ServerSentEvent<?>> sink = userSinks.computeIfAbsent(userId, id ->
                // replay 10 条新用户可立即收到
                Sinks.many().replay().limit(10)
        );

        return sink.asFlux()
                .doFinally(signalType -> {
                    // 清理不再有订阅者的用户通道
                    if (userSinks.containsKey(userId)) {
                        log.debug("User {} disconnected, cleaning up resources", userId);
                        userSinks.remove(userId);
                    }
                })
                .onErrorResume(error -> {
                    log.warn("Error in user channel for {}: {}", userId, error.getMessage());
                    return Flux.empty();
                })
                .timeout(Duration.ofSeconds(60), Flux.empty());
    }


    /**
     * 创建心跳事件
     * @param topic
     * @param seq
     * @return
     */
    private ServerSentEvent<?> createHeartbeatEvent(String topic, Long seq) {
        return ServerSentEvent.builder()
                .comment("heartbeat")
                .id(topic + ":hb:" + seq)
                .event("heartbeat")
                .data("{\"status\":\"ok\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}")
                .build();
    }

    /**
     * 创建超时事件
     * @param topic
     * @return
     */
    private ServerSentEvent<?> createTimeoutEvent(String topic) {
        return ServerSentEvent.builder()
                .comment("timeout")
                .id(topic + ":timeout:" + System.currentTimeMillis())
                .event("timeout")
                .data("{\"status\":\"timeout\",\"timestamp\":\"" + System.currentTimeMillis() + "\"}")
                .build();
    }

    /**
     * Tools
     * @param message
     * @param seq
     * @return
     */
    private ServerSentEvent<?> createEvent(SseMessage message, Long seq) {
        ServerSentEvent.Builder<Object> builder = ServerSentEvent.builder();

        if (message.getComment() != null) {
            builder.comment(message.getComment());
        }

        builder.id(message.getId() != null ? message.getId() : String.valueOf(seq))
                .event(message.getEvent() != null ? message.getEvent() : "message")
                .data(message.getData());

        return builder.build();
    }

    /**
     * 判断是否为客户端断开连接错误
     * @param error
     * @return
     */
    private boolean isClientDisconnectedError(Throwable error) {
        if (error == null) {
            return false;
        }
        
        String message = error.getMessage();
        if (message != null) {
            // 检查常见的客户端断开连接的错误消息
            return message.contains("你的主机中的软件中止了一个已建立的连接") ||
                   message.contains("Connection reset") ||
                   message.contains("Broken pipe") ||
                   message.contains("Client closed connection");
        }
        
        return error instanceof java.io.IOException;
    }

    /**
     * 判断是否为SSE连接错误
     * @param error
     * @return
     */
    private boolean isSseConnectionError(Throwable error) {
        if (error == null) {
            return false;
        }
        
        String message = error.getMessage();
        if (message != null) {
            return message.contains("text/event-stream") ||
                   message.contains("SSE") ||
                   message.contains("Server-Sent Events");
        }
        
        return false;
    }
}