package org.dows.gpt;

import org.dows.gpt.api.ChatApi;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = GptApplication.class)
public class TestSse {


    @Autowired
    private ChatApi chatApi;

    @Test
    public void testSse() {
        chatApi.streamChat("请问你是谁");

    }


}
