package org.dows.gpt.spark.listener;

import org.dows.gpt.spark.constant.StatusConstant;
import org.dows.gpt.spark.request.ChatRequest;
import org.dows.gpt.spark.response.ChatResponse;
import org.dows.gpt.spark.response.ChatSyncResponse;
import org.dows.gpt.spark.response.ChatUsage;
import okhttp3.Response;
import okhttp3.WebSocket;

import jakarta.validation.constraints.NotNull;

/**
 * 讯飞星火同步回答监听
 *
 */
public class SyncListener extends BaseListener {

    private final StringBuilder stringBuilder = new StringBuilder();

    private final ChatSyncResponse chatSyncResponse;

    public SyncListener(ChatSyncResponse chatSyncResponse) {
        this.chatSyncResponse = chatSyncResponse;
    }

    @Override
    public void onMessage(String content, ChatUsage usage, Integer status, ChatRequest chatRequest, ChatResponse chatResponse, WebSocket webSocket) {
        stringBuilder.append(content);
        if (StatusConstant.FINISH == status) {
            chatSyncResponse.setContent(stringBuilder.toString());
            chatSyncResponse.setTextUsage(usage.getText());
            chatSyncResponse.setOk(true);
        }
    }

    @Override
    public void onError(@NotNull WebSocket webSocket, @NotNull Throwable t, Response response) {
        chatSyncResponse.setErrTxt(t.getMessage());
        chatSyncResponse.setSuccess(false);
    }

}
