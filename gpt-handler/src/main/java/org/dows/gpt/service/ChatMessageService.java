package org.dows.gpt.service;

import org.dows.gpt.entity.ChatMessageEntity;
import org.dows.gpt.request.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatMessageService {

    public List<ChatMessage> findLastMessages(String id, int i) {
        return null;
    }

    public void insert(ChatMessageEntity chatMessageDO) {

    }
}
