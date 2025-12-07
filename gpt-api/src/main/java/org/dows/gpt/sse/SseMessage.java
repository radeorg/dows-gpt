package org.dows.gpt.sse;

import java.time.Duration;

public interface SseMessage {

    String getId();

    String getEvent();

    Duration getRetry();

    String getComment();

    Object getData();
}
