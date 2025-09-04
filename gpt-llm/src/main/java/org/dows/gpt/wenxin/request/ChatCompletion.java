package org.dows.gpt.wenxin.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;

/**
 * 聊天信息
 *
 */
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class ChatCompletion implements Serializable {
    @NonNull
    private List<ChatCompletionMessage> messages;

    @Builder.Default
    private Float temperature = 0.2f;

    @JsonProperty("top_p")
    @Builder.Default
    private Float topP = 1.0f;

    @JsonProperty("penalty_score")
    @Builder.Default
    private Float penaltyScore = 1.0f;

    @Builder.Default
    private Boolean stream = false;

    @JsonProperty("user_id")
    private String userId;
}


